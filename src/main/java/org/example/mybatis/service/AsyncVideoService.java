package org.example.mybatis.service;

import org.example.mybatis.entity.User;
import org.example.mybatis.entity.Video;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface AsyncVideoService {
    CompletableFuture<List<Video>> getUserVideosProcedure(int userId);
    CompletableFuture<Void> addNewVideoProcedure(int videoId, int userID, String title, String description, String videoPath);
    CompletableFuture<Void> deleteVideoProcedure(int userId);
    CompletableFuture<Void> updateVideoInfoProcedure(int videoId, String title, String description, String videoPath);

}
