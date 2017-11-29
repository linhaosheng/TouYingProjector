package com.reeman.projector.utils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.reeman.projector.bean.DataInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by haoshenglin on 2017/11/28.
 */

public class ServerUtil {

    private final static String TAG = "ServerUtil==";

    public static HashMap<DataInfo, String> buildServer(Context context, ArrayList<DataInfo> dataInfos) {

        HashMap<DataInfo, String> dataInfoStringHashMap = new HashMap();
        AsyncHttpServer httpServer = new AsyncHttpServer();
        String localUrl = getLocalIp4Address(context);
        httpServer.listen(10000, localUrl);
        // String path = "/storage/emulated/0/netease/cloudmusic/Music/薛之谦 - 我终于成了别人的女人.mp3";
        // httpServer.directory("/test.mp3",new File(path));

        for (int i = 0; i < dataInfos.size() - 1; i++) {
            DataInfo musicInfo = dataInfos.get(i);
            String path = musicInfo.getUrl();
            Log.i(TAG, "path==" + path);
            StringBuilder stringBuilder = new StringBuilder("/musicAndVideo");
            stringBuilder.append(i).append(path.substring(path.lastIndexOf(".")));
            if (path.contains(".mp4")){
                httpServer.directoryWithPath(stringBuilder.toString(),new File(path));
            }else {
                httpServer.directory(stringBuilder.toString(), new File(path));
            }
            Log.i(TAG, "rege==" + stringBuilder.toString());
            StringBuilder sb = new StringBuilder("http://");
            sb.append(localUrl).append(":10000").append(stringBuilder.toString());
            Log.i(TAG, "localUrl==" + sb.toString());
            dataInfoStringHashMap.put(musicInfo, sb.toString());
        }
        return dataInfoStringHashMap;
    }


    public static String getLocalIp4Address(Context context) {

        //获取wifi服务
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        String ip = intToIp(ipAddress);
        Log.i("address", "===" + ip);
        return ip;
    }

    private static String intToIp(int i) {

        return (i & 0xFF) + "." +
                ((i >> 8) & 0xFF) + "." +
                ((i >> 16) & 0xFF) + "." +
                (i >> 24 & 0xFF);
    }
}
