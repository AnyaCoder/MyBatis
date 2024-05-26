package org.example.mybatis.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Setter
@Getter
@Data
public class Follows {
    private Long userId;
    private Long followedUserId;
    private Timestamp followTime;
}
