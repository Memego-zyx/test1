package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class BottomFuncAdapter extends RecyclerView.Adapter<BottomFuncAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<BottomItem> list;

    //内部数据实体：文字 + 图标资源id
    public static class BottomItem {
        public String name;
        public int iconRes;
        public BottomItem(String name, int iconRes) {
            this.name = name;
            this.iconRes = iconRes;
        }
    }

    public BottomFuncAdapter(Context ctx, ArrayList<BottomItem> data){
        context=ctx;
        list=data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.item_bottom,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BottomItem item=list.get(position);
        holder.tvFunc.setText(item.name);
        holder.ivIcon.setImageResource(item.iconRes);

        // Mac Dock焦点缩放效果，增加动画让过渡更顺滑
        holder.itemView.setOnFocusChangeListener((v,hasFocus)->{
            if(hasFocus){
                v.animate().scaleX(1.08f).scaleY(1.08f).setDuration(180).start();
            }else {
                v.animate().scaleX(1f).scaleY(1f).setDuration(180).start();
            }
        });
        //MyApps跳转全部应用列表逻辑
        if("My Apps".equals(item.name)){
            holder.itemView.setOnClickListener(v->{
                Intent intent=new Intent(context, AppListActivity.class);
                context.startActivity(intent);
            });
        }
        //Settings跳转系统设置
        if("Settings".equals(item.name)){
            holder.itemView.setOnClickListener(v->{
                Intent intent=new Intent(android.provider.Settings.ACTION_SETTINGS);
                context.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvFunc;
        ImageView ivIcon;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFunc=itemView.findViewById(R.id.tv_func_name);
            ivIcon=itemView.findViewById(R.id.iv_func_icon);
        }
    }
}