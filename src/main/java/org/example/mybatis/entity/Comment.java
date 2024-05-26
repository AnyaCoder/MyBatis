package org.example.mybatis.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;


@Setter
@Getter
@Data
public class Comment {
    // Getters and Setters
    private Long commentID;
    private Long videoID;
    private Long userID;
    private String username;
    private String content;
    private Timestamp commentTime;
}