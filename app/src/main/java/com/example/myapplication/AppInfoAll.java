package com.example.myapplication;

import android.graphics.drawable.Drawable;

public class AppInfoAll {
    public String label;
    public Drawable icon;
    public String pkgName;
    public String activityName;

    public AppInfoAll(String label, Drawable icon, String pkgName, String activityName){
        this.label = label;
        this.icon = icon;
        this.pkgName = pkgName;
        this.activityName = activityName;
    }
}