package com.example.crxzy.centertainment.system;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.Toast;

import com.example.crxzy.centertainment.R;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    public QuickPageModel mQuickPageModel;
    private boolean mBackKeyPressed = false;
    public ZeroPageBase mRootPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.index);
        onInitiation ( );
    }

    public void onInitiation() {
        mQuickPageModel = new QuickPageModel (this);
        mRootPage = (ZeroPageBase) mQuickPageModel.getRoot ( ).mController;
        mRootPage.onInitiation ();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!mRootPage.closeDrawer ( )) {
                if (!mBackKeyPressed) {
                    Toast.makeText (this, "再按一次退出", Toast.LENGTH_SHORT).show ( );
                    mBackKeyPressed = true;
                    new Timer ( ).schedule (new TimerTask ( ) {
                        @Override
                        public void run() {
                            mBackKeyPressed = false;
                        }
                    }, 2000);
                    return true;
                } else {
                    finish ( );
                }
            }
            return true;
        }
        return super.onKeyDown (keyCode, event);
    }
}
