package com.bilibil.mapper;

import com.bilibil.entity.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Date:  2023/8/30
 */
@Mapper
public interface VideoMapper {

    // 用户上传添加视频
    Integer addVideo(Video video);
    // 批量添加视频标签
    Integer batchAddVideoTags(List<VideoTag> tegList);
    // 满足筛选条件一共有多少条
    Integer pageCountVideos(Map<String, Object> map);
    // 查该标签下的视频
    List<Video> pageListVoides(Map<String, Object> map);
    // 根据视频id视频信息
    Video getVideoById(Long videoId);
    // 当前用户视频有没有被当前用户点赞过
    VideoLike getVideoLikeByVideoIdAndUserId(@Param("videoId")Long videoId , @Param("userId")Long userId);
    // 给用户添加点赞记录
    void addVideoLike(VideoLike videoLike);

    // 查询点赞数量
    Long  getVideoLikes(Long videoId);
    // 取消点赞
    void deleteVideoLike(@Param("videoId")Long videoId, @Param("userId")Long userId);
    // 删除原有的视频收藏
    void deleteVideoCollection(@Param("videoId")Long videoId , @Param("userId")Long userId);
    // 添加视频收藏
    void addVideoCollection(VideoCollection videoCollection);
    // 获取用户收藏的条数
    Long getVideoCollections(Long videoId);
    // 视频收藏
    VideoCollection getVideoCollectionByVideoIdAndUserId(@Param("videoId") Long videoId
            , @Param("userId") Long userId);
    // 查询当前登录的用户对视频已经投了多少硬币了
    VideoCoin getVideoCoinByVideoIdAndUserId(@Param("videoId") Long videoId, @Param("userId") Long userId);
    // 新增视频投币
    void addVideoCoin(VideoCoin videoCoin);
    // 更新视频投币的数量
    void updateVideoCoin(VideoCoin videoCoin);
    // 查询视频的币
    Long getVideoCoinsAmount(Long videoId);
}
