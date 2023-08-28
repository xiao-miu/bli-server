package com.bilibil.controller;

import com.bilibil.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * Date:  2023/8/18
 */
@RestController
public class DemocCntroller {
    @Autowired
    protected DemoService demoService;

    @GetMapping("/query/{id}")
    public Map<String,Object> query(@PathVariable Long id){
        Map<String, Object> query = demoService.query(id);
        System.out.println(query);
        return query;
    }
}
