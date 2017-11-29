package com.reeman.projector.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.hpplay.callback.MirrorStateCallback;
import com.reeman.projector.MyApplication;
import com.reeman.projector.activity.MainActivity;
import com.reeman.projector.R;
import com.reeman.projector.view.WaitDialogUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by haoshenglin on 2017/11/28.
 */

public class MirrorFragment extends Fragment {


    BindView unbinder;
    @BindView(R.id.start_mirror)
    Button startMirror;
    @BindView(R.id.stop_mirror)
    Button stopMirror;
    Unbinder unbinder1;
    WaitDialogUtil waitDialogUtil;
    private final static int CONNECT_SUCCESS = 0X11;
    private final static int CONNECT_FAIL = 0X12;
    private final static int DISCONNECT_SUCCESS = 0X13;



    public static MirrorFragment newInstance(String text){
        MirrorFragment fragmentCommon=new MirrorFragment();
        Bundle bundle=new Bundle();
        bundle.putString("text",text);
        fragmentCommon.setArguments(bundle);
        return fragmentCommon;
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            waitDialogUtil.dismiss();
            switch (what){
                case CONNECT_SUCCESS:
                    Toast.makeText(getActivity(),"连接成功",Toast.LENGTH_SHORT).show();
                    break;
                case CONNECT_FAIL:
                    Toast.makeText(getActivity(),"连接失败",Toast.LENGTH_SHORT).show();
                    break;
                case DISCONNECT_SUCCESS:
                    Toast.makeText(getActivity(),"断开成功",Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.mirror, null);
        init();
        unbinder1 = ButterKnife.bind(this, view);
        return view;
    }

    private void init() {
        waitDialogUtil = new WaitDialogUtil(getActivity());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder1.unbind();
    }

    @OnClick({R.id.start_mirror, R.id.stop_mirror})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.start_mirror:
                openMirror();
                break;
            case R.id.stop_mirror:
                stopMirror();
                break;
        }
    }


    private void openMirror() {
        if (!MyApplication.isConnect){
            Toast.makeText(getActivity(),"请先连接设备",Toast.LENGTH_SHORT).show();
            return;
        }
        waitDialogUtil.showDialog("正在连接...");
        boolean mirrorState = MainActivity.hpplayLinkControl.getMirrorState();
        if (mirrorState) {
            Toast.makeText(getActivity(), "镜像已连接", Toast.LENGTH_SHORT).show();
            return;
        }
        MainActivity.hpplayLinkControl.castStartMirror(getActivity(), new MirrorStateCallback() {
            @Override
            public void onMirrorStarting() {

            }

            @Override
            public void onMirrorFailed() {
                handler.sendEmptyMessage(CONNECT_FAIL);
            }

            @Override
            public void onMirrorDisconnected() {
                handler.sendEmptyMessage(DISCONNECT_SUCCESS);
            }

            @Override
            public void onMirrorConnected() {
                handler.sendEmptyMessage(CONNECT_SUCCESS);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        MainActivity.hpplayLinkControl.castStartMirrorResult(requestCode,
        resultCode,data) ;
    }

    private void stopMirror() {
        boolean mirrorState = MainActivity.hpplayLinkControl.getMirrorState();
        if (!mirrorState) {
            Toast.makeText(getActivity(), "镜像已断开", Toast.LENGTH_SHORT).show();
            return;
        }
        MainActivity.hpplayLinkControl.castStopMirror();
    }

}
