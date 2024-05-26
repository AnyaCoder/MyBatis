package org.example.mybatis.controller;

import org.example.mybatis.entity.Follows;
import org.example.mybatis.service.AsyncFollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/follows")
public class FollowController {

    @Autowired
    private AsyncFollowService asyncFollowService;

    @PostMapping("/async")
    public CompletableFuture<ResponseEntity<String>> insertNewFollow(
            @RequestBody Follows follows) {
        return asyncFollowService.insertNewFollow(follows.getUserId(), follows.getFollowedUserId())
                .thenApply(aVoid -> new ResponseEntity<>("Follows added successfully", HttpStatus.CREATED));
    }

    @DeleteMapping("/async")
    public CompletableFuture<ResponseEntity<String>> deleteFollow(
            @RequestBody Follows follows) {
        return asyncFollowService.deleteFollow(follows.getUserId(), follows.getFollowedUserId())
                .thenApply(aVoid -> new ResponseEntity<>("Follows deleted successfully", HttpStatus.NO_CONTENT));
    }

    @GetMapping("/async/followers/{userId}")
    public CompletableFuture<ResponseEntity<List<String>>> getFollowers(@PathVariable("userId") Long userId) {
        return asyncFollowService.getFollowers(userId)
                .thenApply(followers -> ResponseEntity.ok(followers));
    }

    @GetMapping("/async/followedUsers/{userId}")
    public CompletableFuture<ResponseEntity<List<String>>> getFollowedUsers(@PathVariable("userId") Long userId) {
        return asyncFollowService.getFollowedUsers(userId)
                .thenApply(followedUsers -> ResponseEntity.ok(followedUsers));
    }
}
