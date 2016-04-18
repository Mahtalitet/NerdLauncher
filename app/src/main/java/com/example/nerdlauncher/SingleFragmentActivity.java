package com.example.nerdlauncher;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import com.example.nerdlauncher.activity.ActivityContent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public abstract class SingleFragmentActivity extends AppCompatActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_nerdlauncher);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle(R.string.app_name);
		setSupportActionBar(toolbar);

		ViewPager pager = (ViewPager) findViewById(R.id.view_pager);
		if (pager != null) {
			setupViewPager(pager);
		}

		TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
		tabLayout.setupWithViewPager(pager);

	}

	private void setupViewPager(ViewPager viewPager) {
		Adapter adapter = new Adapter(getSupportFragmentManager());
		adapter.addFragment(setAllTabs(), getString(R.string.tab_all_app));
		adapter.addFragment(setCurrentTabs(), getString(R.string.tab_current_app));
		viewPager.setAdapter(adapter);
	}

	static class Adapter extends FragmentPagerAdapter {
		private final List<Fragment> mFragments = new ArrayList<>();
		private final List<String> mFragmentTitles = new ArrayList<>();

		public Adapter(FragmentManager fm) {
			super(fm);
		}

		public void addFragment(Fragment fragment, String title) {
			mFragments.add(fragment);
			mFragmentTitles.add(title);
		}

		@Override
		public Fragment getItem(int position) {
			return mFragments.get(position);
		}

		@Override
		public int getCount() {
			return mFragments.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return mFragmentTitles.get(position);
		}
	}

	private Fragment setAllTabs() {
		Intent startupIntent = new Intent(Intent.ACTION_MAIN);
		startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);

		final PackageManager pm = getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(startupIntent, 0);

		Collections.sort(activities, new Comparator<ResolveInfo>() {
			@Override
			public int compare(ResolveInfo a, ResolveInfo b) {
				return String.CASE_INSENSITIVE_ORDER
						.compare(a.loadLabel(pm).toString(),
								b.loadLabel(pm).toString());
			}
		});

		return createFragmentWithList(getString(R.string.tab_all_app), activities);
	}

	private Fragment setCurrentTabs() {
		ActivityManager am = (ActivityManager) getSystemService(Activity.ACTIVITY_SERVICE);
		List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(Integer.MAX_VALUE);
		return createFragmentWithList(getString(R.string.tab_current_app), list);

	}

	private Fragment createFragmentWithList(String key, List value) {
		ActivityContent.addItems(key, value);
		return NerdLauncherFragment.setInstrance(key);
	}

	
}
