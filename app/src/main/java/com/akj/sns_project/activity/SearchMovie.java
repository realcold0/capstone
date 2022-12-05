package com.akj.sns_project.activity;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.akj.sns_project.AppController;
import com.akj.sns_project.Movie;
import com.akj.sns_project.MovieList;
import com.akj.sns_project.Poster;
import com.akj.sns_project.R;
import com.akj.sns_project.adapter.PosterAdapter;
import com.akj.sns_project.adapter.SearchMovieAdapter;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchMovie#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchMovie extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private View view;

    private String mParam1;
    private String mParam2;
    static RequestQueue requestQueue;
    private ArrayList<Poster> posters;

    private RecyclerView posterRecyclerView;
    private SearchMovieAdapter searchMovieAdapter;
    String url;

    public SearchMovie(String url) {
        // Required empty public constructor
        this.url = url;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchMovie.
     */
    public static SearchMovie newInstance(String param1, String param2) {
        SearchMovie fragment = new SearchMovie("https://api.themoviedb.org/3/discover/movie?api_key=3c314dc629a0e72e9328fe7c33981cf2");
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    private void initRecyclerViewAndAdapter() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        posterRecyclerView.setLayoutManager(gridLayoutManager);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_search_movie, container, false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                makeRequest(url);
            }
        }).start();

        if (requestQueue == null) {

            requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        }

        posterRecyclerView = view.findViewById(R.id.searchList);
        posterRecyclerView.setHasFixedSize(true);

        posterRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        posters = new ArrayList<Poster>();
        searchMovieAdapter = new SearchMovieAdapter();
        posterRecyclerView.setAdapter(searchMovieAdapter);
        initRecyclerViewAndAdapter();

        return  view;

    }

    public void makeRequest(String url) {
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.v("genre search", response); //요청 출력해보기
                Gson gson = new Gson();  //gson라이브러리 선언

                //ListView PosterList = view.findViewById(R.id.ListView);  리스트뷰 추가후 수정예정

                MovieList movieList = gson.fromJson(response, MovieList.class); //gson으로 Json파일 object로 변환


                Movie movie = new Movie();
                posters = new ArrayList<Poster>();

                for(int i = 0; i< movieList.results.size(); i++)
                {
                    movie = movieList.results.get(i);
                    posters.add(new Poster(movie.title.toString(), movie.poster_path));
                }



                searchMovieAdapter = new SearchMovieAdapter(getActivity(), posters);
                posterRecyclerView.setAdapter(searchMovieAdapter);
                Log.v("genre Posters", posters.get(0).toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error ", error.getMessage());
            }
        }
        ) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }
        };
        request.setShouldCache(false);
        Log.v("SendRequest", "요청 보냄");
        //requestQueue.add(request);
        AppController.getInstance(getActivity()).addToRequestQueue(request);  //gson리퀘스트 큐에 넣기
    }
}