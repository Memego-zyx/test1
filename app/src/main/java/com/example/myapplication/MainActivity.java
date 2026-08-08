package com.example.myapplication;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvApp, rvBottomFunc;
    private TextView tvTime;

    //修复Handler高版本警告，指定主线程Looper
    private final Handler timeHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            updateTime();
            sendEmptyMessageDelayed(0, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // =====补全屏边距适配代码=====
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rvApp = findViewById(R.id.rv_app);
        rvBottomFunc = findViewById(R.id.rv_bottom_func);
        tvTime = findViewById(R.id.tv_time);


        //顶部应用：1行4列网格，一行摆放4个App图标，自动均分屏幕，静态整体居中，不可横向滚动
        GridLayoutManager layoutApp = new GridLayoutManager(this,4) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        };
        layoutApp.setSpanCount(4);
        rvApp.setLayoutManager(layoutApp);

        //底部5个功能按钮：1行5列，自动均分屏幕宽度，整体屏幕居中
        GridLayoutManager layoutBottom = new GridLayoutManager(this,5);
        rvBottomFunc.setLayoutManager(layoutBottom);

        //顶部应用集
        ArrayList<AppInfo> topApps = new ArrayList<>();
        List<ResolveInfo> allAppResolve = getAllInstallApp();

        //优先netflix、youtube、vending、chrome
        String[] expectPkg = {
                "com.netflix.mediaclient",
                "com.google.android.youtube",
                "com.android.vending",
                "com.android.chrome"
        };

        PackageManager pm = getPackageManager();

        //顶部应用
        for (String pkg : expectPkg) {
            for (ResolveInfo ri : allAppResolve) {
                String pkgName = ri.activityInfo.packageName;
                if (pkgName.equals(pkg)) {
                    String appName = ri.loadLabel(pm).toString();
                    Drawable drawable = ri.loadIcon(pm);
                    topApps.add(new AppInfo(appName, drawable, pkgName));
                    break;
                }
            }
        }

        //【新增兜底逻辑】模拟器没有上面指定App的时候，取本机前3个桌面应用，防止顶部列表空白
        if (topApps.isEmpty()) {
            int takeCount = 0;
            for (ResolveInfo ri : allAppResolve) {
                if (takeCount >= 4) break;
                String appName = ri.loadLabel(pm).toString();
                Drawable drawable = ri.loadIcon(pm);
                topApps.add(new AppInfo(appName, drawable, ri.activityInfo.packageName));
                takeCount++;
            }
        }

        AppAdapter appAdapter = new AppAdapter(this, topApps);
//设置应用item点击监听
        appAdapter.setOnAppItemClickListener(info -> {
            openAppByPackageName(info.packageName);
        });
        rvApp.setAdapter(appAdapter);

        //底部功能按钮 —— 使用BottomItem实体类，替换原有字符串ArrayList
        ArrayList<BottomFuncAdapter.BottomItem> bottomData = new ArrayList<>();
        bottomData.add(new BottomFuncAdapter.BottomItem("Keystone", android.R.drawable.ic_menu_call));
        bottomData.add(new BottomFuncAdapter.BottomItem("Miracast", android.R.drawable.ic_menu_gallery));
        bottomData.add(new BottomFuncAdapter.BottomItem("Signal Source", android.R.drawable.ic_menu_search));
        bottomData.add(new BottomFuncAdapter.BottomItem("My Apps", android.R.drawable.ic_menu_view));
        bottomData.add(new BottomFuncAdapter.BottomItem("Settings", android.R.drawable.ic_menu_preferences));
        BottomFuncAdapter bottomAdapter = new BottomFuncAdapter(this, bottomData);
        rvBottomFunc.setAdapter(bottomAdapter);

        timeHandler.sendEmptyMessage(0);
    }

    private void updateTime() {
        if(tvTime == null) return;
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa E,MMMM dd", Locale.ENGLISH);
        tvTime.setText(sdf.format(new Date()));
    }

    //读取系统全部可启动APP
    public List<ResolveInfo> getAllInstallApp() {
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        return getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_ALL);
    }

    //生成倒影Bitmap
    public Bitmap createReflection(Bitmap original) {
        if(original == null || original.isRecycled()) return null;
        int w = original.getWidth();
        int h = original.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);
        Bitmap mirror = Bitmap.createBitmap(original, 0, 0, w, h, matrix, false);
        Bitmap out = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(out);
        canvas.drawBitmap(mirror, 0, 0, null);
        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0, 0, 0, h, 0x80FFFFFF, 0x00FFFFFF, Shader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawRect(0, 0, w, h, paint);
        return out;
    }


    private void openAppByPackageName(String pkg){
        try {
            PackageManager pm = getPackageManager();
            Intent launchIntent = pm.getLaunchIntentForPackage(pkg);
            if(launchIntent != null){
                startActivity(launchIntent);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timeHandler.removeCallbacksAndMessages(null);

    }
}