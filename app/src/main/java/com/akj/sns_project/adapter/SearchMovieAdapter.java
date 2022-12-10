package com.akj.sns_project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.akj.sns_project.Movie;
import com.akj.sns_project.MovieInfo;
import com.akj.sns_project.Poster;
import com.akj.sns_project.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class SearchMovieAdapter extends RecyclerView.Adapter<SearchMovieAdapter.SearchMovieViewHolder>{
    private Context context;
    private ArrayList<Movie> posterList = new ArrayList<Movie>();

    public interface OnItemCllickListener{
        void onItemClick(View view, int pos);
    }

    private PosterAdapter.OnItemCllickListener itemCllickListener;

    public void setOnItemClickListener(PosterAdapter.OnItemCllickListener listner)
    {
        this.itemCllickListener = listner;
    }

    public SearchMovieAdapter()
    {

    }

    public SearchMovieAdapter(Context context, ArrayList<Movie> posterList){
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
        Movie currentPoster = posterList.get(position);
        String imageUrl = currentPoster.GetPosterPath();
        Glide.with(context).load(imageUrl).into(holder.view);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity)v.getContext();
                MovieInfo movieInfo =  new MovieInfo(currentPoster);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, movieInfo).addToBackStack(null).commit();
            }
        });

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
