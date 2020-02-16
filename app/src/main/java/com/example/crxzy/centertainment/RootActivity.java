package com.example.crxzy.centertainment;

import com.example.crxzy.centertainment.system.ActivityBase;

import java.util.Map;

public class RootActivity extends ActivityBase {
    @Override
    protected void initFirstPageMap(Map <String, String[]> firstPageMap) {
        firstPageMap.put ("main", new String[]{"⌂", "首页"});
        firstPageMap.put ("history", new String[]{"↺", "历史记录"});
        firstPageMap.put ("subscribe", new String[]{"✉", "订阅"});
    }
}
