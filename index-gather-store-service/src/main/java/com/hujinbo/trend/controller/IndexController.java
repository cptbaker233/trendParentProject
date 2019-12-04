package com.hujinbo.trend.controller;

import com.hujinbo.trend.pojo.Index;
import com.hujinbo.trend.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class IndexController {
    @Autowired
    IndexService indexService;

    @GetMapping("getCodes")
    public List<Index> get() throws Exception {
        return indexService.get();
    }

    @GetMapping("freshCodes")
    public List<Index> fresh() throws Exception {
        return indexService.fresh();
    }

    @GetMapping("removeCodes")
    public String remove() throws Exception {
        indexService.remove();
        return "remove codes successfully!";
    }

}
