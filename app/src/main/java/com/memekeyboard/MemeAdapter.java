package com.memekeyboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

public class MemeAdapter extends BaseAdapter {

    private Context context;
    private List<String> memePaths;
    private MemeManager memeManager;

    public MemeAdapter(Context context, List<String> memePaths) {
        this.context = context;
        this.memePaths = memePaths;
        this.memeManager = new MemeManager(context);
    }

    public void updateData(List<String> newMemePaths) {
        this.memePaths = newMemePaths;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return memePaths.size();
    }

    @Override
    public Object getItem(int position) {
        return memePaths.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.meme_grid_item, parent, false);
        }

        ImageView memeImageView = convertView.findViewById(R.id.meme_image_view);

        String memePath = memePaths.get(position);
        File memeFile = new File(memePath);

        // Load image/video thumbnail using Glide
        Glide.with(context)
                .load(memeFile)
                .centerCrop()
                .into(memeImageView);

        return convertView;
    }
}

