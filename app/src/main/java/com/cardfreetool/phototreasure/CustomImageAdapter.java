package com.cardfreetool.phototreasure;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by grishmashah on 11/7/14.
 */
public class CustomImageAdapter extends ArrayAdapter {

    Context context;
    int resourceId;
    List<String> imageUrls;

    public CustomImageAdapter(Context context, int resourceId, List<String> imageUrls) {
        super(context,resourceId,imageUrls);
        this.context = context;
        this.resourceId = resourceId;
        this.imageUrls = imageUrls;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        CustomImageViewHolder customImageViewHolder = null;

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.image_view, null);
            customImageViewHolder = new CustomImageViewHolder();
            customImageViewHolder.imageView = (ImageView) convertView.findViewById(R.id.img_thumbnail);
            convertView.setTag(customImageViewHolder);
        } else {
            customImageViewHolder = (CustomImageViewHolder) convertView.getTag();
        }

        // 3rd party library picasso to load image bitmap from passed URL
        // Picasso takes care of memory and disk caching for bitmaps
        String url = imageUrls.get(position);
        Picasso.with(context).load(url).into(customImageViewHolder.imageView);

        return convertView;
    }

    private class CustomImageViewHolder {
        ImageView imageView;
    }
}
