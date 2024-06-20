package org.example.mybatis.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.mybatis.entity.User;

import java.util.List;

@Mapper
public interface UserMapper {
    List<User> listAll();

    User getUserById(Long userID);

    void insertUser(User user);

    void updateUser(User user);

    void deleteUser(Long userID);

    @Select("SELECT * FROM Users WHERE Email = #{email}")
    List<User> findUsersByEmail(String email);

}
