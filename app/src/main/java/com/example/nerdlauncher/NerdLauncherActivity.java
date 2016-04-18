package com.example.nerdlauncher;

import android.content.pm.ResolveInfo;
import android.support.v4.app.Fragment;
import android.os.Bundle;

public class NerdLauncherActivity extends SingleFragmentActivity implements NerdLauncherFragment.OnListFragmentInteractionListener{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onListFragmentInteraction(ResolveInfo item) {

    }
}
