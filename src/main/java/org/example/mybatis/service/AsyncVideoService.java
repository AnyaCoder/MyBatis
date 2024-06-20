package org.example.mybatis.service;

import org.example.mybatis.entity.Video;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface AsyncVideoService {
    CompletableFuture<List<Video>> getUserVideosProcedure(Long userId);

    CompletableFuture<Video> addNewVideoProcedure(Long userID, String title, String description, String videoPath, String thumbnailPath);

    CompletableFuture<Void> deleteVideoProcedure(Long userId, @Nullable String videoPath, @Nullable String thumbnailPath);

    CompletableFuture<Void> updateVideoInfoProcedure(Long videoId, String title, String description, String videoPath);

    CompletableFuture<Void> incrementViews(Long videoId);

    CompletableFuture<Void> incrementLikes(Long videoId);

    CompletableFuture<Void> decrementViews(Long videoId);

    CompletableFuture<Void> decrementLikes(Long videoId);
}