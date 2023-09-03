package com.bilibil.util;

import com.bilibil.exception.ConditionException;
import com.github.tobato.fastdfs.domain.fdfs.FileInfo;
import com.github.tobato.fastdfs.domain.fdfs.MetaData;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.AppendFileStorageClient;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

/**
 * Date:  2023/8/29
 */
@Component
public class FastDFSUtil {
    // 对文件进行操作的类
    @Autowired
    private FastFileStorageClient fastFileStorageClient;
    // 断点续传的工具类(专门用来断点续传)
    @Autowired
    private AppendFileStorageClient appendFileStorageClient;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    private static final String PATH_KEY = "path-key:";
    // 当前已经上传的所有分片加起来的总大小
    private static final String UPLOADED_SIZE_KEY = "uploaded-size-key:";
    // 当前上传了多少个分片
    private static final String UPLOADED_NO_KEY = "uploaded-no-key:";

    private static final String DEFAULT_GROUP = "group1";
    // 分片的大小 2MB
    private static final int SLICE_SIZE = 1024 * 1024 * 2;
    // 接收路径
    @Value("${fdfs.http.storage-addr}")
    private String httpFdfsStorageAddr;
    // 获取文件的类型              MultipartFile根据二进制流进行文件类型转换
    public String getFileType(MultipartFile file) {
        if(file == null){
            throw new ConditionException("非法文件！");
        }
        // 获取问价名称
        String fileName = file.getOriginalFilename();
        // 名称截取(如果有多个点获取最后一个)
        int i = fileName.lastIndexOf(".");
        // 获取文件类型
        return fileName.substring(i+1);

    }
    // 上传 （第一个分片上传用的）
    public String uploadCommonFile(MultipartFile file) throws IOException {
        Set<MetaData> metaDataSet = new HashSet<>();
        // 获取文件的类型
        String fileType = this.getFileType(file);
        // 上传文件的输入流
        StorePath storePath = fastFileStorageClient.uploadFile(file.getInputStream(),
                file.getSize(), fileType, metaDataSet);
        // 获取文件的路径（所有分片上传都是依据这个路径）
        return storePath.getPath();
    }

