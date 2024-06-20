package org.example.mybatis.service;

import org.example.mybatis.entity.Admin;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface AsyncAdminService {

    CompletableFuture<Admin> getAdminProcedure(Long adminID);

    CompletableFuture<List<Admin>> getAdminByUsername(String adminName, String password);

    CompletableFuture<Admin> insertAdminProcedure(String adminName, String email, String password);

    CompletableFuture<Void> deleteAdminProcedure(Long adminID);

    CompletableFuture<Void> updateAdminProcedure(Long adminID, String adminName, String password);

}
