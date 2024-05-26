package org.example.mybatis.service;

import org.example.mybatis.entity.User;

import java.util.concurrent.CompletableFuture;

public interface AsyncUserService {
    CompletableFuture<User> getUserProcedure(int userId);
    CompletableFuture<Void> addNewUserProcedure(int userId, String username, String password, String email, String phoneNumber, int gender);
    CompletableFuture<Void> deleteUserProcedure(int userId);
    CompletableFuture<Void> updateUserInfoProcedure(int userId, String email, String phoneNumber);
}
