package com.reeman.projector.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.hpplay.callback.ExecuteResultCallBack;
import com.reeman.projector.R;
import com.reeman.projector.utils.ImageUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by haoshenglin on 2017/11/29.
 */

public class ShowImgActivity extends AppCompatActivity {

    @BindView(R.id.show_img)
    ImageView showImg;
    @BindView(R.id.title)
    TextView title;
    final static int STOP_PLAY = 0x11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        ButterKnife.bind(this);
        String imgUrl = getIntent().getStringExtra("img_url");
        String title = getIntent().getStringExtra("img_title");
        ImageUtil.loadImg(this, imgUrl, showImg);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        MainActivity.hpplayLinkControl.stopPhotoPlay(new ExecuteResultCallBack() {
//            @Override
//            public void onResultDate(Object o, int i) {
//              boolean isExit = (Boolean) o;
//                Log.i("isExit==","===="+isExit);
//            }
//        },STOP_PLAY);

        MainActivity.hpplayLinkControl.quitPhotoPlay(new ExecuteResultCallBack() {
            @Override
            public void onResultDate(Object o, int i) {

            }
        },STOP_PLAY);
    }
}
