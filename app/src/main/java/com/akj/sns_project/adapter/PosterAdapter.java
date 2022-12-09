package com.akj.sns_project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.akj.sns_project.Fragment04;
import com.akj.sns_project.Movie;
import com.akj.sns_project.MovieInfo;
import com.akj.sns_project.Poster;
import com.akj.sns_project.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class PosterAdapter extends RecyclerView.Adapter<PosterAdapter.PosterViewHolder> {
    private Context context;
    private ArrayList<Movie> movieList = new ArrayList<Movie>();

    public interface OnItemCllickListener{
        void onItemClick(View view, int pos);
    }

    private OnItemCllickListener itemCllickListener;

    public void setOnItemClickListener(OnItemCllickListener listner)
    {
        this.itemCllickListener = listner;
    }

    public PosterAdapter()
    {

    }
    public PosterAdapter(Context context, ArrayList<Movie> movieList){
        this.context = context;
        this.movieList = movieList;
    }


    @Override
    public PosterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.item_poster,parent,false);


        return new PosterViewHolder(v);
    }
    @Override
    public void onBindViewHolder(@NonNull final PosterViewHolder holder, int position) {
        Movie currentPoster = movieList.get(position);
        String imageUrl = currentPoster.GetPosterPath();
        Glide.with(context).load(imageUrl).into(holder.imageView);

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
        return movieList.size();
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
