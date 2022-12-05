package com.akj.sns_project;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.akj.sns_project.activity.LoginActivity;
import com.akj.sns_project.activity.MainActivity;
import com.akj.sns_project.activity.WritePostActivity;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;

public class Fragment02 extends Fragment {
    private Button btnAction;
    private View view;
    private String genre;
    static RequestQueue requestQueue;
    MainActivity mainActivity;
    String language = "&language=ko-KR";
    String url = "https://api.themoviedb.org/3/discover/movie?api_key=3c314dc629a0e72e9328fe7c33981cf2";
    // 메인 액티비티 위에 올린다.
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) getActivity();
    }

    // 메인 액티비티에서 내려온다.
    @Override
    public void onDetach() {
        super.onDetach();
        mainActivity = null;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_02, container, false);
        btnAction = view.findViewById(R.id.btnAction);
        btnAction.setOnClickListener(onClickListener);

        Fragment01 fragment01;



        if (requestQueue == null) {

            requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        }

        return view;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.btnAction:
                    genre = "&with_genres=" + 28;
                    mainActivity.GenreSearch(url+genre+language);
                    break;

            }
        }
    };



}