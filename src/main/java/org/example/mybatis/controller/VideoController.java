package org.example.mybatis.controller;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.example.mybatis.entity.Video;
import org.example.mybatis.service.AsyncVideoService;
import org.example.mybatis.service.VideoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("videos")
public class VideoController {

    @Autowired
    private VideoService videoService;

    @Autowired
    private AsyncVideoService asyncVideoService;

    public static final String UPLOAD_DIR;
    public static final String THUMBNAIL_DIR;

    private static final Logger logger = LoggerFactory.getLogger(VideoController.class);

    static {
        // Get the root path of the project
        String projectRootPath = System.getProperty("user.dir");
        // Define the upload directory relative to the project root
        UPLOAD_DIR = Paths.get(projectRootPath, "UploadedVideos").toString();
        THUMBNAIL_DIR = Paths.get(projectRootPath, "Thumbnails").toString();

        // Create the directories if they do not exist
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        File thumbnailDir = new File(THUMBNAIL_DIR);
        if (!thumbnailDir.exists()) {
            thumbnailDir.mkdirs();
        }
    }

    @PostMapping("/async/upload")
    @Async
    public CompletableFuture<ResponseEntity<?>> uploadMedia(
            @RequestParam("userID") String userID,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("filename") String filename,
            @RequestParam("fileData") String fileData) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("Received [upload]: userID={}, title={}, description={}, filename={}, fileData={}",
                        userID, title, description, filename, fileData.length());
                // Decode Base64 encoded file data
                byte[] decodedBytes = Base64.getDecoder().decode(fileData);

                // Save the file
                String filePath = Paths.get(UPLOAD_DIR, filename).toString();
                try (FileOutputStream fos = new FileOutputStream(filePath)) {
                    fos.write(decodedBytes);
                }
                // Generate thumbnail
                String thumbnailPath = generateThumbnail(new File(filePath), filename);
                // Save video details to the database
                Video video = new Video();
                video.setUserID(Long.parseLong(userID));
                video.setVideoPath(filename);
                video.setTitle(title);
                video.setDescription(description);
                video.setThumbnailPath(filename + ".png");
                asyncVideoService.addNewVideoProcedure(video.getUserID(), video.getTitle(), video.getDescription(), video.getVideoPath(), video.getThumbnailPath());

