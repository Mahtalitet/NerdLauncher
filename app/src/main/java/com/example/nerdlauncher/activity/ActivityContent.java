package com.example.nerdlauncher.activity;

import android.content.pm.ResolveInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityContent {

    public static List<ResolveInfo> mActivityItems;

    public static void additems(List<ResolveInfo> items) {
        mActivityItems = items;
    }

    public static List<ResolveInfo> getItems() {
        if (mActivityItems == null) {
            mActivityItems = new ArrayList<ResolveInfo>();
        }

        return mActivityItems;
    }
}
