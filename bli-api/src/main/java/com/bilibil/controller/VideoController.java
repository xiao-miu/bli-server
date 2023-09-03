package com.bilibil.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bilibil.entity.*;
import com.bilibil.service.VideoService;
import com.bilibil.support.UserSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Date:  2023/8/30
 */
@RestController
public class VideoController {
    @Autowired
    private VideoService videoService;
    @Autowired
    private UserSupport userSupport;
    // 用户上传添加视频
    @PostMapping("/videos")
    public JsonResponse<String> addVideos(@RequestBody Video video){
        Long userId = userSupport.getCurrentUserId();
        video.setUserId(userId);
        videoService.addVideo(video);
        return JsonResponse.success();
    }
    //    /videos?size=4&number=1&area=1
    // 分页查询视频（前端瀑布流视频列表）  Number 第几页  ， area分区  （每个分区加载多少条视频）
    @GetMapping("/videos")
    public JsonResponse<PageResult<Video>> pageListVideos(Integer size , Integer number , String area){
        PageResult<Video> result = videoService.pageListVideos(size,number, area);
        return new JsonResponse<>(result);
    }
    // 在线视频观看（下载）,通过分片的方式
    // 请求的是视频的分片内容
    // 因为是通过流的方式去传输，流会写在Http响应的输出流里，所以不需要返回数据
    // HttpServletRequest前端过来的请求的参数，HttpServletRequest输出流
    @GetMapping("/videeo-slices")
    public void viewVideoOnlineBySkices(HttpServletRequest request
            , HttpServletResponse servletRequest, String url) throws Exception {
        videoService.viewVideoOnlineBySkices(request, servletRequest, url);
    }
    /**
     * 点赞视频
     */
    @PostMapping("/video-likes")
    public JsonResponse<String> addVideoLike(@RequestParam Long videoId){
        Long userId = userSupport.getCurrentUserId();
        videoService.addVideoLike(videoId, userId);
        return JsonResponse.success();
    }

    /**
     * 取消点赞视频
     */
    @DeleteMapping("/video-likes")
    public JsonResponse<String> deleteVideoLike(@RequestParam Long videoId){
        Long userId = userSupport.getCurrentUserId();
        videoService.deleteVideoLike(videoId, userId);
        return JsonResponse.success();
    }

    /**
     * 查询视频点赞数量
     */
    @GetMapping("/video-likes")
    public JsonResponse<Map<String, Object>> getVideoLikes(@RequestParam Long videoId){
        // 就算是没有登录也可以观看视频
        Long userId = null;
        try{
            // 获取当前用户登录的ID
            userId = userSupport.getCurrentUserId();
            // 检查用户Id是否token异常
        }catch (Exception ignored){}
        // 查询点赞的用户数量
        Map<String, Object> result = videoService.getVideoLikes(videoId, userId);
        return new JsonResponse<>(result);
    }

    /**
     * 收藏视频
     */
    @PostMapping("/video-collections")
    public JsonResponse<String> addVideoCollection(@RequestBody VideoCollection videoCollection){
        Long userId = userSupport.getCurrentUserId();
        videoService.addVideoCollection(videoCollection, userId);
        return JsonResponse.success();
    }

    /**
     * 取消收藏视频
     */
    @DeleteMapping("/video-collections")
    public JsonResponse<String> deleteVideoCollection(@RequestParam Long videoId){
        Long userId = userSupport.getCurrentUserId();
        videoService.deleteVideoCollection(videoId, userId);
        return JsonResponse.success();
    }

    /**
     * 查询视频收藏数量
     */
    @GetMapping("/video-collections")
    public JsonResponse<Map<String, Object>> getVideoCollections(@RequestParam Long videoId){
        Long userId = null;
        try{
            userId = userSupport.getCurrentUserId();
        }catch (Exception ignored){}
        Map<String, Object> result = videoService.getVideoCollections(videoId, userId);
        return new JsonResponse<>(result);
    }

    /**
     * 视频投币
     */
    @PostMapping("/video-coins")
    public JsonResponse<String> addVideoCoins(@RequestBody VideoCoin videoCoin){
        Long userId = userSupport.getCurrentUserId();
        videoService.addVideoCoins(videoCoin, userId);
        return JsonResponse.success();
    }

    /**
     * 查询视频投币数量
     */
    @GetMapping("/video-coins")
    public JsonResponse<Map<String, Object>> getVideoCoins(@RequestParam Long videoId){
        // 区分用户有没有登录
        Long userId = null;
        try{
            userId = userSupport.getCurrentUserId();
        }catch (Exception ignored){}
        Map<String, Object> result = videoService.getVideoCoins(videoId, userId);
        return new JsonResponse<>(result);
    }
}
