package com.example.nerdlauncher;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nerdlauncher.activity.ActivityContent;

import java.util.List;

public class NerdLauncherFragment extends Fragment {
    public static final String TAG = "NerdLauncherFragment";
    public static final String EXRA_LIST_KEY = "com.example.nerdlauncher.list_key";

    private String mListKey;
    private OnListFragmentInteractionListener mListener;

    public static Fragment setInstrance(String listKey) {
        Bundle args = new Bundle();
        args.putString(EXRA_LIST_KEY, listKey);
        NerdLauncherFragment fragment = new NerdLauncherFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mListKey = (String) getArguments().getSerializable(EXRA_LIST_KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nerdlauncher_list, container, false);

        if (view instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
            recyclerView.setAdapter(new MyNerdLauncherRecyclerViewAdapter(ActivityContent.getItems(mListKey), mListener));
        }

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(ResolveInfo item);
    }

    public class MyNerdLauncherRecyclerViewAdapter extends RecyclerView.Adapter<MyNerdLauncherRecyclerViewAdapter.ViewHolder> {

        private final TypedValue mTypedValue = new TypedValue();
        private final List mValues;
        private final OnListFragmentInteractionListener mListener;

        private int mBackground;

        public MyNerdLauncherRecyclerViewAdapter(List items, OnListFragmentInteractionListener listener) {
            getActivity().getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
            mValues = items;
            mListener = listener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_nerdlauncher_listitem, parent, false);
            view.setBackgroundResource(mBackground);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            if(mValues.get(position) instanceof ResolveInfo) {
                holder.mItemResolve = (ResolveInfo) mValues.get(position);
                PackageManager pm = getActivity().getPackageManager();
                CharSequence label = holder.mItemResolve.loadLabel(pm);
                holder.mActivityNameView.setText(label);
                Drawable icon = holder.mItemResolve.loadIcon(pm);
                holder.mActivityIconVIew.setImageDrawable(icon);

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null) {
                            mListener.onListFragmentInteraction(holder.mItemResolve);
                        }

                        Context context = v.getContext();
                        ActivityInfo activityInfo = holder.mItemResolve.activityInfo;
                        if (activityInfo == null) return;
                        Intent i = new Intent(Intent.ACTION_MAIN);
                        i.setClassName(activityInfo.packageName, activityInfo.name);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(i);
                    }
                });
            } else if(mValues.get(position) instanceof ActivityManager.RunningTaskInfo) {

                holder.mItemTaskInfo = (ActivityManager.RunningTaskInfo) mValues.get(position);
                PackageManager pm = getActivity().getPackageManager();
                ApplicationInfo applicationInfo = null;
                try {
                    applicationInfo = pm.getApplicationInfo(holder.mItemTaskInfo.baseActivity.getPackageName(), 0);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                    Log.d(TAG, "Didn't find App "+holder.mItemTaskInfo.baseActivity.getPackageName());
                }
                if (applicationInfo != null) {
                    holder.mActivityNameView.setText(pm.getApplicationLabel(applicationInfo));
                    holder.mActivityIconVIew.setImageDrawable(pm.getApplicationIcon(applicationInfo));
                    holder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ActivityManager am = (ActivityManager) getActivity().getSystemService(Activity.ACTIVITY_SERVICE);
                            am.moveTaskToFront(holder.mItemTaskInfo.id, 0);
                        }
                    });
                }
            }
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mActivityNameView;
            public final ImageView mActivityIconVIew;
            public ResolveInfo mItemResolve;
            public ActivityManager.RunningTaskInfo mItemTaskInfo;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mActivityNameView = (TextView) view.findViewById(R.id.activity_name);
                mActivityIconVIew = (ImageView) view.findViewById(R.id.activity_icon);
            }
        }
    }
}
