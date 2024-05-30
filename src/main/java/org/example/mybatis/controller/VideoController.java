package org.example.mybatis.controller;
import org.example.mybatis.entity.Video;
import org.example.mybatis.service.AsyncVideoService;
import org.example.mybatis.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import java.util.List;

@RestController
@RequestMapping("videos")
public class VideoController {

    @Autowired
    private VideoService videoService;

    @Autowired
    private AsyncVideoService asyncVideoService;

    private static final String UPLOAD_DIR;

    private static final Logger logger = LoggerFactory.getLogger(VideoController.class);

    static {
        // Get the root path of the project
        String projectRootPath = System.getProperty("user.dir");
        // Define the upload directory relative to the project root
        UPLOAD_DIR = Paths.get(projectRootPath, "UploadedVideos").toString();
        // Create the directory if it does not exist
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
    }

    @PostMapping("/async/upload")
    public ResponseEntity<?> uploadMedia(
            @RequestParam("userID") String userID,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("filename") String filename,
            @RequestParam("fileData") String fileData) {
        try {
            logger.info("Received data: userID={}, title={}, description={}, filename={}, fileData={}",
                    userID, title, description, filename, fileData.length());
            // Decode Base64 encoded file data
            byte[] decodedBytes = Base64.getDecoder().decode(fileData);

            // Save the file
            String filePath = Paths.get(UPLOAD_DIR, filename).toString();
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                fos.write(decodedBytes);
            }

            // Save video details to the database
            Video video = new Video();
            video.setUserID(Long.parseLong(userID));
            video.setVideoPath(filePath);
            video.setTitle(title);
            video.setDescription(description);
            asyncVideoService.addNewVideoProcedure(video.getUserID(), video.getTitle(), video.getDescription(), video.getVideoPath());

            return ResponseEntity.status
                    (HttpStatus.CREATED).body("{\"msg\": \"Upload successful\"}");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"msg\": \"Upload failed\"}");
        }
    }


    @PostMapping("/async")
    public CompletableFuture<ResponseEntity<Video>> insertVideoAsync(@RequestBody Video video) {
        return asyncVideoService.addNewVideoProcedure(video.getUserID(), video.getTitle(), video.getDescription(), video.getVideoPath())
                .thenApply(newVideo -> {
                    if (newVideo != null) {
                        return new ResponseEntity<>(newVideo, HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                });
    }

    @DeleteMapping("/async/{videoId}")
    public CompletableFuture<ResponseEntity<String>> deleteVideoAsync(@PathVariable("videoId") Long videoId) {
        return asyncVideoService.deleteVideoProcedure(videoId)
                .thenApply(aVoid -> new ResponseEntity<>("{\"msg\": \"Video deleted successfully\"}", HttpStatus.NO_CONTENT));
    }

    @PutMapping("/async/{videoId}")
    public CompletableFuture<ResponseEntity<String>> updateVideoInfoAsync(
            @PathVariable("videoId") Long videoId,
            @RequestBody Video video) {
        return asyncVideoService.updateVideoInfoProcedure(videoId, video.getTitle(), video.getDescription(), video.getVideoPath())
                .thenApply(aVoid -> new ResponseEntity<>("{\"msg\": \"Video updated successfully\"}", HttpStatus.OK));
    }

    @GetMapping("/async/{userId}")
    public CompletableFuture<ResponseEntity<List<Video>>> getUserVideosAsync(@PathVariable("userId") Long userId) {
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

    @DeleteMapping("/async/{videoId}/view")
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
