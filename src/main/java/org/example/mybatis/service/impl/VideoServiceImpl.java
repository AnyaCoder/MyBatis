package org.example.mybatis.service.impl;

import org.example.mybatis.entity.Video;
import org.example.mybatis.mapper.VideoMapper;
import org.example.mybatis.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VideoServiceImpl implements VideoService {

    @Autowired
    private VideoMapper videoMapper;

    @Override
    public List<Video> listAllVideos() {
        return videoMapper.listAll();
    }

    @Override
    public Video getVideoByVideoId(int videoId) {
        return videoMapper.getVideoByVideoId(videoId);
    }

    @Override
    public void insertVideo(Video video) {
        videoMapper.insertVideo(video);
    }

    @Override
    public Video updateVideo(Video video) {
        // 先检查视频是否存在
        Video existingVideo = videoMapper.getVideoByVideoId(video.getVideoID());
        if (existingVideo != null) {
            videoMapper.updateVideo(video);
            return video;
        } else {
            return null;  // 用户不存在时返回 null
        }
    }

    @Override
    public boolean deleteVideo(int videoId) {
        // 检查用户是否存在
        Video existingVideo = videoMapper.getVideoByVideoId(videoId);
        if (existingVideo != null) {
            videoMapper.deleteVideo(videoId);
            return true;  // 成功删除用户
        } else {
            return false;  // 用户不存在时返回 false
        }
    }

}