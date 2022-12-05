package com.akj.sns_project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.akj.sns_project.Poster;
import com.akj.sns_project.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class SearchMovieAdapter extends RecyclerView.Adapter<SearchMovieAdapter.SearchMovieViewHolder>{
    private Context context;
    private ArrayList<Poster> posterList = new ArrayList<>();

    public SearchMovieAdapter()
    {

    }

    public SearchMovieAdapter(Context context, ArrayList<Poster> posterList){
        this.context = context;
        this.posterList = posterList;
    }

    @NonNull
    @Override
    public SearchMovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_search_poster,parent,false);
        return new SearchMovieViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final SearchMovieAdapter.SearchMovieViewHolder  holder, int position) {
        Poster currentPoster = posterList.get(position);
        String imageUrl = currentPoster.getImageUrl();
        Glide.with(context).load(imageUrl).into(holder.view);
    }

    @Override
    public int getItemCount() {
        return posterList.size();
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class SearchMovieViewHolder extends RecyclerView.ViewHolder {
        public ImageView view;

        SearchMovieViewHolder(View itemView) {
            super(itemView);
            view = itemView.findViewById(R.id.poster_image_view);
        }
    }
}
