package com.chesssystem.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.bumptech.glide.Glide;
import com.chesssystem.R;
import com.chesssystem.item.MainItem;
import com.chesssystem.util.DoubleToInt;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @author lyg
 * @time 2016-7-4上午9:47:47
 */
public class CollectionAdapter extends ArrayAdapter<MainItem> {
    private List<MainItem> mainItems;
    private Context mContext;
    private LayoutInflater lInflater;
    private ViewHolder holder;

    public CollectionAdapter(Context context, List<MainItem> datas) {
        super(context, -1, datas);
        mContext = context;
        mainItems = datas;
        lInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        holder = null;
        if (convertView == null) {
            convertView = lInflater.inflate(R.layout.item_collection, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.tv_collection_title);
            holder.detail = (TextView) convertView.findViewById(R.id.tv_collection_detail);
            holder.distance = (TextView) convertView.findViewById(R.id.tv_collection_distance);
            holder.image = (ImageView) convertView.findViewById(R.id.iv_collection_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.name.setText(getItem(position).getName());
        holder.detail.setText(getItem(position).getDetail());
        holder.distance.setText(DoubleToInt.DoubleToDistance(getItem(position).getDistance()));
        String tag = (String) holder.image.getTag(R.id.imageloader_uri);
        if (!getItem(position).getImagepath().equals(tag)) {
            Glide.with(mContext)
                    .load(getItem(position).getImagepath())
                    .placeholder(R.color.gray2)
                    .error(R.color.gray2)
                    .skipMemoryCache(true)
                    .centerCrop()
                    .into(holder.image);
            holder.image.setTag(R.id.imageloader_uri, getItem(position).getImagepath());
        }
        return convertView;
    }

    private class ViewHolder {
        private TextView name;
        private TextView detail;
        private TextView distance;
        private ImageView image;
    }

}
