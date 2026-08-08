package com.example.myapplication;

import android.graphics.drawable.Drawable;

public class AppInfo {
    public String appName;
    public Drawable iconDrawable;
    public String packageName;

    public AppInfo(String appName, Drawable drawable, String packageName) {
        this.appName = appName;
        this.iconDrawable = drawable;
        this.packageName = packageName;
    }
}