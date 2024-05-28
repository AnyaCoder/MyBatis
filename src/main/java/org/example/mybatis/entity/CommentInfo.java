package org.example.mybatis.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Setter
@Getter
@Data
public class CommentInfo {
    private Long commentID;
    private String content;
    private Timestamp commentTime;
    private Long userID;
    private String Username;
    private Long videoID;
    private String title;
}
