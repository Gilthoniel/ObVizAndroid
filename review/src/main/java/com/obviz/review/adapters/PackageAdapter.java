package com.obviz.review.adapters;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.obviz.reviews.R;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Created by gaylor on 31.07.15.
 *
 */
public class PackageAdapter extends BaseAdapter {

    private Context mContext;
    private List<PackageInfo> mPackages;

    public PackageAdapter(Context context) {

        mContext = context;
        mPackages = context.getPackageManager().getInstalledPackages(0);
        Collections.sort(mPackages, new Comparator<PackageInfo>() {
            @Override
            public int compare(PackageInfo info, PackageInfo other) {

                CharSequence infoLabel = mContext.getPackageManager().getApplicationLabel(info.applicationInfo);
                CharSequence otherLabel = mContext.getPackageManager().getApplicationLabel(other.applicationInfo);

                return infoLabel.toString().compareTo(otherLabel.toString());
            }
        });

        Iterator<PackageInfo> it = mPackages.iterator();
        while (it.hasNext()) {
            PackageInfo info = it.next();

            // Remove the system application
            if ((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                it.remove();
            }
        }
    }

    @Override
    public int getCount() {
        return mPackages.size();
    }

    @Override
    public String getItem(int i) {
        return mPackages.get(i).packageName;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        LinearLayout layout;
        if (view != null) {
            layout = (LinearLayout) view;
        } else {
            layout = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.package_item_row, parent, false);
        }

        PackageInfo appInfo = mPackages.get(position);

        TextView appName = (TextView) layout.findViewById(R.id.app_name);
        appName.setText(mContext.getPackageManager().getApplicationLabel(appInfo.applicationInfo));

        ImageView logo = (ImageView) layout.findViewById(R.id.app_logo);
        logo.setImageDrawable(mContext.getPackageManager().getApplicationIcon(appInfo.applicationInfo));

        return layout;
    }
}
