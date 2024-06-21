package org.example.mybatis.controller;

import org.example.mybatis.entity.Admin;
import org.example.mybatis.entity.User;
import org.example.mybatis.entity.UserStats;
import org.example.mybatis.service.AsyncUserService;
import org.example.mybatis.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AsyncUserService asyncUserService;


    @GetMapping("/async/{userId}")
    public CompletableFuture<ResponseEntity<User>> getUserAsync(@PathVariable("userId") Long userId) {
        return asyncUserService.getUserProcedure(userId)
                .thenApply(user -> {
                    if (user != null) {
                        return new ResponseEntity<>(user, HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                });
    }


    @GetMapping("/async/stats/{userId}")
    public CompletableFuture<ResponseEntity<UserStats>> getUserStatsAsync(@PathVariable("userId") Long userId) {
        return asyncUserService.getUserStats(userId)
                .thenApply(userStats -> {
                    if (userStats != null) {
                        return new ResponseEntity<>(userStats, HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                });
    }

    @PostMapping("/async/login")
    public CompletableFuture<User> login(@RequestBody User user) {
        return asyncUserService.getUserByPhoneNumber(user.getPhoneNumber(), user.getPassword())
                .thenApply(users -> {
                    if (users.size() == 1) {
                        return users.get(0); // 返回匹配的唯一用户
                    } else if (users.size() > 1) {
                        throw new RuntimeException("Multiple users found with the same credentials");
                    } else {
                        throw new RuntimeException("No user found with the given credentials");
                    }
                });
    }


    @PostMapping("/async")
    public CompletableFuture<ResponseEntity<User>> insertUserAsync(@RequestBody User user) {
        return asyncUserService.addNewUserProcedure(user.getUsername(), user.getPassword(), user.getEmail(), user.getPhoneNumber(), user.getGender())
                .thenApply(newUser -> {
                    if (newUser != null) {
                        return new ResponseEntity<>(newUser, HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                    }
                });
    }

    // 删除用户（异步）
    @DeleteMapping("/async/{userId}")
    public CompletableFuture<ResponseEntity<String>> deleteUserAsync(@PathVariable("userId") Long userId) {
        return asyncUserService.deleteUserProcedure(userId)
                .thenApply(aVoid -> new ResponseEntity<>("{\"msg\": \"User deleted successfully\"}", HttpStatus.NO_CONTENT));
    }

    // 更新用户信息（异步）
    @PutMapping("/async/{userId}")
    public CompletableFuture<ResponseEntity<String>> updateUserAsync(@PathVariable("userId") Long userId,
                                                                     @RequestBody User user) {
        return asyncUserService.updateUserInfoProcedure(userId, user.getUsername(), user.getPassword(), user.getEmail(), user.getPhoneNumber(), user.getGender())
                .thenApply(aVoid -> new ResponseEntity<>("{\"msg\": \"User updated successfully\"}", HttpStatus.OK));
    }

    // 查询所有用户
    @GetMapping
    public ResponseEntity<List<User>> listAllUsers() {
        List<User> users = userService.listAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    // 通过 ID 查询单个用户
    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable("userId") Long userId) {
        User user = userService.getUserById(userId);
        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // 插入新用户
    @PostMapping
    public ResponseEntity<User> insertUser(@RequestBody User user) {
        userService.insertUser(user);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    // 更新用户信息
    @PutMapping("/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable("userId") Long userId, @RequestBody User user) {
        user.setUserID(userId);
        User updatedUser = userService.updateUser(user);
        if (updatedUser != null) {
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // 删除用户
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") Long userId) {
        boolean isDeleted = userService.deleteUser(userId);
        if (isDeleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
