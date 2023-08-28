package com.bilibil.service.impl;


import com.bilibil.mapper.DemoMapper;
import com.bilibil.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Date:  2023/8/18
 */
@Service
public class DemoSerIMPL implements DemoService {
    @Autowired
    protected DemoMapper demoMapper;

    @Override
    public Map<String, Object> query(Long id) {
        return demoMapper.query(id);
    }
}
