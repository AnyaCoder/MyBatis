package org.example.mybatis.entity;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class Admin {
    private Long adminID;
    private String adminName;
    private String email;
    private String password;
}
