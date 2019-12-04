package com.hujinbo.trend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.hujinbo.trend.pojo.IndexData;
import com.hujinbo.trend.service.IndexDataService;

@RestController
public class IndexDataController {
    @Autowired
    IndexDataService indexDataService;

    @GetMapping("freshIndexData/{code}")
    public String fresh(@PathVariable String code) throws Exception {
        indexDataService.fresh(code);
        return "fresh index data successfully";
    }

    @GetMapping("getIndexData/{code}")
    public List<IndexData> get(@PathVariable String code) throws Exception {
        return indexDataService.get(code);
    }

    @GetMapping("removeIndexData/{code}")
    public String remove(@PathVariable String code) {
        indexDataService.remove(code);
        return "remove index data successfully";
    }


}
