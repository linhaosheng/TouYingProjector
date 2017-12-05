package com.reeman.projector.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hpplay.bean.MediaStateInfo;
import com.hpplay.callback.CastDeviceCallback;
import com.reeman.projector.MyApplication;
import com.reeman.projector.R;
import com.reeman.projector.bean.DataInfo;

import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by haoshenglin on 2017/12/5.
 */

public class VideoPlayActivity extends AppCompatActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.video_title)
    TextView videoTitle;
    @BindView(R.id.tv)
    ImageView tv;
    @BindView(R.id.current_time)
    TextView currentTime;
    @BindView(R.id.progress)
    AppCompatSeekBar progress;
    @BindView(R.id.all_time)
    TextView allTime;
    private int position;
    private DataInfo dataInfo;
    private MediaStateInfo videoInfo;
    private SimpleDateFormat dateFormat;
    private final static int PROGRESS_STATUS  =0x15;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        position = getIntent().getIntExtra("position",0);
        dataInfo = MyApplication.instance.getVideoList().get(position);
        videoTitle.setText(dataInfo.getDisplayName());
        dateFormat = new SimpleDateFormat("mm:ss");
        String format = dateFormat.format(dataInfo.getDuration());
        allTime.setText(format);
        getDeviceState();
    }

     Handler handler = new Handler(){
         @Override
         public void handleMessage(Message msg) {
             super.handleMessage(msg);
             int what = msg.what;
             if (what==PROGRESS_STATUS){
                 //getDeviceState();
                 Log.i("time==",dataInfo.getDuration()+"");
                 progress.setMax((int) dataInfo.getDuration());
                 progress.setProgress(videoInfo.getPosition()*1000);
                 currentTime.setText(dateFormat.format(videoInfo.getPosition()*1000));
                 if (dataInfo.getDuration()-(videoInfo.getPosition()*1000)<2){
                     VideoPlayActivity.this.finish();
                 }
             }
         }
     };

    //获取播放状态
    private void getDeviceState(){
        MainActivity.hpplayLinkControl.castDeviceCallback(new CastDeviceCallback() {
            @Override
            public void onDeviceCallback(Object o) {
                videoInfo = (MediaStateInfo)o;
                Log.i("videoInfo=====","count=="+videoInfo.getDuration() +"positon==="+videoInfo.getPosition());
                handler.sendEmptyMessage(PROGRESS_STATUS);
            }
        });
    }

    @OnClick({R.id.back, R.id.progress})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.progress:
                break;
        }
    }
}
