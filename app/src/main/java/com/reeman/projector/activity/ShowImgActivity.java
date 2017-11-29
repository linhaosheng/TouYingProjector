package com.reeman.projector.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        ButterKnife.bind(this);
        String imgUrl = getIntent().getStringExtra("img_url");
        String title = getIntent().getStringExtra("img_title");
        ImageUtil.loadImg(this, imgUrl, showImg);
    }
}
