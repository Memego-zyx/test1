package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.AppInfo;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;

import java.util.ArrayList;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<AppInfo> list;

    // 点击回调接口，通知Activity打开对应App
    public interface OnAppItemClickListener {
        void onItemClick(AppInfo info);
    }
    private OnAppItemClickListener mOnAppItemClickListener;

    public void setOnAppItemClickListener(OnAppItemClickListener listener) {
        mOnAppItemClickListener = listener;
    }

    public AppAdapter(Context ctx,ArrayList<AppInfo> data){
        context=ctx;
        list=data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.app_item,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppInfo info=list.get(position);
        holder.tvName.setText(info.appName);
        holder.ivIcon.setImageDrawable(info.iconDrawable);

        //生成倒影
        holder.ivIcon.setDrawingCacheEnabled(true);
        holder.ivIcon.measure(View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED));
        holder.ivIcon.layout(0,0,holder.ivIcon.getMeasuredWidth(),holder.ivIcon.getMeasuredHeight());
        holder.ivIcon.buildDrawingCache(true);
        Bitmap src=Bitmap.createBitmap(holder.ivIcon.getDrawingCache());
        Bitmap refBitmap=((MainActivity)context).createReflection(src);
        holder.ivReflect.setImageDrawable(new BitmapDrawable(context.getResources(),refBitmap));
        holder.ivIcon.setDrawingCacheEnabled(false);

        //TV焦点放大，改为属性动画实现平滑过渡
        holder.itemView.setOnFocusChangeListener((v,hasFocus)->{
            if(hasFocus){
                v.animate().scaleX(1.05f).scaleY(1.05f).setDuration(180).start();
            }else{
                v.animate().scaleX(1f).scaleY(1f).setDuration(180).start();
            }
        });

        // item点击事件回调
        holder.itemView.setOnClickListener(v -> {
            if(mOnAppItemClickListener != null){
                mOnAppItemClickListener.onItemClick(info);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView ivIcon,ivReflect;
        TextView tvName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon=itemView.findViewById(R.id.iv_icon);
            ivReflect=itemView.findViewById(R.id.iv_reflect);
            tvName=itemView.findViewById(R.id.tv_name);
        }
    }
}