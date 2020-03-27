package com.hujinbo.trend.service;

import com.hujinbo.trend.client.IndexDataClient;
import com.hujinbo.trend.pojo.IndexData;
import com.hujinbo.trend.pojo.Profit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BackTestService {
    @Autowired
    IndexDataClient indexDataClient;

    //列出某指数所有数据的方法
    public List<IndexData> listIndexData(String code) {
        List<IndexData> result = indexDataClient.getIndexData(code);
        Collections.reverse(result);
//        for (IndexData indexData : result) {
//            System.out.println(indexData.getDate());
//        }
        return result;
    }

    //模拟回测的方法
    public Map<String, Object> simulate(int ma, float sellRate, float buyRate, float serviceCharge, List<IndexData> indexDatas) {
        List<Profit> profits =  new ArrayList<>();
        float initCash = 1000;  //假设初始现金为1000
        float cash = initCash;  //1000充值交易所
        float share = 0;    //初始化份额
        float value = 0;    //初始化价值

        float init = 0;
        if (!indexDatas.isEmpty()) {
            init = indexDatas.get(0).getClosePoint();   //已第一天收盘价为基准值
        }
        for (int i = 0; i < indexDatas.size(); i ++) {
            IndexData indexData = indexDatas.get(i);
            float closePoint = indexData.getClosePoint();
            float avg = getMA(i, ma, indexDatas);
            float max = getMax(i, ma, indexDatas);

            float increase_rate = closePoint / avg;
            float decrease_rate = closePoint / max;
            if (avg != 0) { //天数足够多
                //buy 超过了ma均线
                if (increase_rate > buyRate) {
                    //如果目前没有份额手持现金则买入份额
                    if (0 == share) {
                        share = cash / closePoint;
                        cash = 0;
                    } else {
                        //持币观望 do nothing
                    }
                } else if (decrease_rate < sellRate) {
                    //目前达到卖出标准, 如果手头没有现金持有份额则卖出份额换取现金
                    if (0 != share) {
                        cash = closePoint * share * (1 - serviceCharge);
                        share = 0;
                    } else {
                        //持股观望 do nothing
                    }
                } else {
                    //既达不到买入也达不到卖出标准, 观望 do nothing
                }
            }

            //计算价值
            if (share != 0) {   //目前持股
                value = share * closePoint;
            } else {    //目前持现金
                value = cash;
            }
            float rate = value / initCash;  //盈利率 + 1
            Profit profit = new Profit();
            profit.setDate(indexData.getDate());
            profit.setValue(rate * init);   //当日价值设置为以第一天收盘价为基准加上利润之后的价值
            System.out.println("profit.value:" + profit.getValue());
            profits.add(profit);
        }
        Map<String,Object> map = new HashMap<>();
        map.put("profits", profits);
        return map;
    }

    //获取最高点的方法
    private static float getMax(int i, int day, List<IndexData> list) {
        int start = i-1-day;
        if(start<0)
            start = 0;
        int now = i-1;

        if(start<0)
            return 0;

        float max = 0;
        for (int j = start; j < now; j++) {
            IndexData bean =list.get(j);
            if(bean.getClosePoint()>max) {
                max = bean.getClosePoint();
            }
        }
        return max;
    }

    //获取均线的方法
    private static float getMA(int i, int ma, List<IndexData> list) {
        int start = i-1-ma;
        int now = i-1;

        if(start<0)
            return 0;

        float sum = 0;
        float avg = 0;
        for (int j = start; j < now; j++) {
            IndexData bean =list.get(j);
            sum += bean.getClosePoint();
        }
        avg = sum / (now - start);
        return avg;
    }

}
