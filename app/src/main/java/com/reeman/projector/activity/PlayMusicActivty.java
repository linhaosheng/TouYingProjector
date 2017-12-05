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
import android.widget.SeekBar;
import android.widget.TextView;

import com.hpplay.bean.MediaStateInfo;
import com.hpplay.callback.CastDeviceCallback;
import com.hpplay.callback.ExecuteResultCallBack;
import com.hpplay.link.HpplayLinkControl;
import com.reeman.projector.MyApplication;
import com.reeman.projector.R;
import com.reeman.projector.bean.DataInfo;
import com.reeman.projector.bean.VideoInfo;

import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by haoshenglin on 2017/12/4.
 */

public class PlayMusicActivty extends AppCompatActivity {

    @BindView(R.id.pre)
    ImageView pre;
    @BindView(R.id.play)
    ImageView play;
    @BindView(R.id.next)
    ImageView next;
    @BindView(R.id.back)
    ImageView back;
    final static int PLAY = 0x12;
    final static int STOP = 0x13;
    final static int SEEK_BAR = 0x14;
    private final static int PROGRESS_STATUS  =0x15;
    @BindView(R.id.music_title)
    TextView musicTitle;
    @BindView(R.id.author)
    TextView author;
    @BindView(R.id.progress)
    AppCompatSeekBar progress;
    @BindView(R.id.all_time)
    TextView allTime;
    @BindView(R.id.current_time)
    TextView current_time;
    private boolean isStop;
    private int position;
    private SimpleDateFormat dateFormat;
    private  MediaStateInfo videoInfo;
    private DataInfo dataInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_music);
        ButterKnife.bind(this);
        initView();
    }

    Handler handler  =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            if (what==PROGRESS_STATUS){
                //getDeviceState();
                Log.i("time==",dataInfo.getDuration()+"");
                progress.setMax((int) dataInfo.getDuration());
                progress.setProgress(videoInfo.getPosition()*1000);
                current_time.setText(dateFormat.format(videoInfo.getPosition()*1000));
            }
        }
    };
    private void initView() {
        position = getIntent().getIntExtra("position", 0);
        dateFormat = new SimpleDateFormat("mm:ss");
        setTitleAndAuthor();
        progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int rate = seekBar.getProgress() / 1000;
                seekBar(rate);
            }
        });
        getDeviceState();
    }

    private void setTitleAndAuthor() {
        dataInfo = MyApplication.instance.getMusicList().get(position);
        String format = dateFormat.format(dataInfo.getDuration());
        allTime.setText(format);
        author.setText(dataInfo.getArtist());
        musicTitle.setText(dataInfo.getDisplayName());
        progress.setMax((int) dataInfo.getDuration());
    }

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

    public void seekBar(int rate) {
        MainActivity.hpplayLinkControl.castSeek(new ExecuteResultCallBack() {
            @Override
            public void onResultDate(Object o, int i) {

            }
        }, SEEK_BAR, rate);
    }

    @OnClick({R.id.pre, R.id.play, R.id.next, R.id.back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.pre:
                if (position == 0) {
                    position = 0;
                } else {
                    position = position - 1;
                }
                playMusic(position);
                break;
            case R.id.play:
                if (!isStop) {
                    isStop = true;
                    play.setImageResource(R.mipmap.stop);
                    play(false);
                } else {
                    play.setImageResource(R.mipmap.play);
                    isStop = false;
                    play(true);
                }
                break;
            case R.id.next:
                playNext();
                break;
            case R.id.back:
                finish();
                break;
        }
    }

    private void playNext() {
        int size = MyApplication.instance.getMusicList().size();
        if (position == (size - 1)) {
            position = size - 1;
        } else {
            position = position + 1;
        }
        playMusic(position);
    }

    private void play(boolean isPlay) {
        MainActivity.hpplayLinkControl.castPlayControl(new ExecuteResultCallBack() {
            @Override
            public void onResultDate(Object o, int i) {

            }
        }, PLAY, isPlay);
    }


    private void playMusic(int position) {
        setTitleAndAuthor();
        isStop = false;
        DataInfo dataInfo = MyApplication.instance.getMusicList().get(position);
        String url = MyApplication.instance.getDataInfoStringHashMap().get(dataInfo);
        MainActivity.hpplayLinkControl.castStartMediaPlay(new ExecuteResultCallBack() {
            @Override
            public void onResultDate(Object o, int i) {
            }
        }, 1, url, HpplayLinkControl.PUSH_MUSIC);
    }

}
