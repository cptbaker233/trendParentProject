package com.hujinbo.trend.job;

import java.util.List;

import cn.hutool.core.date.DateUtil;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.hujinbo.trend.pojo.Index;
import com.hujinbo.trend.service.IndexDataService;
import com.hujinbo.trend.service.IndexService;

public class IndexDataSyncJob extends QuartzJobBean {

    @Autowired
    IndexService indexService;

    @Autowired
    IndexDataService indexDataService;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("定时启动: " + DateUtil.now());
        List<Index> indexes = indexService.fresh();
        for (Index index : indexes) {
            indexDataService.fresh(index.getCode());
        }
        System.out.println("定时结束: " + DateUtil.now());
    }

}
