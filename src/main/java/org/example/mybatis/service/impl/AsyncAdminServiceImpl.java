package org.example.mybatis.service.impl;


import org.example.mybatis.entity.Admin;
import org.example.mybatis.service.AsyncAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
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
public class AsyncAdminServiceImpl implements AsyncAdminService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public CompletableFuture<Admin> getAdminProcedure(Long adminID) {
        return CompletableFuture.supplyAsync(() -> jdbcTemplate.execute((Connection connection) -> {
            Admin admin = null;
            try (CallableStatement callableStatement = connection.prepareCall("{call GetAdmin(?)}")) {
                callableStatement.setLong(1, adminID);
                try (ResultSet resultSet = callableStatement.executeQuery()) {
                    if (resultSet.next()) {
                        admin = new Admin();
                        admin.setAdminID(resultSet.getLong("AdminID"));
                        admin.setAdminName(resultSet.getString("AdminName"));
                        admin.setPassword(resultSet.getString("Password"));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return admin;
        }));
    }

    public CompletableFuture<List<Admin>> getAdminByUsername(String adminName, String password) {
        return CompletableFuture.supplyAsync(() -> jdbcTemplate.execute((Connection connection) -> {
            List<Admin> admins = new ArrayList<>();
            try (CallableStatement callableStatement = connection.prepareCall("{call GetAdminByUsername(?)}")) {
                callableStatement.setString(1, adminName);
                try (ResultSet resultSet = callableStatement.executeQuery()) {
                    while (resultSet.next()) {
                        String storedPassword = resultSet.getString("Password");
                        if (passwordEncoder.matches(password, storedPassword)) {
                            Admin admin = new Admin();
                            admin.setAdminID(resultSet.getLong("AdminID"));
                            admin.setAdminName(resultSet.getString("AdminName"));
                            admin.setEmail(resultSet.getString("Email"));
                            // 设置其他字段
                            admins.add(admin);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return admins;
        }));
    }


    public CompletableFuture<Admin> insertAdminProcedure(String adminName, String email, String password) {
        return CompletableFuture.supplyAsync(() -> jdbcTemplate.execute((Connection connection) -> {
            Admin admin = null;
            try (CallableStatement callableStatement = connection.prepareCall("{call InsertNewAdmin(?, ?, ?)}")) {
                String encryptedPassword = passwordEncoder.encode(password);
                callableStatement.setString(1, adminName);
                callableStatement.setString(2, email);
                callableStatement.setString(3, encryptedPassword);
                try (ResultSet resultSet = callableStatement.executeQuery()) {
                    if (resultSet.next()) {
                        admin = new Admin();
                        admin.setAdminID(resultSet.getLong("LAST_INSERT_ID()"));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return admin;
        }));
    }

    public CompletableFuture<Void> deleteAdminProcedure(Long adminID) {
        return CompletableFuture.runAsync(() -> {
            jdbcTemplate.execute((Connection connection) -> {
                try (CallableStatement callableStatement = connection.prepareCall("{call DeleteAdmin(?)}")) {
                    callableStatement.setLong(1, adminID);
                    callableStatement.execute();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return null;
            });
        });
    }

    public CompletableFuture<Void> updateAdminProcedure(Long adminID, String adminName, String password) {
        return CompletableFuture.runAsync(() -> {
            jdbcTemplate.execute((Connection connection) -> {
                try (CallableStatement callableStatement = connection.prepareCall("{call UpdateAdminInfo(?, ?, ?)}")) {
                    callableStatement.setLong(1, adminID);
                    callableStatement.setString(2, adminName);
                    String encryptedPassword = passwordEncoder.encode(password);
                    callableStatement.setString(3, encryptedPassword);
                    callableStatement.execute();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return null;
            });
        });
    }
}
