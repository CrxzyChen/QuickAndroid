package com.example.crxzy.centertainment.controllers.main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.crxzy.centertainment.system.QuickPageModel;
import com.example.crxzy.centertainment.system.SecondPageBase;

import java.util.Map;

public class Picture extends SecondPageBase {
    public Picture(AppCompatActivity context, View view, QuickPageModel.Page pageModel) {
        super (context, view, pageModel);
    }
    @Override
    public void initThirdPageMap(Map <String, String[]> mThirdPageMap) {
        mThirdPageMap.put ("latest",new String[]{"最新"});
        mThirdPageMap.put ("recommend",new String[]{"推荐"});
    }
}
