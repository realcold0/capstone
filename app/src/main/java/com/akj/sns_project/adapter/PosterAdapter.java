package com.akj.sns_project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.akj.sns_project.Poster;
import com.akj.sns_project.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class PosterAdapter extends RecyclerView.Adapter<PosterAdapter.PosterViewHolder> {
    private Context context;
    private ArrayList<Poster> posterList = new ArrayList<>();

    public PosterAdapter()
    {

    }
    public PosterAdapter(Context context, ArrayList<Poster> posterList){
        this.context = context;
        this.posterList = posterList;
    }


    @Override
    public PosterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.item_poster,parent,false);
        return new PosterViewHolder(v);
    }
    @Override
    public void onBindViewHolder(@NonNull final PosterViewHolder holder, int position) {
        Poster currentPoster = posterList.get(position);
        String imageUrl = currentPoster.getImageUrl();
        Glide.with(context).load(imageUrl).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return posterList.size();
    }
    public class PosterViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageView;

        public PosterViewHolder(View itemView)
        {
            super(itemView);
            imageView= itemView.findViewById(R.id.image_view);
        }
    }

}
