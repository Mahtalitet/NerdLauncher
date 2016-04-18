package com.example.nerdlauncher.activity;

import android.content.pm.ResolveInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityContent {

    public static HashMap<String, List> mActivityItemsTab = new HashMap<>();

    public static void addItems(String key, List value) {
        mActivityItemsTab.put(key, value);
    }

    public static List<ResolveInfo> getItems(String key) {
        return mActivityItemsTab.get(key);
    }
}
