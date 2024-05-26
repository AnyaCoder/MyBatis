package org.example.mybatis.controller;
import org.example.mybatis.entity.User;
import org.example.mybatis.entity.Video;
import org.example.mybatis.service.AsyncVideoService;
import org.example.mybatis.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.concurrent.CompletableFuture;
import java.util.List;

@RestController
@RequestMapping("videos")
public class VideoController {

    @Autowired
    private VideoService videoService;

    @Autowired
    private AsyncVideoService asyncVideoService;

    @PostMapping("/async")
    public CompletableFuture<ResponseEntity<String>> insertVideoAsync(@RequestBody Video video) {
        return asyncVideoService.addNewVideoProcedure(video.getVideoID(), video.getUserID(), video.getTitle(), video.getDescription(), video.getVideoPath())
                .thenApply(aVoid -> new ResponseEntity<>("Video added successfully", HttpStatus.CREATED));
    }


    @DeleteMapping("/async/{videoId}")
    public CompletableFuture<ResponseEntity<String>> deleteVideoAsync(@PathVariable("videoId") int videoId) {
        return asyncVideoService.deleteVideoProcedure(videoId)
                .thenApply(aVoid -> new ResponseEntity<>("Video deleted successfully", HttpStatus.NO_CONTENT));
    }

    @PutMapping("/async/{videoId}")
    public CompletableFuture<ResponseEntity<String>> updateVideoInfoAsync(
            @PathVariable("videoId") int videoId,
            @RequestBody Video video) {
        return asyncVideoService.updateVideoInfoProcedure(videoId, video.getTitle(), video.getDescription(), video.getVideoPath())
                .thenApply(aVoid -> new ResponseEntity<>("Video updated successfully", HttpStatus.OK));
    }

    @GetMapping("/async/{userId}")
    public CompletableFuture<ResponseEntity<List<Video>>> getUserVideosAsync(@PathVariable("userId") int userId) {
        return asyncVideoService.getUserVideosProcedure(userId)
                .thenApply(videos -> {
                    if (videos != null) {
                        return new ResponseEntity<>(videos, HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                    }
                });
    }


    // 查询所有用户
    @GetMapping
    public ResponseEntity<List<Video>> listAllVideos() {
        List<Video> videos = videoService.listAllVideos();
        return new ResponseEntity<>(videos, HttpStatus.OK);
    }

    // 通过 ID 查询单个用户
    @GetMapping("/{videoId}")
    public ResponseEntity<Video> getVideoByVideoId(@PathVariable("videoId") int videoId) {
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
    public ResponseEntity<Video> updateVideo(@PathVariable("videoId") int videoId, @RequestBody Video video) {
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
    public ResponseEntity<Void> deleteVideo(@PathVariable("videoId") int videoId) {
        boolean isDeleted = videoService.deleteVideo(videoId);
        if (isDeleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
