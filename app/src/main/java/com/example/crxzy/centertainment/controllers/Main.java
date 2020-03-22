package com.example.crxzy.centertainment.controllers;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.crxzy.centertainment.R;
import com.example.crxzy.centertainment.activities.SearchActivity;
import com.example.crxzy.centertainment.models.NetApi;
import com.example.crxzy.centertainment.system.FirstPageBase;
import com.example.crxzy.centertainment.system.MainActivity;
import com.example.crxzy.centertainment.system.PageBase;
import com.example.crxzy.centertainment.system.QuickPageModel;
import com.example.crxzy.centertainment.tools.Network;
import com.example.crxzy.centertainment.tools.Tool;
import com.example.crxzy.centertainment.views.CardBox;
import com.example.crxzy.centertainment.views.LabelBox;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main extends FirstPageBase {

    private String[] mAllLabels;
    private PopupWindow mPopupWindow;
    private View mPopupView;
    private AutoCompleteTextView mPopupMarkAutoComplete;
    private AutoCompleteTextView mPopupFilterAutoComplete;
    private MyHandler mHandler;
    private boolean mIsLoadedAllLabels = false;
    private LabelBox mPopupFilterContainer;
    private LabelBox mPopupMarkContainer;
    private ViewGroup mPopupLanguageSelector;
    private RadioGroup mPopupSyasinnOrderSelector;

    public Main(MainActivity activity, QuickPageModel.Page pageModel) {
        super (activity, pageModel);
    }

    @Override
    public void onInitiation() {
        mHandler = new MyHandler (this);
        super.onInitiation ( );
        initCommonConfigPopup ( );
    }

    @Override
    public void onHeaderInitialize(View header) {
        ((LinearLayout) header).getChildAt (0).setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                mActivity.mRootPage.mMainLayout.openDrawer (Gravity.START);
            }
        });
        header.findViewById (R.id.header_main_def).setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                if (mApp.mUser.isUserConfigLoaded) {
                    Tool.setWindowAlpha (mActivity, 0.6f);
                    JSONObject object = (JSONObject) mApp.mUser.getConfig ("picture_common");
                    if (object == null) {
                        ((CheckBox) mPopupLanguageSelector.getChildAt (0)).setChecked (true);
                        ((CheckBox) mPopupLanguageSelector.getChildAt (1)).setChecked (true);
                        ((CheckBox) mPopupLanguageSelector.getChildAt (2)).setChecked (true);
                    } else {
                        try {
                            JSONArray lang = object.getJSONArray ("language");
                            for (int index = 0; index < lang.length ( ); index++) {
                                for (int index2 = 0; index2 < mPopupLanguageSelector.getChildCount ( ); index2++) {
                                    if (((String) ((CheckBox) mPopupLanguageSelector.getChildAt (index2)).getText ( )).toLowerCase ( ).equals (lang.getString (index))) {
                                        ((CheckBox) mPopupLanguageSelector.getChildAt (index2)).setChecked (true);
                                    }
                                }
                            }
                            JSONArray mark = object.getJSONArray ("mark");
                            for (int index = 0; index < mark.length ( ); index++) {
                                mPopupMarkContainer.addLabel (new LabelBox.CancelAbleLabel (mActivity, mark.getString (index)));
                            }
                            JSONArray filter = object.getJSONArray ("filter");
                            for (int index = 0; index < filter.length ( ); index++) {
                                mPopupFilterContainer.addLabel (new LabelBox.CancelAbleLabel (mActivity, filter.getString (index)));
                            }
                            String syasinn_order = object.getString ("syasinn_order");
                            for (int index = 0; index < mPopupSyasinnOrderSelector.getChildCount ( ); index++) {
                                if (syasinn_order.equals ((String) ((RadioButton) mPopupSyasinnOrderSelector.getChildAt (index)).getText ( ))) {
                                    ((RadioButton) mPopupSyasinnOrderSelector.getChildAt (index)).setChecked (true);
                                    break;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace ( );
                        }
                    }
                    mPopupWindow.showAtLocation (mView, Gravity.CENTER, 0, 0);
                } else {
                    Toast.makeText (mActivity, "正在加载用户设置，请稍候...", Toast.LENGTH_SHORT).show ( );
                }
            }
        });
    }

    @Override
    public void selectPage(int index) {
        if (mPageModel.mIsInitialize && mPageModel.getIndex ("picture") == index && index == mPageModel.currentChildIndex) {
            QuickPageModel.Page child = mPageModel.getChild (index);
            PageBase page = child.getChild (child.currentChildIndex).getController ( );
            ((CardBox) page.mView).smoothScrollToPosition (0);
        } else {
            super.selectPage (index);
        }
    }

    private void initCommonConfigPopup() {
        mPopupView = View.inflate (mActivity, R.layout.popup_picture_setting, null);
        mPopupLanguageSelector = mPopupView.findViewById (R.id.popup_picture_language_selector);
        mPopupMarkAutoComplete = mPopupView.findViewById (R.id.popup_picture_mark_auto_complete);
        mPopupFilterAutoComplete = mPopupView.findViewById (R.id.popup_picture_filter_auto_complete);
        mPopupFilterContainer = mPopupView.findViewById (R.id.popup_picture_filter_container);
        mPopupMarkContainer = mPopupView.findViewById (R.id.popup_picture_mark_container);
        mPopupSyasinnOrderSelector = mPopupView.findViewById (R.id.popup_picture_syasinn_order_selector);
        mPopupWindow = new PopupWindow (mPopupView, (int) (Tool.getScreenWidth (mActivity) * 0.8), ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setTouchable (true);
        mPopupWindow.setFocusable (true);
        mPopupWindow.setOutsideTouchable (true);
        mPopupWindow.setOnDismissListener (new PopupWindow.OnDismissListener ( ) {
            @Override
            public void onDismiss() {
                Tool.setWindowAlpha (mActivity, 1f);
            }
        });
        mPopupView.findViewById (R.id.popup_picture_add_mark).setOnClickListener (new MyPopupAddButtonDownListener (mPopupMarkAutoComplete));
        mPopupMarkAutoComplete.setOnItemClickListener (new MyPopupAddLabelListener (mPopupMarkAutoComplete, mPopupMarkContainer));
        mPopupMarkAutoComplete.setOnKeyListener (new MyPopupEnterDownListener (mPopupMarkAutoComplete));

        mPopupView.findViewById (R.id.popup_picture_add_filter).setOnClickListener (new MyPopupAddButtonDownListener (mPopupFilterAutoComplete));
        mPopupFilterAutoComplete.setOnItemClickListener (new MyPopupAddLabelListener (mPopupFilterAutoComplete, mPopupFilterContainer));
        mPopupFilterAutoComplete.setOnKeyListener (new MyPopupEnterDownListener (mPopupFilterAutoComplete));

        mPopupView.findViewById (R.id.popup_picture_save).setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                List <String> marked_labels = mPopupMarkContainer.getAllLabels ( );
                List <String> filter_labels = mPopupFilterContainer.getAllLabels ( );
                List <String> language_labels = new ArrayList <> ( );
                for (int index = 0; index < mPopupLanguageSelector.getChildCount ( ); index++) {
                    CheckBox checkBox = (CheckBox) mPopupLanguageSelector.getChildAt (index);
                    if (checkBox.isChecked ( )) {
                        language_labels.add (((String) checkBox.getText ( )).toLowerCase ( ));
                    }
                }
                String syasinn_order = null;
                for (int index = 0; index < mPopupSyasinnOrderSelector.getChildCount ( ); index++) {
                    if (((RadioButton) mPopupSyasinnOrderSelector.getChildAt (index)).isChecked ( )) {
                        syasinn_order = (String) ((RadioButton) mPopupSyasinnOrderSelector.getChildAt (index)).getText ( );
                        break;
                    }
                }
                if (syasinn_order == null) {
                    syasinn_order = "Asc";
                }
                JSONObject json = new JSONObject ( );
                try {
                    json.put ("language", new JSONArray (language_labels));
                    json.put ("mark", new JSONArray (marked_labels));
                    json.put ("filter", new JSONArray (filter_labels));
                    json.put ("syasinn_order", syasinn_order);
                    mApp.mUser.putConfig ("picture_common", json);
                    mPopupWindow.dismiss ( );
                    Toast.makeText (mActivity, "SAVED", Toast.LENGTH_SHORT).show ( );
                } catch (JSONException e) {
                    e.printStackTrace ( );
                }
            }
        });

        mPopupView.findViewById (R.id.popup_picture_reset).setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {

            }
        });
    }

    class MyPopupAddButtonDownListener implements View.OnClickListener {
        private final AutoCompleteTextView mAutoCompleteTextView;

        MyPopupAddButtonDownListener(AutoCompleteTextView autoCompleteTextView) {
            mAutoCompleteTextView = autoCompleteTextView;
        }

        @Override
        public void onClick(View v) {
            if (mIsLoadedAllLabels) {
                mAutoCompleteTextView.setVisibility (View.VISIBLE);
            } else {
                NetApi.getAllLabels (Main.this, "getAllLabelsSuccess");
                mAutoCompleteTextView.setVisibility (View.VISIBLE);
            }
        }
    }

    class MyPopupEnterDownListener implements View.OnKeyListener {
        private final AutoCompleteTextView mAutoCompleteTextView;

        MyPopupEnterDownListener(AutoCompleteTextView autoCompleteTextView) {
            mAutoCompleteTextView = autoCompleteTextView;
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                mAutoCompleteTextView.clearFocus ( );
                mAutoCompleteTextView.setVisibility (View.GONE);
                WindowManager windowManager = mActivity.getWindowManager ( );
                InputMethodManager imm = (InputMethodManager) mActivity.getSystemService (Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput (0, InputMethodManager.HIDE_NOT_ALWAYS);
                return true;
            }
            return false;
        }
    }

    class MyPopupAddLabelListener implements AdapterView.OnItemClickListener {
        private final LabelBox mLabelBox;
        private final AutoCompleteTextView mAutoCompleteTextView;

        MyPopupAddLabelListener(AutoCompleteTextView autoCompleteTextView, LabelBox labelBox) {
            mAutoCompleteTextView = autoCompleteTextView;
            mLabelBox = labelBox;
        }

        @Override
        public void onItemClick(AdapterView <?> parent, View view, int position, long id) {
            TextView v = (TextView) view;
            mLabelBox.addLabel (new LabelBox.CancelAbleLabel (mActivity, (String) v.getText ( )));
            mAutoCompleteTextView.setText ("");
        }
    }

    public void getAllLabelsSuccess(Network.Response response) {
        JSONArray json = (JSONArray) response.content;
        mAllLabels = new String[json.length ( )];
        for (int index = 0; index < json.length ( ); index++) {
            try {
                mAllLabels[index] = json.getString (index);
            } catch (JSONException e) {
                e.printStackTrace ( );
            }
        }
        Message message = mHandler.obtainMessage ( );
        message.what = MyHandler.ADD_LABEL_ADAPT;
        mHandler.sendMessage (message);
    }

    static class MyHandler extends PageHandler {

        static final int ADD_LABEL_ADAPT = 100;

        MyHandler(PageBase activity) {
            super (activity);
        }

        @Override
        public void handleMessage(Message msg) {
            Main pageBase = (Main) mOuterClass.get ( );
            if (msg.what == ADD_LABEL_ADAPT) {
                pageBase.setPopupLabelAutoComplete ( );
            }
        }
    }

    private void setPopupLabelAutoComplete() {
        mPopupMarkAutoComplete.setAdapter (new ArrayAdapter <> (mActivity,
                android.R.layout.simple_list_item_1, mAllLabels));
        mPopupFilterAutoComplete.setAdapter (new ArrayAdapter <> (mActivity,
                android.R.layout.simple_list_item_1, mAllLabels));
        mIsLoadedAllLabels = true;
    }

    @Override
    public void onHeaderShow(View header) {

    }

    @Override
    public void onShow() {
    }

    @Override
    public void setPageMap(Map <String, String[]> secondPageMap) {
        secondPageMap.put ("picture", new String[]{"图片", "❀"});
        secondPageMap.put ("video", new String[]{"视频", "▷"});
        secondPageMap.put ("article", new String[]{"文章", "✎"});
        secondPageMap.put ("personal", new String[]{"个人", "☻"});
    }
}