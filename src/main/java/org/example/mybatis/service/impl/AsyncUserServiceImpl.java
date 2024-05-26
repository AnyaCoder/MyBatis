package org.example.mybatis.service.impl;

import org.example.mybatis.entity.User;
import org.example.mybatis.service.AsyncUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

@Service
public class AsyncUserServiceImpl implements AsyncUserService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

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
    public CompletableFuture<Void> addNewUserProcedure(Long userID, String username, String password, String email, String phoneNumber, int gender) {
        return CompletableFuture.runAsync(() -> {
            jdbcTemplate.execute((Connection connection) -> {
                try (CallableStatement callableStatement = connection.prepareCall("{call InsertNewUser(?, ?, ?, ?, ?, ?)}")) {
                    callableStatement.setLong(1, userID);
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

    @Async
    public CompletableFuture<Void> deleteUserProcedure(int userId) {
        return CompletableFuture.runAsync(() -> {
            jdbcTemplate.execute((Connection connection) -> {
                try (CallableStatement callableStatement = connection.prepareCall("{call DeleteUser(?)}")) {
                    callableStatement.setInt(1, userId);
                    callableStatement.execute();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return null;
            });
        });
    }

    @Async
    public CompletableFuture<Void> updateUserInfoProcedure(int userId, String email, String phoneNumber) {
        return CompletableFuture.runAsync(() -> {
            jdbcTemplate.execute((Connection connection) -> {
                try (CallableStatement callableStatement = connection.prepareCall("{call UpdateUserInfo(?, ?, ?)}")) {
                    callableStatement.setInt(1, userId);
                    callableStatement.setString(2, email);
                    callableStatement.setString(3, phoneNumber);
                    callableStatement.execute();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return null;
            });
        });
    }
}
