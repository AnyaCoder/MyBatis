package org.example.mybatis.service.impl;

import org.example.mybatis.entity.Admin;
import org.example.mybatis.entity.User;
import org.example.mybatis.entity.UserStats;
import org.example.mybatis.service.AsyncUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class AsyncUserServiceImpl implements AsyncUserService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Async
    public CompletableFuture<User> getUserProcedure(Long userID) {
        return CompletableFuture.supplyAsync(() -> jdbcTemplate.execute((Connection connection) -> {
            User user = null;
            try (CallableStatement callableStatement = connection.prepareCall("{call GetUser(?)}")) {
                callableStatement.setLong(1, userID);
                try (ResultSet resultSet = callableStatement.executeQuery()) {
                    if (resultSet.next()) {
                        user = new User();
                        user.setUserID(resultSet.getLong("UserID"));
                        user.setUsername(resultSet.getString("Username"));
                        user.setPassword(resultSet.getString("Password"));
                        user.setEmail(resultSet.getString("Email"));
                        user.setPhoneNumber(resultSet.getString("PhoneNumber"));
                        user.setRegistrationDate(resultSet.getTimestamp("RegistrationDate"));
                        user.setGender(resultSet.getByte("Gender"));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return user;
        }));
    }

    @Async
    public CompletableFuture<UserStats> getUserStats(Long userId) {
        return CompletableFuture.supplyAsync(() -> jdbcTemplate.execute((Connection connection) -> {
            UserStats userStats = null;
            try (CallableStatement callableStatement = connection.prepareCall("{call GetUserStats(?)}")) {
                callableStatement.setLong(1, userId);
                try (ResultSet resultSet = callableStatement.executeQuery()) {
                    if (resultSet.next()) {
                        userStats = new UserStats();
                        userStats.setUserID(resultSet.getLong("UserID"));
                        userStats.setFollowers(resultSet.getLong("Followers"));
                        userStats.setFollowing(resultSet.getLong("Following"));
                        userStats.setLikes(resultSet.getLong("Likes"));
                        userStats.setFriends(resultSet.getLong("Friends"));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return userStats;
        }));
    }

    @Async
    public CompletableFuture<List<User>> getUserByPhoneNumber(String phoneNumber, String password) {
        return CompletableFuture.supplyAsync(() -> jdbcTemplate.execute((Connection connection) -> {
            List<User> users = new ArrayList<>();
            try (CallableStatement callableStatement = connection.prepareCall("{call GetUserByPhoneNumber(?)}")) {
                callableStatement.setString(1, phoneNumber);
                try (ResultSet resultSet = callableStatement.executeQuery()) {
                    while (resultSet.next()) {
                        String storedPassword = resultSet.getString("Password");
                        if (passwordEncoder.matches(password, storedPassword)) {
                            User user = new User();
                            user.setUserID(resultSet.getLong("UserID"));
                            user.setUsername(resultSet.getString("Username"));
                            user.setGender(resultSet.getInt("Gender"));
                            user.setPhoneNumber(resultSet.getString("PhoneNumber"));
                            user.setEmail(resultSet.getString("Email"));
                            user.setRegistrationDate(resultSet.getTimestamp("RegistrationDate"));
                            // 设置其他字段
                            users.add(user);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return users;
        }));
    }


    @Async
    public CompletableFuture<User> getUserByEmailProcedure(String email) {
        return CompletableFuture.supplyAsync(() -> jdbcTemplate.execute((Connection connection) -> {
            User user = null;
            try (CallableStatement callableStatement = connection.prepareCall("{call GetUserByEmail(?)}")) {
                callableStatement.setString(1, email);
                try (ResultSet resultSet = callableStatement.executeQuery()) {
                    if (resultSet.next()) {
                        user = new User();
                        user.setUserID(resultSet.getLong("UserID"));
                        user.setUsername(resultSet.getString("Username"));
                        user.setPassword(resultSet.getString("Password"));
                        user.setEmail(resultSet.getString("Email"));
                        user.setPhoneNumber(resultSet.getString("PhoneNumber"));
                        user.setRegistrationDate(resultSet.getTimestamp("RegistrationDate"));
                        user.setGender(resultSet.getByte("Gender"));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return user;
        }));
    }


    @Async
    public CompletableFuture<User> addNewUserProcedure(String username, String password, String email, String phoneNumber, int gender) {
        return CompletableFuture.supplyAsync(() -> jdbcTemplate.execute((Connection connection) -> {
            User user = null;
            try (CallableStatement callableStatement = connection.prepareCall("{call InsertNewUser(?, ?, ?, ?, ?)}")) {
                String encryptedPassword = passwordEncoder.encode(password);
                callableStatement.setString(1, username);
                callableStatement.setString(2, encryptedPassword);
                callableStatement.setString(3, email);
                callableStatement.setString(4, phoneNumber);
                callableStatement.setInt(5, gender);
                try (ResultSet resultSet = callableStatement.executeQuery()) {
                    if (resultSet.next()) {
                        user = new User();
                        user.setUserID(resultSet.getLong("LAST_INSERT_ID()"));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return user;
        }));
    }

    @Async
    public CompletableFuture<Void> deleteUserProcedure(Long userId) {
        return CompletableFuture.runAsync(() -> {
            jdbcTemplate.execute((Connection connection) -> {
                try (CallableStatement callableStatement = connection.prepareCall("{call DeleteUserAndRelatedData(?)}")) {
                    callableStatement.setLong(1, userId);
                    callableStatement.execute();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return null;
            });
        });
    }

    @Async
    public CompletableFuture<Void> updateUserInfoProcedure(Long userId, String username, String password, String email, String phoneNumber, int gender) {
        return CompletableFuture.runAsync(() -> {
            jdbcTemplate.execute((Connection connection) -> {
                try (CallableStatement callableStatement = connection.prepareCall("{call UpdateUserInfo(?, ?, ?, ?, ?, ?)}")) {
                    callableStatement.setLong(1, userId);
                    callableStatement.setString(2, username);
                    callableStatement.setString(3, password);
                    callableStatement.setString(4, email);
                    callableStatement.setString(5, phoneNumber);
                    callableStatement.setInt(6, gender);
                    callableStatement.execute();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return null;
            });
        });
    }
}
