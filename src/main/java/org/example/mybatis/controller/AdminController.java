package org.example.mybatis.controller;

import org.example.mybatis.entity.Admin;
import org.example.mybatis.service.AsyncAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("admins")
public class AdminController {
    @Autowired
    private AsyncAdminService asyncAdminService;

    @GetMapping("/async/{adminId}")
    public CompletableFuture<ResponseEntity<Admin>> getAdminAsync(@PathVariable("adminId") Long adminId) {
        return asyncAdminService.getAdminProcedure(adminId)
                .thenApply(admin -> {
                    if (admin != null) {
                        return new ResponseEntity<>(admin, HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                });
    }

    @PostMapping("/async/login")
    public CompletableFuture<Admin> login(@RequestBody Admin admin) {
        return asyncAdminService.getAdminByUsername(admin.getAdminName(), admin.getPassword())
                .thenApply(admins -> {
                    if (admins.size() == 1) {
                        return admins.get(0); // 返回匹配的唯一管理员
                    } else if (admins.size() > 1) {
                        // 处理多个匹配的管理员的情况，抛出异常或选择一个特定的逻辑
                        throw new RuntimeException("Multiple admins found with the same credentials");
                    } else {
                        throw new RuntimeException("No admin found with the given credentials");
                    }
                });
    }

    @PostMapping("/async")
    public CompletableFuture<ResponseEntity<Admin>> insertAdminAsync(@RequestBody Admin admin) {
        return asyncAdminService.insertAdminProcedure(admin.getAdminName(), admin.getEmail(), admin.getPassword())
                .thenApply(newAdmin -> {
                    if (newAdmin != null) {
                        return new ResponseEntity<>(newAdmin, HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                    }
                });
    }

    // 删除用户（异步）
    @DeleteMapping("/async/{adminId}")
    public CompletableFuture<ResponseEntity<String>> deleteAdminAsync(@PathVariable("adminId") Long adminId) {
        return asyncAdminService.deleteAdminProcedure(adminId)
                .thenApply(aVoid -> new ResponseEntity<>("{\"msg\": \"Admin deleted successfully\"}", HttpStatus.NO_CONTENT));
    }

    // 更新用户信息（异步）
    @PutMapping("/async")
    public CompletableFuture<ResponseEntity<String>> updateAdminAsync(@RequestBody Admin admin) {
        return asyncAdminService.updateAdminProcedure(admin.getAdminID(), admin.getAdminName(), admin.getPassword())
                .thenApply(aVoid -> new ResponseEntity<>("{\"msg\": \"Admin updated successfully\"}", HttpStatus.OK));
    }

}
