package com.akj.sns_project;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class MovieInfo extends Fragment {




    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private Movie movie;
    private View view;
    private String title;
    private float rate;
    private String releaseDate;
    private String overView;
    private String posterURL;

    private TextView textViewTitle;
    private TextView textViewReleaseDate;
    private TextView textViewOverView;

    private ImageView imageviewPoster;

    public MovieInfo(Movie movie) {
        this.movie = movie;
        this.title = movie.title;
        this.rate = movie.vote_average;
        this.overView = movie.overview;
        this.posterURL = movie.GetPosterPath();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment MovieInfo.
     */



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_movie_info, container, false);

        textViewOverView = view.findViewById(R.id.textViewOverView);
        textViewTitle = view.findViewById(R.id.textViewMovieTitle);
        textViewReleaseDate = view.findViewById(R.id.textViewMovieReleaseDate);
        imageviewPoster = view.findViewById(R.id.imageViewPoster);

        textViewTitle.setText(title);
        textViewTitle.setTextColor(Color.parseColor("#181D31"));
        textViewOverView.setText(overView);
        textViewReleaseDate.setText(releaseDate);
        Glide.with(getActivity()).load(posterURL).into(imageviewPoster);


        return view;

    }
}