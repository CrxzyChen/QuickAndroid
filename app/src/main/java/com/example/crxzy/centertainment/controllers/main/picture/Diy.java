package com.example.crxzy.centertainment.controllers.main.picture;

import android.widget.Toast;

import com.example.crxzy.centertainment.system.MainActivity;
import com.example.crxzy.centertainment.system.QuickPageModel;
import com.example.crxzy.centertainment.system.ThirdPageBase;

public class Diy extends ThirdPageBase {
    public Diy(MainActivity activity, QuickPageModel.Page pageModel) {
        super (activity, pageModel);
    }

    @Override
    public void onInitiation() {
        Toast.makeText (mActivity,"test",Toast.LENGTH_SHORT ).show();
    }
}
