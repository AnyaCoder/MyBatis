package org.example.mybatis.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
@Data
public class UserStats {
    private Long userID;
    private Long likes;
    private Long friends;
    private Long following;
    private Long followers;
}
