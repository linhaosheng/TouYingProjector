package com.reeman.projector;

import android.app.Application;

import com.reeman.projector.bean.DataInfo;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by haoshenglin on 2017/11/28.
 */

public class MyApplication extends Application {


    HashMap<DataInfo, String> dataInfoStringHashMap;
    ArrayList<DataInfo> allPhotoInfo;
    ArrayList<DataInfo> musicList;
    ArrayList<DataInfo> videoList;

    public static MyApplication instance;
    public static boolean isConnect;


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initData();
    }

    public void initData(){
        allPhotoInfo = new ArrayList<>();
        musicList = new ArrayList<>();
        videoList = new ArrayList<>();
        dataInfoStringHashMap = new HashMap<>();
    }

    public void setDataInfoStringHashMap(HashMap<DataInfo,String> dataInfoStringHashMapData){
        dataInfoStringHashMap.putAll(dataInfoStringHashMapData);
    }
    public void setAllPhotoInfo(ArrayList<DataInfo> photoInfo){
        allPhotoInfo.addAll(photoInfo);
    }

    public void setMusicList(ArrayList<DataInfo>musicListData){
        musicList.addAll(musicListData);
    }

    public void setVideoList(ArrayList<DataInfo> videoListData){
        videoList.addAll(videoListData);
    }

    public ArrayList<DataInfo> getAllPhotoInfo(){return allPhotoInfo;}

    public ArrayList<DataInfo> getMusicList(){return musicList;}

    public ArrayList<DataInfo> getVideoList(){return videoList;}

    public HashMap<DataInfo,String> getDataInfoStringHashMap(){return dataInfoStringHashMap;}
}
