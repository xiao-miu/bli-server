package com.bilibil.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
 * Date:  2023/9/3
 */
@Mapper
public interface MapperUserCoin {
    Integer selectVideoCoinAmounts(Long userId);

    void updateUserCoinAmount(@Param("userId") Long userId, @Param("size") Integer size,@Param("updateTime") Date updateTime);
}