                return ResponseEntity.status(HttpStatus.CREATED).body("{\"msg\": \"Upload successful\"}");
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"msg\": \"Upload failed\"}");
            }
        });
    }

    private String generateThumbnail(File videoFile, String filename) {
        FFmpegFrameGrabber frameGrabber = null;
        try {
            frameGrabber = new FFmpegFrameGrabber(videoFile);
            frameGrabber.start();

            Frame frame = null;
            for (int i = 0; i < frameGrabber.getLengthInFrames(); i++) {
                frame = frameGrabber.grabFrame();
                if (frame != null && frame.image != null) {
                    break;
                }
            }

            if (frame == null || frame.image == null) {
                throw new IOException("Could not grab a valid frame from the video file");
            }

            // Convert the frame to BufferedImage
            Java2DFrameConverter converter = new Java2DFrameConverter();
            BufferedImage bufferedImage = converter.convert(frame);

            if (bufferedImage == null) {
                throw new IOException("Could not convert frame to BufferedImage");
            }

            // Save the thumbnail to the server
            String thumbnailFilename = filename + ".png";
            String thumbnailPath = Paths.get(THUMBNAIL_DIR, thumbnailFilename).toString();
            File thumbnailFile = new File(thumbnailPath);
            ImageIO.write(bufferedImage, "png", thumbnailFile);

            return thumbnailPath;
        } catch (IOException e) {
            logger.error("Failed to generate thumbnail for video file {}: {}", videoFile.getName(), e.getMessage());
            return null;
        } finally {
            if (frameGrabber != null) {
                try {
                    frameGrabber.stop();
                } catch (FFmpegFrameGrabber.Exception e) {
                    logger.error("Failed to stop frame grabber: {}", e.getMessage());
                }
            }
        }
    }

    @PostMapping("/async")
    public CompletableFuture<ResponseEntity<Video>> insertVideoAsync(@RequestBody Video video) {
        logger.info("Received [insert]: userID={}, title={}, description={}",
                video.getUserID(), video.getTitle(), video.getDescription());
        return asyncVideoService.addNewVideoProcedure(video.getUserID(), video.getTitle(), video.getDescription(), video.getVideoPath(), video.getThumbnailPath())
                .thenApply(newVideo -> {
                    if (newVideo != null) {
                        return new ResponseEntity<>(newVideo, HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                });
    }

    @DeleteMapping("/async/{videoId}")
    public CompletableFuture<ResponseEntity<String>> deleteVideoAsync(
            @PathVariable("videoId") Long videoId,
            @RequestParam(value = "videoPath", required = false) @Nullable String videoPath,
            @RequestParam(value = "thumbnailPath", required = false) @Nullable String thumbnailPath
    ) {
        logger.info("Received [delete]: video={}, videoPath={}, thumbnailPath={}",
                videoId, videoPath, thumbnailPath);
        return asyncVideoService.deleteVideoProcedure(videoId, videoPath, thumbnailPath)
                .thenApply(aVoid -> new ResponseEntity<>("{\"msg\": \"Video deleted successfully\"}", HttpStatus.NO_CONTENT));
    }

    @PutMapping("/async/{videoId}")
    public CompletableFuture<ResponseEntity<String>> updateVideoInfoAsync(
            @PathVariable("videoId") Long videoId,
            @RequestBody Video video) {
        logger.info("Received [update]: videoID={}, title={}, description={}",
                videoId, video.getTitle(), video.getDescription());
        return asyncVideoService.updateVideoInfoProcedure(videoId, video.getTitle(), video.getDescription(), video.getVideoPath())
                .thenApply(aVoid -> new ResponseEntity<>("{\"msg\": \"Video updated successfully\"}", HttpStatus.OK));
    }

    @GetMapping("/async/{userId}")
    public CompletableFuture<ResponseEntity<List<Video>>> getUserVideosAsync(@PathVariable("userId") Long userId) {
        logger.info("Received [access]: userID={}", userId);
        return asyncVideoService.getUserVideosProcedure(userId)
                .thenApply(videos -> {
                    if (videos != null) {
                        return new ResponseEntity<>(videos, HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                    }
                });
    }

    @PostMapping("/async/{videoId}/view")
    public CompletableFuture<Void> addView(@PathVariable("videoId") Long videoId) {
        return asyncVideoService.incrementViews(videoId);
    }

    @PostMapping("/async/{videoId}/like")
    public CompletableFuture<Void> addLike(@PathVariable("videoId") Long videoId) {
        return asyncVideoService.incrementLikes(videoId);
    }

    @DeleteMapping("/async" +
            "" +
            "/{videoId}/view")
    public CompletableFuture<Void> deleteView(@PathVariable("videoId") Long videoId) {
        return asyncVideoService.decrementViews(videoId);
    }

    @DeleteMapping("/async/{videoId}/like")
    public CompletableFuture<Void> deleteLike(@PathVariable("videoId") Long videoId) {
        return asyncVideoService.decrementLikes(videoId);
    }

    // 查询所有用户
    @GetMapping
    public ResponseEntity<List<Video>> listAllVideos() {
        List<Video> videos = videoService.listAllVideos();
        return new ResponseEntity<>(videos, HttpStatus.OK);
    }

    // 通过 ID 查询单个用户
    @GetMapping("/{videoId}")
    public ResponseEntity<Video> getVideoByVideoId(@PathVariable("videoId") Long videoId) {
        Video video = videoService.getVideoByVideoId(videoId);
        if (video != null) {
            return new ResponseEntity<>(video, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // 插入新用户
    @PostMapping
    public ResponseEntity<Video> insertVideo(@RequestBody Video video) {
        videoService.insertVideo(video);
        return new ResponseEntity<>(video, HttpStatus.CREATED);
    }

    // 更新用户信息
    @PutMapping("/{videoId}")
    public ResponseEntity<Video> updateVideo(@PathVariable("videoId") Long videoId, @RequestBody Video video) {
        video.setVideoID(videoId);
        Video updatedVideo = videoService.updateVideo(video);
        if (updatedVideo != null) {
            return new ResponseEntity<>(updatedVideo, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // 删除用户
    @DeleteMapping("/{videoId}")
    public ResponseEntity<Void> deleteVideo(@PathVariable("videoId") Long videoId) {
        boolean isDeleted = videoService.deleteVideo(videoId);
        if (isDeleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
