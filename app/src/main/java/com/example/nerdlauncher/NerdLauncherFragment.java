package com.example.nerdlauncher;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
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

import com.bumptech.glide.Glide;
import com.example.nerdlauncher.activity.ActivityContent;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NerdLauncherFragment extends Fragment {
    public static final String TAG = "NerdLauncherFragment";

    private OnListFragmentInteractionListener mListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent startupIntent = new Intent(Intent.ACTION_MAIN);
        startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PackageManager pm = getActivity().getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(startupIntent, 0);

        Log.i(TAG, "I've found "+ activities.size()+" activities.");

        Collections.sort(activities, new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo a, ResolveInfo b) {
                PackageManager pm = getActivity().getPackageManager();
                return String.CASE_INSENSITIVE_ORDER
                        .compare(a.loadLabel(pm).toString(),
                                b.loadLabel(pm).toString());
            }
        });

        ActivityContent.additems(activities);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nerdlauncher_list, container, false);

        if (view instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
            recyclerView.setAdapter(new MyNerdLauncherRecyclerViewAdapter(ActivityContent.getItems(), mListener));
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
        private final List<ResolveInfo> mValues;
        private final OnListFragmentInteractionListener mListener;

        private int mBackground;

        public MyNerdLauncherRecyclerViewAdapter(List<ResolveInfo> items, OnListFragmentInteractionListener listener) {
            getActivity().getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
            mValues = items;
            mListener = listener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_nerdlauncher, parent, false);
            view.setBackgroundResource(mBackground);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            PackageManager pm = getActivity().getPackageManager();
            CharSequence label = mValues.get(position).loadLabel(pm);
            holder.mActivityNameView.setText(label);
            Drawable icon = mValues.get(position).loadIcon(pm);
            holder.mActivityIconVIew.setImageDrawable(icon);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onListFragmentInteraction(holder.mItem);
                    }

                    Context context = v.getContext();
                    ActivityInfo activityInfo = holder.mItem.activityInfo;
                    if (activityInfo == null) return;
                    Intent i = new Intent(Intent.ACTION_MAIN);
                    i.setClassName(activityInfo.packageName, activityInfo.name);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
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
            public final ImageView mActivityIconVIew;
            public ResolveInfo mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mActivityNameView = (TextView) view.findViewById(R.id.activity_name);
                mActivityIconVIew = (ImageView) view.findViewById(R.id.activity_icon);
            }
        }
    }
}
