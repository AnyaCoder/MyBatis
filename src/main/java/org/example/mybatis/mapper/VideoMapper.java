package org.example.mybatis.mapper;

import org.example.mybatis.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.example.mybatis.entity.Video;

import java.util.List;

@Mapper
public interface VideoMapper {
    List<Video> listAll();
    Video getVideoByVideoId(Long VideoID);
    void insertVideo(Video video);
    void updateVideo(Video video);
    void deleteVideo(Long videoID);
}
