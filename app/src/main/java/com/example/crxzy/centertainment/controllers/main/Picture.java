package com.example.crxzy.centertainment.controllers.main;

import com.example.crxzy.centertainment.system.MainActivity;
import com.example.crxzy.centertainment.system.QuickPageModel;
import com.example.crxzy.centertainment.system.SecondPageBase;

import java.util.Map;

public class Picture extends SecondPageBase {
    public Picture(MainActivity context, QuickPageModel.Page pageModel) {
        super (context, pageModel);
    }

    @Override
    public void setPageMap(Map <String, String[]> mThirdPageMap) {
        mThirdPageMap.put ("latest", new String[]{"最新"});
        mThirdPageMap.put ("recommend", new String[]{"推荐"});
    }
}
