package com.bilibil.service.impl;

import com.bilibil.mapper.MapperUserCoin;
import com.bilibil.service.UserCoinServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;


/*
 * Date:  2023/9/3
 */
@Service
public class UserCoinSerIMPL implements UserCoinServer {
    @Autowired
    private MapperUserCoin mapperUserCoin;
    // 查询用户的币还有多少
    @Override
    public Integer selectUserCoinAmounts(Long userId) {

        return mapperUserCoin.selectVideoCoinAmounts(userId);
    }

    @Override
    public void updateUserCoinAmount(Long userId, Integer size) {
        Date updateTime = new Date();
        mapperUserCoin.updateUserCoinAmount(userId,size,updateTime);
    }
}
