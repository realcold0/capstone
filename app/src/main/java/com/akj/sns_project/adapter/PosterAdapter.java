package com.akj.sns_project.adapter;



import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.akj.sns_project.Fragment01;
import com.akj.sns_project.Poster;
import com.akj.sns_project.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class PosterAdapter extends BaseAdapter {
    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<Poster> sample;

    public PosterAdapter(Context context, ArrayList<Poster> data) {
        mContext = context;
        sample = data;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.item_poster, null);

        ImageView imageView = (ImageView)view.findViewById(R.id.Poster);

        Glide.with(mContext).load(sample.get(position).getPoster()).into(imageView);
        return null;
    }
}
