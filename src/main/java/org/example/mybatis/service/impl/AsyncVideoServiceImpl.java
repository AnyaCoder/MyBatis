package org.example.mybatis.service.impl;

import org.example.mybatis.entity.User;
import org.example.mybatis.entity.Video;
import org.example.mybatis.service.AsyncVideoService;
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
public class AsyncVideoServiceImpl implements AsyncVideoService {
    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Async
    @Override
    public CompletableFuture<Video> addNewVideoProcedure(Long userID, String title, String description, String videoPath) {
        return CompletableFuture.supplyAsync(() -> jdbcTemplate.execute((Connection connection) -> {
            Video video = null;
            try (CallableStatement callableStatement = connection.prepareCall("{call InsertNewVideo(?, ?, ?, ?)}")) {
                callableStatement.setLong(1, userID);
                callableStatement.setString(2, title);
                callableStatement.setString(3, description);
                callableStatement.setString(4, videoPath);
                try (ResultSet resultSet = callableStatement.executeQuery()) {
                    if (resultSet.next()) {
                        video = new Video();
                        video.setVideoID(resultSet.getLong("LAST_INSERT_ID()"));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return video;
        }));
    }

    @Async
    @Override
    public CompletableFuture<Void> deleteVideoProcedure(Long videoId) {
        return CompletableFuture.runAsync(() -> {
            jdbcTemplate.execute((Connection connection) -> {
                try (CallableStatement callableStatement = connection.prepareCall("{call DeleteVideo(?)}")) {
                    callableStatement.setLong(1, videoId);
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
    public CompletableFuture<List<Video>> getUserVideosProcedure(Long userId) {
        return CompletableFuture.supplyAsync(() -> {
            return jdbcTemplate.execute((Connection connection) -> {
                List<Video> videos = new ArrayList<>();
                try (CallableStatement callableStatement = connection.prepareCall("{call GetUserVideos(?)}")) {
                    callableStatement.setLong(1, userId);
                    try (ResultSet resultSet = callableStatement.executeQuery()) {
                        while (resultSet.next()) {
                            Video video = new Video();
                            video.setVideoID(resultSet.getLong("VideoID"));
                            video.setTitle(resultSet.getString("Title"));
                            video.setDescription(resultSet.getString("Description"));
                            video.setUploadTime(resultSet.getTimestamp("UploadTime"));
                            video.setLikes(resultSet.getLong("Likes"));
                            video.setViews(resultSet.getLong("Views"));
                            videos.add(video);
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return videos;
            });
        });
    }



    @Async
    @Override
    public CompletableFuture<Void> updateVideoInfoProcedure(Long videoId, String title, String description, String videoPath) {
        return CompletableFuture.runAsync(() -> {
            jdbcTemplate.execute((Connection connection) -> {
                try (CallableStatement callableStatement = connection.prepareCall("{call UpdateVideoInfo(?, ?, ?, ?)}")) {
                    callableStatement.setLong(1, videoId);
                    callableStatement.setString(2, title);
                    callableStatement.setString(3, description);
                    callableStatement.setString(4, videoPath);
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
    public CompletableFuture<Void> incrementViews(Long videoId) {
        return CompletableFuture.runAsync(() -> {
            jdbcTemplate.execute((Connection connection) -> {
                try (CallableStatement callableStatement = connection.prepareCall("{call IncrementVideoViews(?)}")) {
                    callableStatement.setLong(1, videoId);
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
    public CompletableFuture<Void> incrementLikes(Long videoId) {
        return CompletableFuture.runAsync(() -> {
            jdbcTemplate.execute((Connection connection) -> {
                try (CallableStatement callableStatement = connection.prepareCall("{call IncrementVideoLikes(?)}")) {
                    callableStatement.setLong(1, videoId);
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
    public CompletableFuture<Void> decrementViews(Long videoId) {
        return CompletableFuture.runAsync(() -> {
            jdbcTemplate.execute((Connection connection) -> {
                try (CallableStatement callableStatement = connection.prepareCall("{call DecrementVideoViews(?)}")) {
                    callableStatement.setLong(1, videoId);
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
    public CompletableFuture<Void> decrementLikes(Long videoId) {
        return CompletableFuture.runAsync(() -> {
            jdbcTemplate.execute((Connection connection) -> {
                try (CallableStatement callableStatement = connection.prepareCall("{call DecrementVideoLikes(?)}")) {
                    callableStatement.setLong(1, videoId);
                    callableStatement.execute();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return null;
            });
        });
    }

}
