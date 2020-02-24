package com.example.crxzy.centertainment.controllers.main;

import com.example.crxzy.centertainment.system.MainActivity;
import com.example.crxzy.centertainment.system.QuickPageModel;
import com.example.crxzy.centertainment.system.SecondPageBase;

import java.util.Map;

public class Video extends SecondPageBase {
    public Video(MainActivity activity, QuickPageModel.Page pageModel) {
        super (activity, pageModel);
    }

    public void initThirdPageMap(Map <String, String[]> mThirdPageMap) {
        mThirdPageMap.put ("latest", new String[]{"最新"});
        mThirdPageMap.put ("recommend", new String[]{"推荐"});
    }
}
