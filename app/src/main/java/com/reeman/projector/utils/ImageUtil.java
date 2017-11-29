package com.reeman.projector.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.reeman.projector.R;

/**
 * Created by haoshenglin on 2017/11/28.
 */

public class ImageUtil {

    public static void loadImg(Context context, String url, ImageView imageView){
        Glide.with(context).load(url).into(imageView);
    }
}
