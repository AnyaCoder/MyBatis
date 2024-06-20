package org.example.mybatis.service;

import org.example.mybatis.entity.Comment;
import org.example.mybatis.entity.CommentInfo;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface AsyncCommentService {
    CompletableFuture<Void> insertNewComment(Long videoId, Long userId, String content);

    CompletableFuture<Void> deleteComment(Long commentId);

    CompletableFuture<Void> updateComment(Long commentId, String content);

    CompletableFuture<List<Comment>> getVideoComments(Long videoId);

    CompletableFuture<List<CommentInfo>> getAllComments();
}
