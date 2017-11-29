package com.reeman.projector.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
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

public class MainActivity extends AppCompatActivity implements ExecuteResultCallBack, TransportCallBack,PromptButtonListener {


    //    @BindView(R.id.picture)
//    Button picture;
//    @BindView(R.id.connect)
//    Button connect;
//    @BindView(R.id.video)
//    Button video;
//    @BindView(R.id.music)
//    Button music;
//    @BindView(R.id.mirror)
//    Button mirror;
    @BindView(R.id.tabView)
    TabView tabView;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.connect)
    TextView connect;
    private final static String TAG = "MainActivity==";
    private final static String KEY = "4b51c13b09a6d58445ebeb57dad95471";
    private final static int FIND_DEVICE = 0x11;
    private final static int CONNECT_DEVICE = 0X12;
    public static HpplayLinkControl hpplayLinkControl;
    ArrayList<DataInfo> dataInfos = new ArrayList<>();
    HashMap<DataInfo, String> dataInfoStringHashMap;

    PictureFragment pictureFragment;
    MusicFragment musicFragment;
    VideoFragment videoFragment;
    MirrorFragment mirrorFragment;
    FragmentManager fragmentManager;
    FragmentTransaction transaction;

    private WaitDialogUtil waitDialogUtil;
    PromptDialog promptDialog;
    PromptButton promptButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //  EventBus.getDefault().register(this);
        ButterKnife.bind(this);
        initView();
        initData();
        connect();
        getDataAndBuildServer();
        //getDataAndBuildServer();
    }

    private void initView() {

        List<TabViewChild> tabViewChildList = new ArrayList<>();
        TabViewChild tabViewChild01 = new TabViewChild(R.mipmap.redpic, R.mipmap.whitepic, "相册", PictureFragment.newInstance("相册"));
        TabViewChild tabViewChild02 = new TabViewChild(R.mipmap.redvideo, R.mipmap.whitevideo, "视频", VideoFragment.newInstance("视频"));
        TabViewChild tabViewChild03 = new TabViewChild(R.mipmap.redmusic, R.mipmap.whitemusic, "音乐", MusicFragment.newInstance("音乐"));
        TabViewChild tabViewChild04 = new TabViewChild(R.mipmap.redmirr, R.mipmap.whitemirr, "镜像", MirrorFragment.newInstance("镜像"));
        //TabViewChild tabViewChild05=new TabViewChild(R.drawable.tab05_sel,R.drawable.tab05_unsel,"我的",  FragmentCommon.newInstance("我的"));
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
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case FIND_DEVICE:
                    waitDialogUtil.dismiss();
                    break;
                case 0:
                    promptDialog.showAlertSheet("",true,promptButton,new PromptButton("note 3",MainActivity.this),new PromptButton("note 3",MainActivity.this));

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
//
//    @OnClick({R.id.connect, R.id.picture, R.id.music, R.id.video, R.id.mirror})
//    public void onViewClicked(View view) {
//        int id = view.getId();
//        switch (id) {
//            case R.id.connect:
//                connect();
//                break;
//            case R.id.picture:
//                fragmentDeal(R.id.picture);
//                break;
//            case R.id.music:
//                fragmentDeal(R.id.music);
//                break;
//            case R.id.video:
//                fragmentDeal(R.id.video);
//                break;
//            case R.id.mirror:
//                fragmentDeal(R.id.mirror);
//                break;
//        }
//    }

//    public void fragmentDeal(int id) {
//        hide();
//        switch (id) {
//            case R.id.picture:
//                if (pictureFragment == null) {
//                    pictureFragment = new PictureFragment();
//                    transaction.add(R.id.data_show, pictureFragment);
//                } else {
//                    transaction.add(R.id.data_show, pictureFragment);
//                }
//                break;
//            case R.id.music:
//                if (musicFragment == null) {
//                    musicFragment = new MusicFragment();
//                    transaction.add(R.id.data_show, musicFragment);
//                } else {
//                    transaction.add(R.id.data_show, musicFragment);
//                }
//                break;
//            case R.id.video:
//                if (videoFragment == null) {
//                    videoFragment = new VideoFragment();
//                    transaction.add(R.id.data_show, videoFragment);
//                } else {
//                    transaction.add(R.id.data_show, videoFragment);
//                }
//                break;
//            case R.id.mirror:
//                if (mirrorFragment==null){
//                    mirrorFragment = new MirrorFragment();
//                    transaction.add(R.id.data_show,mirrorFragment);
//                }else {
//                    transaction.add(R.id.data_show,mirrorFragment);
//                }
//                break;
//
//        }
//        transaction.commit();
//    }

//    public void hide() {
//        if (pictureFragment != null) {
//            transaction.hide(pictureFragment);
//        }
//        if (musicFragment != null) {
//            transaction.hide(musicFragment);
//        }
//        if (videoFragment!=null){
//            transaction.hide(videoFragment);
//        }
//        if (mirrorFragment!=null){
//            transaction.hide(mirrorFragment);
//        }
//    }

    @Override
    public void onResultDate(Object o, int i) {

    }

    @Override
    public void onTransportData(Object o) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hpplayLinkControl.castDisconnectDevice();
        //EventBus.getDefault().unregister(this);
    }


    public void connect() {
        hpplayLinkControl.castServiceDiscovery(this, new CastDeviceServiceCallback() {
            @Override
            public void onNoneCastDeviceService() {
                waitDialogUtil.dismiss();
                Toast.makeText(MainActivity.this, "找不到设备", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "connect not device==");
            }

            @Override
            public void onCastDeviceServiceAvailable(List<CastDeviceInfo> list) {
                handler.sendEmptyMessageDelayed(FIND_DEVICE, 2000);
                waitDialogUtil.setDialogTitle("正在连接设备");
                Log.i(TAG, "device  num==" + list.size());

                if (list.size() > 0) {
                    CastDeviceInfo castDeviceInfo = list.get(0);
                    hpplayLinkControl.castConnectDevice(castDeviceInfo, new ConnectStateCallback() {
                        @Override
                        public void onConnectError() {
                            //      Toast.makeText(MainActivity.this, "连接错误", Toast.LENGTH_SHORT).show();
                            Log.i(TAG, "onConnectError==");
                        }

                        @Override
                        public void onConnected() {
                            //       Toast.makeText(MainActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
                            Log.i(TAG, "onConnected==");
                        }

                        @Override
                        public void onConnectionBusy() {
                            //      Toast.makeText(MainActivity.this, "连接繁忙", Toast.LENGTH_SHORT).show();
                            Log.i(TAG, "onConnectionBusy==");
                        }

                        @Override
                        public void onDisConnect() {
                            //        Toast.makeText(MainActivity.this, "断开连接", Toast.LENGTH_SHORT).show();
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
                conectDevice();
                break;
        }
    }

    private void conectDevice() {
        promptDialog = new PromptDialog(this);
        promptButton = new PromptButton("收起",this);
        promptDialog.showAlertSheet("",true,promptButton,new PromptButton("正在查找...",this),new PromptButton("正在查找...",this));
        handler.sendEmptyMessageDelayed(0,2000);
    }

    @Override
    public void onClick(PromptButton promptButton) {
           Log.i(TAG,"text=="+promptButton.getText());
    }

    @Override
    public void onBackPressed() {
        if (promptDialog.onBackPressed())
            super.onBackPressed();
    }
}
