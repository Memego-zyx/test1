package com.example.myapplication;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.AppInfoAll;

import java.util.ArrayList;
import java.util.List;

public class AppListActivity extends AppCompatActivity {

    private RecyclerView rvAllApp;
    private List<AppInfoAll> appInfoAllList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applist);
        rvAllApp = findViewById(R.id.rv_all_app);

        //TV网格布局，4列
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,4);
        rvAllApp.setLayoutManager(gridLayoutManager);

        loadAllApps();

        AllAppAdapter adapter = new AllAppAdapter(this, appInfoAllList, info -> {
            //点击启动对应App
            Intent launchIntent = new Intent();
            launchIntent.setClassName(info.pkgName, info.activityName);
            startActivity(launchIntent);
        });
        rvAllApp.setAdapter(adapter);
    }

    private void loadAllApps(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        PackageManager pm = getPackageManager();
        List<ResolveInfo> resolveInfoList = pm.queryIntentActivities(intent,PackageManager.MATCH_ALL);
        appInfoAllList.clear();

        for(ResolveInfo ri : resolveInfoList){
            String label = ri.loadLabel(pm).toString();
            Drawable icon = ri.loadIcon(pm);
            String pkg = ri.activityInfo.packageName;
            String act = ri.activityInfo.name;
            appInfoAllList.add(new AppInfoAll(label, icon, pkg, act));
        }
    }
}