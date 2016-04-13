package com.example.nerdlauncher;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nerdlauncher.NerdLauncherFragment.OnListFragmentInteractionListener;
import java.util.List;

public class MyNerdLauncherRecyclerViewAdapter extends RecyclerView.Adapter<MyNerdLauncherRecyclerViewAdapter.ViewHolder> {

    private final List<ResolveInfo> mValues;
    private final OnListFragmentInteractionListener mListener;
    private final Activity mActivity;

    public MyNerdLauncherRecyclerViewAdapter(Activity activity, List<ResolveInfo> items, OnListFragmentInteractionListener listener) {
        mActivity = activity;
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_nerdlauncher, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        PackageManager pm = mActivity.getPackageManager();
        holder.mActivityNameView.setText(mValues.get(position).loadLabel(pm));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mActivityNameView;
        public ResolveInfo mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mActivityNameView = (TextView) view.findViewById(R.id.activity_name);
        }
    }
}
