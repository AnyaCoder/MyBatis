package org.example.mybatis.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface AsyncFollowService {
    CompletableFuture<Void> insertNewFollow(Long userId, Long followedUserId);
    CompletableFuture<Void> deleteFollow(Long userId, Long followedUserId);
    CompletableFuture<List<String>> getFollowers(Long userId);
    CompletableFuture<List<String>> getFollowedUsers(Long userId);
}
