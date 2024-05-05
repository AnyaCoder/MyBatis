package org.example.mybatis.service.impl;

import org.example.mybatis.entity.User;
import org.example.mybatis.mapper.UserMapper;
import org.example.mybatis.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<User> listAllUsers() {
        return userMapper.listAll();
    }

    @Override
    public User getUserById(Long userId) {
        return userMapper.getUserById(userId);
    }

    @Override
    public void insertUser(User user) {
        userMapper.insertUser(user);
    }

    @Override
    public User updateUser(User user) {
        // 先检查用户是否存在
        User existingUser = userMapper.getUserById(user.getUserID());
        if (existingUser != null) {
            userMapper.updateUser(user);
            return user;
        } else {
            return null;  // 用户不存在时返回 null
        }
    }

    @Override
    public boolean deleteUser(Long userId) {
        // 检查用户是否存在
        User existingUser = userMapper.getUserById(userId);
        if (existingUser != null) {
            userMapper.deleteUser(userId);
            return true;  // 成功删除用户
        } else {
            return false;  // 用户不存在时返回 false
        }
    }

}