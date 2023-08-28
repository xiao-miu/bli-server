package com.bilibil.service;

import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Date:  2023/8/18
 * @author 1
 */

public interface DemoService {
    Map<String, Object> query(Long id);
}
