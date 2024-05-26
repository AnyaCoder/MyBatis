package org.example.mybatis.service;

import org.example.mybatis.entity.Video;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface AsyncVideoService {
    CompletableFuture<List<Video>> getUserVideosProcedure(Long userId);
    CompletableFuture<Void> addNewVideoProcedure(Long videoId, Long userID, String title, String description, String videoPath);
    CompletableFuture<Void> deleteVideoProcedure(Long userId);
    CompletableFuture<Void> updateVideoInfoProcedure(Long videoId, String title, String description, String videoPath);
}