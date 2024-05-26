package org.example.mybatis.controller;

import org.example.mybatis.entity.Comment;
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
    private AsyncCommentService asyncCommentCommentService;

    @PostMapping("/async")
    public CompletableFuture<ResponseEntity<String>> insertNewComment(
            @RequestBody Comment comment) {
        return asyncCommentCommentService.insertNewComment(comment.getVideoID(), comment.getUserID(), comment.getContent())
                .thenApply(aVoid -> new ResponseEntity<>("Comment added successfully", HttpStatus.CREATED));
    }

    @DeleteMapping("/async/{commentId}")
    public CompletableFuture<ResponseEntity<String>> deleteComment(@PathVariable("commentId") Long commentId) {
        return asyncCommentCommentService.deleteComment(commentId)
                .thenApply(aVoid -> new ResponseEntity<>("Comment deleted successfully", HttpStatus.NO_CONTENT));
    }

    @PutMapping("/async/{commentId}")
    public CompletableFuture<ResponseEntity<String>> updateComment(
            @PathVariable("commentId") Long commentId,
            @RequestBody Comment comment) {
        return asyncCommentCommentService.updateComment(commentId, comment.getContent())
                .thenApply(aVoid -> new ResponseEntity<>("Comment updated successfully", HttpStatus.OK));
    }

    @GetMapping("/async/video/{videoId}")
    public CompletableFuture<ResponseEntity<List<Comment>>> getVideoComments(@PathVariable("videoId") Long videoId) {
        return asyncCommentCommentService.getVideoComments(videoId)
                .thenApply(comments -> ResponseEntity.ok(comments));
    }
}
