package org.example.mybatis.mapper;

import org.example.mybatis.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    List<User> listAll();
    User getUserById(Long userID);
    void insertUser(User user);
    void updateUser(User user);
    void deleteUser(Long userID);
}
