package org.example.mybatis.controller;

import org.example.mybatis.entity.Comment;
import org.example.mybatis.entity.CommentInfo;
import org.example.mybatis.service.AsyncCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private AsyncCommentService asyncCommentService;

    @PostMapping("/async")
    public CompletableFuture<ResponseEntity<String>> insertNewComment(
            @RequestBody Comment comment) {
        return asyncCommentService.insertNewComment(comment.getVideoID(), comment.getUserID(), comment.getContent())
                .thenApply(aVoid -> new ResponseEntity<>("{\"msg\": \"Comment added successfully\"}", HttpStatus.CREATED));
    }

    @DeleteMapping("/async/{commentId}")
    public CompletableFuture<ResponseEntity<String>> deleteComment(@PathVariable("commentId") Long commentId) {
        return asyncCommentService.deleteComment(commentId)
                .thenApply(aVoid -> new ResponseEntity<>("{\"msg\": \"Comment deleted successfully\"}", HttpStatus.NO_CONTENT));
    }

    @PutMapping("/async/{commentId}")
    public CompletableFuture<ResponseEntity<String>> updateComment(
            @PathVariable("commentId") Long commentId,
            @RequestBody Comment comment) {
        return asyncCommentService.updateComment(commentId, comment.getContent())
                .thenApply(aVoid -> new ResponseEntity<>("{\"msg\": \"Comment updated successfully\"}", HttpStatus.OK));
    }

    @GetMapping("/async/video/{videoId}")
    public CompletableFuture<ResponseEntity<List<Comment>>> getVideoComments(@PathVariable("videoId") Long videoId) {
        return asyncCommentService.getVideoComments(videoId)
                .thenApply(comments -> ResponseEntity.ok(comments));
    }

    @GetMapping("/async")
    public CompletableFuture<ResponseEntity<List<CommentInfo>>> getAllComments() {
        return asyncCommentService.getAllComments()
                .thenApply(commentInfos -> ResponseEntity.ok(commentInfos));
    }
}
