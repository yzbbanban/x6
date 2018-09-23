package com.example.x6.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.x6.R;
import com.example.x6.entity.Bucket;
import com.example.x6.util.LogUtil;

import java.util.List;

public class GridViewAdapter extends BaseAdapter {
    private static final String TAG = "GridViewAdapter";
    private Context context;
    private List<Bucket> buckets;

    public GridViewAdapter(Context context, List<Bucket> buckets) {
        this.context = context;
        this.buckets = buckets;
        LogUtil.info(TAG, String.valueOf(buckets));
    }

    @Override
    public int getCount() {
        return buckets.size();
    }

    @Override
    public Bucket getItem(int position) {
        return buckets.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        Bucket t = getItem(position);
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_bucket, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvId.setText(t.getIdName());
        viewHolder.tv.setText(t.getName());
        return convertView;
    }

    class ViewHolder {
        protected TextView tv;
        protected TextView tvId;

        public ViewHolder(View convertView) {
            tvId = convertView.findViewById(R.id.tv_item_id);
            tv = convertView.findViewById(R.id.tv_item_name);
        }
    }
}
