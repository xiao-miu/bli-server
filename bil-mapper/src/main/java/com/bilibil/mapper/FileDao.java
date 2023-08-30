package com.bilibil.mapper;

import com.bilibil.entity.File;
import org.apache.ibatis.annotations.Mapper;



/**
 * Date:  2023/8/30
 */
@Mapper
public interface FileDao {
    // 天剑文件
    Integer addFile(File file);
    // 获取文件
    File getFileByMD5(String md5);
}
