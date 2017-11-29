package com.reeman.projector.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hpplay.callback.ExecuteResultCallBack;
import com.reeman.projector.activity.MainActivity;
import com.reeman.projector.MyApplication;
import com.reeman.projector.R;
import com.reeman.projector.activity.ShowImgActivity;
import com.reeman.projector.adapter.DataAdapter;
import com.reeman.projector.bean.DataInfo;
import com.reeman.projector.view.MyItemDecoration;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by haoshenglin on 2017/11/28.
 */

public class PictureFragment extends Fragment {

    private final static String TAG = "PictureFragment====";

    @BindView(R.id.data_list)
    RecyclerView dataList;
    Unbinder unbinder;
    DataAdapter dataAdapter;

    public static PictureFragment newInstance(String text){
        PictureFragment fragmentCommon=new PictureFragment();
        Bundle bundle=new Bundle();
        bundle.putString("text",text);
        fragmentCommon.setArguments(bundle);
        return fragmentCommon;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(),R.layout.picture,null);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
      dataList.setLayoutManager(new LinearLayoutManager(getActivity()));
      dataList.addItemDecoration(new MyItemDecoration());
      dataAdapter = new DataAdapter();
      dataAdapter.setContext(getActivity());
      dataAdapter.setNewData(MyApplication.instance.getAllPhotoInfo());
      dataList.setAdapter(dataAdapter);
      dataAdapter.notifyDataSetChanged();

      dataAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
          @Override
          public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
              DataInfo dataInfo = MyApplication.instance.getAllPhotoInfo().get(position);
              Log.i(TAG,"url==="+dataInfo.getUrl());
              MainActivity.hpplayLinkControl.startPhotoPlay(new ExecuteResultCallBack() {
                  @Override
                  public void onResultDate(Object o, int i) {
                      Log.i(TAG,"o=="+o+"===i"+i);
                  }
              },10,dataInfo.getUrl());
              Intent intent  = new Intent(getActivity(), ShowImgActivity.class);
              intent.putExtra("img_url",dataInfo.getUrl());
              intent.putExtra("img_title",dataInfo.getDisplayName());
              startActivity(intent);
          }
      });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

//    public void setDataList(ArrayList<DataInfo> dataList){
//        this.dataInfos = dataList;
//        dataAdapter.setNewData(dataInfos);
//        dataAdapter.notifyDataSetChanged();
//    }
}
