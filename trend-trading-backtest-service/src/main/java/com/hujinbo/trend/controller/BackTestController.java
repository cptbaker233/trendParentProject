package com.hujinbo.trend.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.hujinbo.trend.pojo.IndexData;
import com.hujinbo.trend.pojo.Profit;
import com.hujinbo.trend.pojo.Trade;
import com.hujinbo.trend.service.BackTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class BackTestController {
    @Autowired
    BackTestService backTestService;

    @GetMapping("/simulate/{code}/{ma}/{buyThreshold}/{sellThreshold}/{serviceCharge}/{startDate}/{endDate}")
    @CrossOrigin
    public Map<String, Object> backTest(
            @PathVariable("code") String code
            ,@PathVariable("ma") int ma
            ,@PathVariable("buyThreshold") float buyThreshold
            ,@PathVariable("sellThreshold") float sellThreshold
            ,@PathVariable("serviceCharge") float serviceCharge
            ,@PathVariable("startDate") String strStartDate
            ,@PathVariable("endDate") String strEndDate
    ) throws Exception {
        List<IndexData> allIndexDatas = backTestService.listIndexData(code);
        //获取第一天的日期和最后一天的日期
        String indexStartDate = allIndexDatas.get(0).getDate();
        String indexEndDate = allIndexDatas.get(allIndexDatas.size() - 1).getDate();
        //过滤掉非区间数据
        allIndexDatas = filterByDateRange(allIndexDatas, strStartDate, strEndDate);

        //新增趋势投资曲线部分
        //设置初始参数
        //int ma = 20;
        float sellRate = sellThreshold;
        float buyRate = buyThreshold;
        //float serviceCharge = 0.001f;
        Map<String, Object> simulateResult = backTestService.simulate(ma, sellRate, buyRate, serviceCharge, allIndexDatas);
        List<Profit> profits = (List<Profit>) simulateResult.get("profits");
        List<Trade> trades = (List<Trade>) simulateResult.get("trades");

        float years = backTestService.getYear(allIndexDatas);
        float indexIncomeTotal = (allIndexDatas.get(allIndexDatas.size() - 1).getClosePoint() - allIndexDatas.get(0).getClosePoint()) / allIndexDatas.get(0).getClosePoint();
        float indexIncomeAnnual = (float) Math.pow(1 + indexIncomeTotal, 1 / years) - 1;
        float trendIncomeTotal = (profits.get(profits.size()-1).getValue() - profits.get(0).getValue()) / profits.get(0).getValue();
        float trendIncomeAnnual = (float) Math.pow(1+trendIncomeTotal, 1/years) - 1;

        Map<String, Object> result = new HashMap<>();
        result.put("indexDatas", allIndexDatas);
        result.put("indexStartDate", indexStartDate);
        result.put("indexEndDate", indexEndDate);
        result.put("profits", profits);
        result.put("trades", trades);
        result.put("years", years);
        result.put("indexIncomeTotal", indexIncomeTotal);
        result.put("indexIncomeAnnual", indexIncomeAnnual);
        result.put("trendIncomeTotal", trendIncomeTotal);
        result.put("trendIncomeAnnual", trendIncomeAnnual);
        return result;
    }

    //根据日期过滤指数数据的方法
    private List<IndexData> filterByDateRange(List<IndexData> allIndexDatas, String strStartDate, String strEndDate) {
        //空值判断, 如果日期有空则不筛选返回全部
        if (StrUtil.isBlankOrUndefined(strStartDate) || StrUtil.isBlankOrUndefined(strEndDate)) {
            return allIndexDatas;
        }
        List<IndexData> result = new ArrayList<>();
        Date startDate = DateUtil.parse(strStartDate);
        Date endDate = DateUtil.parse(strEndDate);
        for (IndexData indexData : allIndexDatas) {
            Date date = DateUtil.parse(indexData.getDate());
            if (date.getTime() >= startDate.getTime() && date.getTime() <= endDate.getTime()) {
                result.add(indexData);
            }
        }
        return result;
    }

}
