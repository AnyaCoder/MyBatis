package org.example.mybatis.service.impl;

import org.example.mybatis.service.AsyncFollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class AsyncFollowServiceImpl implements AsyncFollowService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Async
    public CompletableFuture<Void> insertNewFollow(Long userId, Long followedUserId) {
        return CompletableFuture.runAsync(() -> {
            jdbcTemplate.execute((Connection connection) -> {
                try (CallableStatement callableStatement = connection.prepareCall("{call InsertNewFollow(?, ?)}")) {
                    callableStatement.setLong(1, userId);
                    callableStatement.setLong(2, followedUserId);
                    callableStatement.execute();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return null;
            });
        });
    }

    @Async
    public CompletableFuture<Void> deleteFollow(Long userId, Long followedUserId) {
        return CompletableFuture.runAsync(() -> {
            jdbcTemplate.execute((Connection connection) -> {
                try (CallableStatement callableStatement = connection.prepareCall("{call DeleteFollow(?, ?)}")) {
                    callableStatement.setLong(1, userId);
                    callableStatement.setLong(2, followedUserId);
                    callableStatement.execute();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return null;
            });
        });
    }

    @Async
    public CompletableFuture<List<String>> getFollowers(Long userId) {
        return CompletableFuture.supplyAsync(() -> {
            return jdbcTemplate.execute((Connection connection) -> {
                List<String> followers = new ArrayList<>();
                try (CallableStatement callableStatement = connection.prepareCall("{call GetFollowers(?)}")) {
                    callableStatement.setLong(1, userId);
                    try (ResultSet resultSet = callableStatement.executeQuery()) {
                        while (resultSet.next()) {
                            followers.add(resultSet.getString("Username"));
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return followers;
            });
        });
    }

    @Async
    public CompletableFuture<List<String>> getFollowedUsers(Long userId) {
        return CompletableFuture.supplyAsync(() -> {
            return jdbcTemplate.execute((Connection connection) -> {
                List<String> followedUsers = new ArrayList<>();
                try (CallableStatement callableStatement = connection.prepareCall("{call GetFollowedUsers(?)}")) {
                    callableStatement.setLong(1, userId);
                    try (ResultSet resultSet = callableStatement.executeQuery()) {
                        while (resultSet.next()) {
                            followedUsers.add(resultSet.getString("Username"));
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return followedUsers;
            });
        });
    }
}