    // 断点续传（如果网络中断，服务中断，网络崩溃，如果是大文件只能重新上传），所以要把文件分片
    public String uploadAppendFile(MultipartFile file) throws IOException {
        String fileType = this.getFileType(file);
        StorePath storePath = appendFileStorageClient.uploadAppenderFile(DEFAULT_GROUP,
                file.getInputStream(), file.getSize(), fileType);
        return storePath.getPath();
    }
    // 分片文件的添加
    //offset 实际的位置 （偏移量）
    public void modifyAppenderFile(MultipartFile file, String filePath, long offset) throws IOException {
        appendFileStorageClient.modifyFile(DEFAULT_GROUP,filePath,
                file.getInputStream(), file.getSize(), offset);
    }
    // 把文件进行MD5加密   sliceNo当前分片数   totalSliceNo总分片数，用来判断什么时候上传完成的
    public String uploadFileBySlices(MultipartFile file , String fileMD5
            , Integer sliceNo , Integer totalSliceNo) throws IOException {
        // 输入参数的判断
        if(file == null || sliceNo == null || totalSliceNo == null){
            throw new ConditionException("文件异常");
        }
        // 生成分片的Key
        String pathKey = PATH_KEY+fileMD5;
        // 当前已经上传的所有分片加起来的总大小
        String uploadSizeKey =  UPLOADED_SIZE_KEY+fileMD5;
        // 当前已经有了多少分片了
        String uploadNoKey = UPLOADED_NO_KEY+fileMD5;
        String uploadSizeStr = redisTemplate.opsForValue().get(uploadSizeKey);
        // 上传大小 （如果是第一个分片当前的大小肯定是0）
        Long uploadSize = 0L;
        // 是否为空   如果不是0看一下上传了多少了
        if(!StringUtil.isNullOrEmpty(uploadSizeStr)){
            uploadSize = Long.valueOf(uploadSizeStr);
        }
        // 进行开始上传
        // 判断前端的穿的序号是第几个
        // 上传第一个分片
        if(sliceNo == 1){
            // 调用上传的方法
            // 获取到上传的路径
            String path = this.uploadCommonFile(file);
            if(StringUtil.isNullOrEmpty(path)){
                throw new ConditionException("文件上传失败");
            }
            // 把path存储到rides中
            redisTemplate.opsForValue().set(pathKey, path);
            // 保存已将上传的序号
            redisTemplate.opsForValue().set(uploadNoKey, "1");
        }else {
            // 获取到redis上存储的路径
            String filePath = redisTemplate.opsForValue().get(pathKey);
            if(StringUtil.isNullOrEmpty(filePath)){
                throw new ConditionException("文件上传失败");
            }
            this.modifyAppenderFile(file,filePath,uploadSize);
            // 把key 对应的value进行+1
            redisTemplate.opsForValue().increment(uploadNoKey,1);
        }
        //上传完毕， 把文件的上传大小进行统一修改
        uploadSize += file.getSize();
        redisTemplate.opsForValue().set(uploadSizeKey, String.valueOf(uploadSize));
        // 所有分片上传完毕，清空redis里的相关的key和value
        String uploadNoStr = redisTemplate.opsForValue().get(uploadNoKey);
        // 当前已有上传的分片
        Integer uploadNo = Integer.valueOf(uploadNoStr);
        String resultPath = "";
        // 判断上传过程是否可以结束了
        if(sliceNo.equals(uploadNo)){
            // 拿到返回文件的路径
            resultPath =redisTemplate.opsForValue().get(pathKey);
            List<String> keyList = Arrays.asList(pathKey,uploadSizeKey,uploadNoKey);
            // 把对应列表里的key全都删除
            redisTemplate.delete(keyList);
        }
        return resultPath;
    }
    // 功能测试
    // 把一个文件转成一个或多个分片
    public void convertFileToSlices(MultipartFile multipartFile) throws IOException {
        String fileName = multipartFile.getName();
        String fileType = this.getFileType(multipartFile);
        // 转换成java自带的文件类型
        File file = this.multipartFileToFile(multipartFile);
        // 开始分片操作
        long fileLength = file.length();
        // 分片的计数器
        int count = 1;
        for (int i = 0; i < fileLength; i+=SLICE_SIZE) {
            // 文件读权限
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
            // 搜寻开始位置
            randomAccessFile.seek(i);
            byte[] bytes = new byte[SLICE_SIZE];
            // 实际读取出来的文件长度
            int len = randomAccessFile.read(bytes);
            String path = "F:\\file"+count+"."+fileType;
            File slice = new File(path);
            FileOutputStream fileOutputStream = new FileOutputStream(slice);
            fileOutputStream.write(bytes, 0, len);
            fileOutputStream.close();
            randomAccessFile.close();
            count++;
        }
        // 删除临时文件
        file.delete();
    }
    // 转换成java自带的文件类型
    public File multipartFileToFile(MultipartFile multipartFile) throws IOException {
        // 获取上传文件的原始名
        String originalFilename = multipartFile.getOriginalFilename();
        String[] fileName = originalFilename.split("\\.");
        // 生成临时文件
        File tempFile = File.createTempFile(fileName[0], "."+fileName[1]);
        // 上传文件保存到指定的目标文件
        multipartFile.transferTo(tempFile);
        return tempFile;
    }


    // 删除 根据路径删除文件
    public void deleteFile(String filePath){
        fastFileStorageClient.deleteFile(filePath);
    }
    // 在线视频观看,通过分片的方式
    public void viewVideoOnlineBySkices(HttpServletRequest request,
                                        HttpServletResponse response, String url) throws Exception {
        FileInfo fileInfo = fastFileStorageClient.queryFileInfo(DEFAULT_GROUP, url);
        // 文件大小
        long fileSize = fileInfo.getFileSize();
        // 实际访问的文件路径
        String fileUrl = httpFdfsStorageAddr+url;
        // 获取请求头
        // 获取到所有的相关请求头的信息
        Enumeration<String> headerNames = request.getHeaderNames();
        Map<String, Object> map = new HashMap<>();
        // hasMoreElements 判断当前枚举类型里还有没有更多的数据
        while (headerNames.hasMoreElements()){
            String header = headerNames.nextElement();
            map.put(header, request.getHeader(header));
        }
        // 前端穿过来的的信息，获取信息 range   文件分片的大小范围
        String rangeStr = request.getHeader("Range");
        String[] range;
        // 判断 Range是否为空
        if(StringUtil.isNullOrEmpty(rangeStr)){
            // 赋值   开始位置是从0开始
            rangeStr = "bytes=0-"+(fileSize-1);
        }
        range = rangeStr.split("bytes=|-");
        long begin = 0;
        // range >= 2  说明只有起始位置没有结束位置
        if(range.length >= 2){
            begin = Long.parseLong(range[1]);
        }
        // 若果长度终止位置
        long end = fileSize-1;
        if(range.length >= 3){
            end = Long.parseLong(range[2]);
        }
        // 实际分片长度
        long len = (end - begin) + 1;
        // 实际响应头
        String contentRange = "bytes"+begin+"-"+end+"/"+fileSize;
        response.setHeader("Content-Range", contentRange);
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("Content-Type", "video/mp4");
        response.setContentLength((int)len);
        response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
        HttpUtil.get(url, map, response);
    }


    // 下载（通过nginx，https协议获取文件。可以通过http连接来获取到文件）

}
