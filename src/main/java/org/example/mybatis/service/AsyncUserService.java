package org.example.mybatis.service;

import org.example.mybatis.entity.User;

import java.util.concurrent.CompletableFuture;

public interface AsyncUserService {
    CompletableFuture<User> getUserProcedure(Long userId);
    CompletableFuture<Void> addNewUserProcedure(Long userId, String username, String password, String email, String phoneNumber, int gender);
    CompletableFuture<Void> deleteUserProcedure(Long userId);
    CompletableFuture<Void> updateUserInfoProcedure(Long userId, String email, String phoneNumber);
}
