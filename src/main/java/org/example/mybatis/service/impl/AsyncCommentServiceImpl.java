package org.example.mybatis.service.impl;

import org.example.mybatis.entity.Comment;
import org.example.mybatis.entity.CommentInfo;
import org.example.mybatis.service.AsyncCommentService;
import org.example.mybatis.service.AsyncVideoService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class AsyncCommentServiceImpl implements AsyncCommentService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Async
    public CompletableFuture<Void> insertNewComment(Long videoId, Long userId, String content) {
        return CompletableFuture.runAsync(() -> {
            jdbcTemplate.execute((Connection connection) -> {
                try (CallableStatement callableStatement = connection.prepareCall("{call InsertNewComment(?, ?, ?)}")) {
                    callableStatement.setLong(1, videoId);
                    callableStatement.setLong(2, userId);
                    callableStatement.setString(3, content);
                    callableStatement.execute();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return null;
            });
        });
    }

    @Async
    public CompletableFuture<Void> deleteComment(Long commentId) {
        return CompletableFuture.runAsync(() -> {
            jdbcTemplate.execute((Connection connection) -> {
                try (CallableStatement callableStatement = connection.prepareCall("{call DeleteComment(?)}")) {
                    callableStatement.setLong(1, commentId);
                    callableStatement.execute();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return null;
            });
        });
    }

    @Async
    public CompletableFuture<Void> updateComment(Long commentId, String content) {
        return CompletableFuture.runAsync(() -> {
            jdbcTemplate.execute((Connection connection) -> {
                try (CallableStatement callableStatement = connection.prepareCall("{call UpdateComment(?, ?)}")) {
                    callableStatement.setLong(1, commentId);
                    callableStatement.setString(2, content);
                    callableStatement.execute();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return null;
            });
        });
    }

    @Async
    @Override
    public CompletableFuture<List<CommentInfo>> getAllComments() {
        return CompletableFuture.supplyAsync(() -> {
            return jdbcTemplate.execute((Connection connection) -> {
                List<CommentInfo> commentInfos = new ArrayList<>();
                try (CallableStatement callableStatement = connection.prepareCall("{call GetAllComments()}")) {
                    try (ResultSet resultSet = callableStatement.executeQuery()) {
                        while (resultSet.next()) {
                            CommentInfo commentInfo = new CommentInfo();
                            commentInfo.setCommentID(resultSet.getLong("CommentID"));
                            commentInfo.setContent(resultSet.getString("Content"));
                            commentInfo.setCommentTime(resultSet.getTimestamp("CommentTime"));
                            commentInfo.setUserID(resultSet.getLong("UserID"));
                            commentInfo.setUsername(resultSet.getString("Username"));
                            commentInfo.setVideoID(resultSet.getLong("VideoID"));
                            commentInfo.setTitle(resultSet.getString("Title"));
                            commentInfos.add(commentInfo);
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return commentInfos;
            });
        });

    }


    @Async
    public CompletableFuture<List<Comment>> getVideoComments(Long videoId) {
        return CompletableFuture.supplyAsync(() -> {
            return jdbcTemplate.execute((Connection connection) -> {
                List<Comment> comments = new ArrayList<>();
                try (CallableStatement callableStatement = connection.prepareCall("{call GetVideoComments(?)}")) {
                    callableStatement.setLong(1, videoId);
                    try (ResultSet resultSet = callableStatement.executeQuery()) {
                        while (resultSet.next()) {
                            Comment comment = new Comment();
                            comment.setCommentID(resultSet.getLong("CommentID"));
                            comment.setContent(resultSet.getString("Content"));
                            comment.setCommentTime(resultSet.getTimestamp("CommentTime"));
                            comment.setUsername(resultSet.getString("Username"));
                            comments.add(comment);
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return comments;
            });
        });
    }
}
