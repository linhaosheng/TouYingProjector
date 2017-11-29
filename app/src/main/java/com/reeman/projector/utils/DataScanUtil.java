package com.reeman.projector.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.reeman.projector.bean.DataInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by haoshenglin on 2017/11/28.
 */

public class DataScanUtil {

    //获取本机的音乐列表
    public static ArrayList<DataInfo> getMusicList(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor mCursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,   //路径
                new String[]{MediaStore.Audio.Media._ID,    //写入我想要获得的信息（列）
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.SIZE,
                        MediaStore.Audio.Media.DATA}, null, null, null);
        ArrayList<DataInfo> dataInfos = new ArrayList<>();

        try {
            for (int i = 0; i < mCursor.getCount(); ++i) {
                mCursor.moveToNext();   //读取下一行，moveToNext()有boolean返回值，执行成功返回ture,反之false，可用于判断是否读取完毕。

                long id = mCursor.getLong(0);
                String title = mCursor.getString(1);
                String album = mCursor.getString(2);
                String displayName = mCursor.getString(3);
                String artist = mCursor.getString(4);
                long duration = mCursor.getLong(5);
                long size = mCursor.getLong(6);
                String url = mCursor.getString(7);   //转存数据

                DataInfo musicInfo = new DataInfo();
                musicInfo.setAlbum(album);
                musicInfo.setId(id);
                musicInfo.setTitle(title);
                musicInfo.setDisplayName(displayName);
                musicInfo.setArtist(artist);
                musicInfo.setDuration(duration);
                musicInfo.setSize(size);
                musicInfo.setUrl(url);
                dataInfos.add(musicInfo);
                Log.i("path====", "===" + url);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mCursor.close();
        }
        return dataInfos;
    }

    //获取本机的视频列表
    public static ArrayList<DataInfo> getVideoList(Context context) {
        Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Video.Media.DEFAULT_SORT_ORDER);
        ArrayList<DataInfo> dataInfos = new ArrayList<>();
        try {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)); // id
                String displayName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
                String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.ALBUM)); // 专辑
                String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.ARTIST)); // 艺术家
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)); // 显示名称
                String mimeType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));

                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)); // 路径
                Log.i("path==", "==" + path);
                long duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)); // 时长
                long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)); // 大小
                String resolution = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.RESOLUTION));

                DataInfo musicInfo = new DataInfo();
                musicInfo.setId(id);
                musicInfo.setDisplayName(displayName);
                musicInfo.setAlbum(album);
                musicInfo.setArtist(artist);
                musicInfo.setTitle(title);
                musicInfo.setUrl(path);
                musicInfo.setDuration(duration);
                musicInfo.setSize(size);
                dataInfos.add(musicInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return dataInfos;
    }


    /**
     * 读取手机中所有图片信息
     */
    public static ArrayList<DataInfo> getAllPhotoInfo(Context context) {
        ArrayList<DataInfo> pictureList = new ArrayList<>();
        //HashMap<String,List<DataInfo>> allPhotosTemp = new HashMap<>();//所有照片
        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projImage = {MediaStore.Images.Media._ID
                , MediaStore.Images.Media.DATA
                , MediaStore.Images.Media.SIZE
                , MediaStore.Images.Media.DISPLAY_NAME};
        Cursor mCursor = context.getContentResolver().query(mImageUri,
                projImage,
                MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
                new String[]{"image/jpeg", "image/png"},
                MediaStore.Images.Media.DATE_MODIFIED + " desc");

        try {
            if (mCursor != null) {
                while (mCursor.moveToNext()) {
                    // 获取图片的路径
                    String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    long size = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Images.Media.SIZE)) / 1024;
                    String displayName = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));

                    DataInfo dataInfo = new DataInfo();
                    dataInfo.setUrl(path);
                    dataInfo.setDisplayName(displayName);
                    dataInfo.setSize(size);
                    pictureList.add(dataInfo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mCursor.close();
        }
        return pictureList;
    }

}
