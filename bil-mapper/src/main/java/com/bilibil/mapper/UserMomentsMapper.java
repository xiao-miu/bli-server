package com.bilibil.mapper;

import com.bilibil.entity.UserMoment;
import org.apache.ibatis.annotations.Mapper;

/**
 * Date:  2023/8/23
 */
@Mapper
public interface UserMomentsMapper {

    void addUserMoments(UserMoment userMoment);
}
