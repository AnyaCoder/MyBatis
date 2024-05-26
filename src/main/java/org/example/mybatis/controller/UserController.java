package org.example.mybatis.controller;
import org.example.mybatis.entity.User;
import org.example.mybatis.service.AsyncUserService;
import org.example.mybatis.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.concurrent.CompletableFuture;
import java.util.List;

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
                        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                    }
                });
    }

    @PostMapping("/async")
    public CompletableFuture<ResponseEntity<String>> insertUserAsync(@RequestBody User user) {
        return asyncUserService.addNewUserProcedure(user.getUserID(), user.getUsername(), user.getPassword(), user.getEmail(), user.getPhoneNumber(), user.getGender())
                .thenApply(aVoid -> new ResponseEntity<>("User added successfully", HttpStatus.CREATED));
    }

    // 删除用户（异步）
    @DeleteMapping("/async/{userId}")
    public CompletableFuture<ResponseEntity<String>> deleteUserAsync(@PathVariable("userId") Long userId) {
        return asyncUserService.deleteUserProcedure(userId)
                .thenApply(aVoid -> new ResponseEntity<>("User deleted successfully", HttpStatus.NO_CONTENT));
    }

    // 更新用户信息（异步）
    @PutMapping("/async/{userId}")
    public CompletableFuture<ResponseEntity<String>> updateUserAsync(@PathVariable("userId") Long userId,
                                                                     @RequestBody User user) {
        return asyncUserService.updateUserInfoProcedure(userId, user.getEmail(), user.getPhoneNumber())
                .thenApply(aVoid -> new ResponseEntity<>("User updated successfully", HttpStatus.OK));
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
