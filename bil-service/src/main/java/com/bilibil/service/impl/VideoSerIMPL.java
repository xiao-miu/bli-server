package com.bilibil.service.impl;

import com.bilibil.entity.*;
import com.bilibil.exception.ConditionException;
import com.bilibil.mapper.VideoMapper;
import com.bilibil.service.VideoService;
import com.bilibil.service.UserCoinServer;
import com.bilibil.util.FastDFSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;


/**
 * Date:  2023/8/30
 */
@Service
public class VideoSerIMPL implements VideoService {
    @Autowired
    private VideoMapper videoMapper;
    @Autowired
    private FastDFSUtil fastDFSUtil;
    // 视频硬币
    @Autowired
    private UserCoinServer userCoinService;

    // 用户视频投稿
    // 因为有两个数据库要进行添加为了以防一个成功一个失败，或者是添加过程中出现问题，来进行事务回滚
    @Transactional
    @Override
    public void addVideo(Video video) {
        video.setCreateTime(new Date());
        // 用户添加成功视频投稿记录表
        videoMapper.addVideo(video);
        Long videoId = video.getUserId();
        // 前端传过来的视频的Tag（标签）取出来
        List<VideoTag> tegList = video.getVideoTagList();
        // 进行赋值，添加视频标签
        tegList.stream().forEach(item -> {
            item.setVideoId(videoId);
            item.setCreateTime(new Date());
        });
        videoMapper.batchAddVideoTags(tegList);
    }
    // 分页查询视频（前端瀑布流视频列表）  Number 第几页  ， area分区也就是视频的标签  （每个分区加载多少条视频）
    @Override
    public PageResult<Video> pageListVideos(Integer size, Integer number, String area) {
        if(size == null || number == null){
            throw new ClassCastException("参数异常!");
        }
        // 封装参数
        Map<String, Object> map = new HashMap<>();
        // 查询的起始位置（偏移量)
        map.put("start",(number-1)*size);
        map.put("limit",size);
        map.put("area",area);
        // 如果不成功 会回传一个空的数据集合
        List<Video> list = new ArrayList<>();
        // 满足筛选条件一共有多少条
        Integer total = videoMapper.pageCountVideos(map);
        if(total > 0){
            list = videoMapper.pageListVoides(map);
        }
        return new PageResult<>(total, list);
    }
    // 在线视频观看,通过分片的方式
    @Override
    public void viewVideoOnlineBySkices(HttpServletRequest request,
                                        HttpServletResponse servletRequest, String url) throws Exception {
        fastDFSUtil.viewVideoOnlineBySkices(request, servletRequest, url);
    }
    /**
     * 点赞视频
     */
    @Override
    public void addVideoLike(Long videoId, Long userId) {
        // 根据视频id视频信息
        Video video = videoMapper.getVideoById(videoId);
        if(video == null){
            throw new ConditionException("非法视频！");
        }
        // 当前用户视频有没有被当前用户点赞过
        VideoLike videoLike = videoMapper.getVideoLikeByVideoIdAndUserId(videoId, userId);
        if(videoLike != null){
            throw new ConditionException("已经赞过！");
        }
        // 给用户添加点赞记录
        videoLike = new VideoLike();
        videoLike.setVideoId(videoId);
        videoLike.setUserId(userId);
        videoLike.setCreateTime(new Date());
        videoMapper.addVideoLike(videoLike);
    }
    // 取消点赞
    @Override
    public void deleteVideoLike(Long videoId, Long userId) {
        videoMapper.deleteVideoLike(videoId, userId);
    }
    // 查询点赞数量
    @Override
    public Map<String, Object> getVideoLikes(Long videoId, Long userId) {
        // 查用户id点赞的数量
        Long count = videoMapper.getVideoLikes(videoId);
        // 当前用户视频有没有被当前用户点赞过
        VideoLike videoLike = videoMapper.getVideoLikeByVideoIdAndUserId(videoId, userId);
        boolean like = videoLike != null;
        // 封装用户点赞数据返回给前端
        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        result.put("like", like);
        return result;
    }
    // 收藏视频
    @Override
    public void addVideoCollection(VideoCollection videoCollection, Long userId) {
        Long videoId = videoCollection.getVideoId();
        Long groupId = videoCollection.getGroupId();
        if(videoId == null && groupId == null) {
            throw new ConditionException("参数异常");
        }
        if(userId == null){
            throw new ConditionException("视频异常");
        }
        // 删除原有的视频收藏
        videoMapper.deleteVideoCollection(videoId, userId);
        // 添加视频收藏
        videoCollection.setUserId(userId);
        videoCollection.setCreateTime(new Date());
        videoMapper.addVideoCollection(videoCollection);
    }

    @Override
    public void deleteVideoCollection(Long videoId, Long userId) {
        // 删除原有的视频收藏
        videoMapper.deleteVideoCollection(videoId, userId);
    }
    // 查询视频收藏数量
    @Override
    public Map<String, Object> getVideoCollections(Long videoId, Long userId) {
        // 获取用户收藏的条数
        Long count = videoMapper.getVideoCollections(videoId);
        // 视频收藏
        VideoCollection videoCollection = videoMapper.getVideoCollectionByVideoIdAndUserId(videoId, userId);
        boolean like = videoCollection != null;
        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        result.put("like", like);
        return result;
    }
    // 视频投币
    @Transactional
    @Override
    public void addVideoCoins(VideoCoin videoCoin, Long userId) {
        Long videoId = videoCoin.getVideoId();
        // 给视频投币的数量
        Integer amount = videoCoin.getAmount();
        if(userId == null){
            throw new ConditionException("请登录");
        }
        Video videoById = videoMapper.getVideoById(videoId);
        if(videoById == null){
            throw new ConditionException("视频不存在");
        }
        // 看一下用户的币还够吗
        Integer size = userCoinService.selectUserCoinAmounts(userId);
        // 初始化赋值
        size = size == null ? 0 : size;
        // 总数和给视频投币的数
        if(amount > size){
            throw new ConditionException("硬币数量不够");
        }
        // 查询当前登录的用户对视频已经投了多少硬币了
        VideoCoin dbVideoCoin = videoMapper.getVideoCoinByVideoIdAndUserId(videoId,userId);
        // 新增视频投币
        if(dbVideoCoin == null){
            videoCoin.setUserId(userId);
            videoCoin.setCreateTime(new Date());
            videoMapper.addVideoCoin(videoCoin);
        }else {
            // 获取视频已经投币的数量
            Integer dbAmount = dbVideoCoin.getAmount();
            dbAmount += amount;
            videoCoin.setUserId(userId);
            videoCoin.setAmount(dbAmount);
            videoCoin.setUpdateTime(new Date());
            // 更新视频投币的数量
            videoMapper.updateVideoCoin(videoCoin);
        }
        // 更新用户的币
        userCoinService.updateUserCoinAmount(userId,(size-amount));
    }
    // 查询视频投币数量
    @Override
    public Map<String, Object> getVideoCoins(Long videoId, Long userId) {
        // 查询视频的币
        Long videoCoinsAmount = videoMapper.getVideoCoinsAmount(videoId);
        // 判断视频有没有投币过
        VideoCoin videoCollection = videoMapper.getVideoCoinByVideoIdAndUserId(videoId, userId);
        boolean like = videoCollection != null;
        Map<String, Object> result = new HashMap<>();
        result.put("count", videoCoinsAmount);
        result.put("like", like);
        return result;
    }


}
