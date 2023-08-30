package com.bilibil.service;

import org.apache.ibatis.annotations.Param;

/**
 * Date:  2023/9/3
 */
public interface UserCoinServer {
    // 查询用户的币还有多少
    Integer selectUserCoinAmounts(Long userId);
    // 更新用户的币
    void updateUserCoinAmount(Long userId, Integer size);
}
