package com.hujinbo.trend.controller;

import java.util.List;

import com.hujinbo.trend.config.IpConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hujinbo.trend.pojo.Index;
import com.hujinbo.trend.service.IndexService;

@RestController
public class IndexController {
    @Autowired
    IndexService indexService;
    @Autowired
    IpConfiguration ipConfiguration;

    @GetMapping("codes")
    @CrossOrigin
    public List<Index> codes() throws Exception {
        System.out.println("current instance's port is " + ipConfiguration.getPort());
        return indexService.get();
    }

}