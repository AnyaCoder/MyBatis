package org.example.mybatis.service;

import org.example.mybatis.entity.User;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface UserService {
    List<User> listAllUsers();
    User getUserById(Long userId);
    void insertUser(User user);
    User updateUser(User user);
    boolean deleteUser(Long userId);
}
