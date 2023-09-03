package com.bilibil.service;

import com.bilibil.entity.PageResult;
import com.bilibil.entity.Video;
import com.bilibil.entity.VideoCoin;
import com.bilibil.entity.VideoCollection;
import org.apache.ibatis.annotations.Param;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Date:  2023/8/30
 */

public interface VideoService {
    // 用户上传添加视频
    void addVideo(Video video);
    // 分页查询视频（前端瀑布流视频列表）  Number 第几页  ， area分区  （每个分区加载多少条视频）
    PageResult<Video> pageListVideos(Integer size, Integer number, String area);
    // 在线视频观看,通过分片的方式
    void viewVideoOnlineBySkices(HttpServletRequest request, HttpServletResponse servletRequest, String url) throws Exception;
    /**
     * 点赞视频
     */
    void addVideoLike(Long videoId, Long userId);

    void deleteVideoLike(Long videoId, Long userId);
    // 查询点赞数量
    Map<String, Object> getVideoLikes(Long videoId, Long userId);
    // 收藏视频
    void addVideoCollection(VideoCollection videoCollection, Long userId);

    void deleteVideoCollection(Long videoId, Long userId);
    // 查询视频收藏数量
    Map<String, Object> getVideoCollections(Long videoId, Long userId);
    // 视频投币
    void addVideoCoins(VideoCoin videoCoin, Long userId);
    // 区分用户有没有登录
    Map<String, Object> getVideoCoins(Long videoId, Long userId);
}

