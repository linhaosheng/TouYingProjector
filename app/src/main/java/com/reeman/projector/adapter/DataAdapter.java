package com.reeman.projector.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.reeman.projector.R;
import com.reeman.projector.bean.DataInfo;
import com.reeman.projector.utils.ImageUtil;

/**
 * Created by haoshenglin on 2017/11/28.
 */

public class DataAdapter  extends BaseQuickAdapter<DataInfo,BaseViewHolder>{

    private Context context;
    public DataAdapter(){
        super(R.layout.data_item);
    }
    public void setContext(Context context){
        this.context = context;
    }
    @Override
    protected void convert(BaseViewHolder helper, DataInfo item) {
        ImageView imageView = helper.getView(R.id.pic);
        if (item.getUrl().contains(".mp3")){
          //  ImageUtil.loadImg(context,item.getAlbum(),imageView);
        }else {
            ImageUtil.loadImg(context,item.getUrl(),imageView);
        }
        TextView view = helper.getView(R.id.name);
        view.setText(item.getDisplayName());
    }
}
