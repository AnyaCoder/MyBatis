package org.example.mybatis.service;

import org.example.mybatis.entity.User;
import org.example.mybatis.entity.Video;

import java.util.List;

public interface VideoService {
    List<Video> listAllVideos();

    Video getVideoByVideoId(int videoId);

    void insertVideo(Video video);

    Video updateVideo(Video video);

    boolean deleteVideo(int videoID);
}