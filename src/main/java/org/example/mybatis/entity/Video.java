package org.example.mybatis.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Setter
@Getter
@Data
public class Video {
    // Getters and Setters
    private int videoID;
    private int userID;
    private String title;
    private String description;
    private Timestamp uploadTime;
    private String videoPath;
    private int likes;
    private int views;
}