package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AllAppAdapter extends RecyclerView.Adapter<AllAppAdapter.ViewHolder> {
    private final Context context;
    private final List<AppInfoAll> dataList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener{
        void onItemClick(AppInfoAll info);
    }

    public AllAppAdapter(Context ctx, List<AppInfoAll> list, OnItemClickListener clickListener){
        context = ctx;
        dataList = list;
        listener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_allapp, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppInfoAll info = dataList.get(position);
        holder.tvLabel.setText(info.label);
        holder.ivIcon.setImageDrawable(info.icon);

        //TV焦点缩放
        holder.itemView.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus){
                v.setScaleX(1.10f);
                v.setScaleY(1.10f);
            }else{
                v.setScaleX(1f);
                v.setScaleY(1f);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if(listener != null){
                listener.onItemClick(info);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView ivIcon;
        TextView tvLabel;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_app_icon);
            tvLabel = itemView.findViewById(R.id.tv_app_label);
        }
    }
}