package com.reeman.projector.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hpplay.bean.CastDeviceInfo;
import com.hpplay.callback.CastDeviceServiceCallback;
import com.hpplay.callback.ConnectStateCallback;
import com.hpplay.callback.ExecuteResultCallBack;
import com.hpplay.callback.TransportCallBack;
import com.hpplay.link.HpplayLinkControl;
import com.reeman.projector.MyApplication;
import com.reeman.projector.R;
import com.reeman.projector.bean.DataInfo;
import com.reeman.projector.fragment.MirrorFragment;
import com.reeman.projector.fragment.MusicFragment;
import com.reeman.projector.fragment.PictureFragment;
import com.reeman.projector.fragment.VideoFragment;
import com.reeman.projector.utils.DataScanUtil;
import com.reeman.projector.utils.ServerUtil;
import com.reeman.projector.view.WaitDialogUtil;
import com.ycl.tabview.library.TabView;
import com.ycl.tabview.library.TabViewChild;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.leefeng.promptlibrary.PromptButton;
import me.leefeng.promptlibrary.PromptButtonListener;
import me.leefeng.promptlibrary.PromptDialog;

public class MainActivity extends AppCompatActivity implements ExecuteResultCallBack, TransportCallBack, PromptButtonListener {


    @BindView(R.id.tabView)
    TabView tabView;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.connect)
    TextView connect;
    @BindView(R.id.connetc_status)
    TextView connetc_status;
    private final static String TAG = "MainActivity==";
    private final static String KEY = "4b51c13b09a6d58445ebeb57dad95471";
    private final static int FIND_DEVICE = 0x11;
    private final static int CONNECT_DEVICE = 0X12;
    private final static int EXIT_STOP_PLAY = 0x13;
    public static HpplayLinkControl hpplayLinkControl;
    ArrayList<DataInfo> dataInfos = new ArrayList<>();
    HashMap<DataInfo, String> dataInfoStringHashMap;
    List<CastDeviceInfo> castDeviceInfoList = new ArrayList<>();
    FragmentManager fragmentManager;
    FragmentTransaction transaction;

    private WaitDialogUtil waitDialogUtil;
    PromptDialog promptDialog;
    PromptButton promptButton;
    private boolean isFindConnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
        initData();
        connect();
        getDataAndBuildServer();
    }

    private void initView() {
        List<TabViewChild> tabViewChildList = new ArrayList<>();
        TabViewChild tabViewChild01 = new TabViewChild(R.mipmap.redpic, R.mipmap.whitepic, "相册", PictureFragment.newInstance("相册"));
        TabViewChild tabViewChild02 = new TabViewChild(R.mipmap.redvideo, R.mipmap.whitevideo, "视频", VideoFragment.newInstance("视频"));
        TabViewChild tabViewChild03 = new TabViewChild(R.mipmap.redmusic, R.mipmap.whitemusic, "音乐", MusicFragment.newInstance("音乐"));
        TabViewChild tabViewChild04 = new TabViewChild(R.mipmap.redmirr, R.mipmap.whitemirr, "镜像", MirrorFragment.newInstance("镜像"));
        tabViewChildList.add(tabViewChild01);
        tabViewChildList.add(tabViewChild02);
        tabViewChildList.add(tabViewChild03);
        tabViewChildList.add(tabViewChild04);

        tabView.setTabViewChild(tabViewChildList, getSupportFragmentManager());
    }

    private void initData() {
        hpplayLinkControl = HpplayLinkControl.getInstance();
        hpplayLinkControl.setDebug(false);
        hpplayLinkControl.initHpplayLink(this, KEY);
        hpplayLinkControl.setTransportCallBack(this);
        fragmentManager = this.getFragmentManager();
        transaction = fragmentManager.beginTransaction();
        waitDialogUtil = new WaitDialogUtil(this);
        waitDialogUtil.showDialog("正在查找设备");
        //已推送并在电视端播放的多媒体是否随接入SDK的退出去退出电视播放
        hpplayLinkControl.setIsBackgroundPlay(new ExecuteResultCallBack() {
            @Override
            public void onResultDate(Object o, int i) {

            }
        }, EXIT_STOP_PLAY, true);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case FIND_DEVICE:
                    if (castDeviceInfoList.size() == 0) {
                        Toast.makeText(MainActivity.this, "找不到设备", Toast.LENGTH_SHORT).show();
                        connetc_status.setText("未连接");
                    } else {
                        Toast.makeText(MainActivity.this, "设备连接成功", Toast.LENGTH_SHORT).show();
                        connetc_status.setText("已连接");
                    }
                    waitDialogUtil.dismiss();
                    break;
                case CONNECT_DEVICE:
                    String phoneNum = android.os.Build.MODEL;
                    if (castDeviceInfoList.size() == 0) {
                        promptDialog.showAlertSheet("", true, promptButton, new PromptButton("找不到设备", MainActivity.this), new PromptButton(phoneNum, MainActivity.this));
                    } else {
                        promptDialog.showAlertSheet("", true, promptButton, new PromptButton(phoneNum, MainActivity.this), new PromptButton(castDeviceInfoList.get(0).getHpplayLinkName(), MainActivity.this));
                    }

            }
        }
    };

    public void getDataAndBuildServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DataAndServer();
            }
        }).start();
    }

    public void DataAndServer() {
        ArrayList<DataInfo> musicList = DataScanUtil.getMusicList(MainActivity.this);
        dataInfos.addAll(musicList);
        ArrayList<DataInfo> videoList = DataScanUtil.getVideoList(MainActivity.this);
        dataInfos.addAll(videoList);
        dataInfoStringHashMap = ServerUtil.buildServer(this.getApplicationContext(), dataInfos);
        ArrayList<DataInfo> allPhotoInfo = DataScanUtil.getAllPhotoInfo(MainActivity.this);
        MyApplication.instance.setAllPhotoInfo(allPhotoInfo);
        MyApplication.instance.setMusicList(musicList);
        MyApplication.instance.setVideoList(videoList);
        MyApplication.instance.setDataInfoStringHashMap(dataInfoStringHashMap);
    }

    @Override
    public void onResultDate(Object o, int i) {

    }

    @Override
    public void onTransportData(Object o) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //hpplayLinkControl.castDisconnectDevice();
    }


    public void connect() {
        hpplayLinkControl.castServiceDiscovery(this, new CastDeviceServiceCallback() {
            @Override
            public void onNoneCastDeviceService() {
                waitDialogUtil.dismiss();
                //Toast.makeText(MainActivity.this, "找不到设备", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "connect not device==");
            }

            @Override
            public void onCastDeviceServiceAvailable(List<CastDeviceInfo> list) {
                if (!isFindConnect) {
                    handler.sendEmptyMessageDelayed(FIND_DEVICE, 2000);
                    waitDialogUtil.setDialogTitle("正在连接设备");
                }
                // castDeviceInfoList.clear();
                castDeviceInfoList = list;
                //castDeviceInfoList.addAll(list);
                Log.i(TAG, "device  num==" + list.size());
                MyApplication.isConnect = true;
                if (list.size() > 0) {
                    CastDeviceInfo castDeviceInfo = list.get(0);
                    hpplayLinkControl.castConnectDevice(castDeviceInfo, new ConnectStateCallback() {
                        @Override
                        public void onConnectError() {
                            Log.i(TAG, "onConnectError==");
                        }

                        @Override
                        public void onConnected() {
                            Log.i(TAG, "onConnected==");
                        }

                        @Override
                        public void onConnectionBusy() {
                            Log.i(TAG, "onConnectionBusy==");
                        }

                        @Override
                        public void onDisConnect() {
                            Log.i(TAG, "onDisConnect==");
                        }
                    });
                }
            }
        });
    }


    @OnClick({R.id.back, R.id.connect})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.connect:
                connectDevice();
                break;
        }
    }

    private void connectDevice() {
        promptDialog = new PromptDialog(this);
        promptButton = new PromptButton("收起", this);
        String phoneNum = android.os.Build.MODEL;
        promptDialog.showAlertSheet("", true, promptButton, new PromptButton("正在查找...", this), new PromptButton(phoneNum, this));
        connect();
        handler.sendEmptyMessageDelayed(CONNECT_DEVICE, 3000);
        isFindConnect = true;
        // castDeviceInfoList.clear();
    }

    @Override
    public void onClick(PromptButton promptButton) {
        Log.i(TAG, "text==" + promptButton.getText());
    }


    private long exitTime;

    @Override
    public void onBackPressed() {
        if (promptDialog!=null){
            promptDialog.dismiss();
        }
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(MainActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
            return;
        }
        super.onBackPressed();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        MainActivity.hpplayLinkControl.castStartMirrorResult(requestCode,
                resultCode, data);
    }
}
