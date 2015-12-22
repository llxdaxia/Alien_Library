package cn.alien95.set.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by llxal on 2015/12/19.
 */
public class BaseViewHolder<T> extends RecyclerView.ViewHolder {

    private final String TAG = "recyclerview-baseviewholder";

    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    public BaseViewHolder(Context context,int layoutId){
        super(LayoutInflater.from(context).inflate(layoutId,null));
        Log.i(TAG,"itemview-isclickable:" + itemView.isClickable());
    }

    /**
     * 设置数据
     * @param object
     */
    public void setData(T object){

    }
}
