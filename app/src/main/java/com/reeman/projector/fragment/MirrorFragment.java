package com.reeman.projector.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.hpplay.callback.MirrorStateCallback;
import com.reeman.projector.activity.MainActivity;
import com.reeman.projector.R;

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


    public static MirrorFragment newInstance(String text){
        MirrorFragment fragmentCommon=new MirrorFragment();
        Bundle bundle=new Bundle();
        bundle.putString("text",text);
        fragmentCommon.setArguments(bundle);
        return fragmentCommon;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.mirror, null);
        unbinder1 = ButterKnife.bind(this, view);
        return view;
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
                Toast.makeText(getActivity(), "镜像连接失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMirrorDisconnected() {
                Toast.makeText(getActivity(), "镜像断开成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMirrorConnected() {
                Toast.makeText(getActivity(), "镜像连接成功", Toast.LENGTH_SHORT).show();
            }
        });
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
