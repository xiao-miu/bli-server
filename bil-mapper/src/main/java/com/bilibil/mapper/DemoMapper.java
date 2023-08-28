package com.bilibil.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

/**
 * Date:  2023/8/18
 */
@Mapper
public interface DemoMapper {
    public Map<String,Object> query(Long id);
}
