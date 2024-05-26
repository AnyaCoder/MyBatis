package org.example.mybatis.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.sql.Timestamp;

@Setter
@Getter
@Data
public class User {
    // Getters and Setters
    private int userID;
    private String username;
    private String password;
    private String email;
    private String phoneNumber;
    private Timestamp registrationDate;
    private int gender;
}