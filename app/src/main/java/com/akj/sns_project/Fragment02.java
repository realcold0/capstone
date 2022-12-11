package com.akj.sns_project;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.akj.sns_project.activity.MainActivity;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;

public class Fragment02 extends Fragment {
    private Button btnAction;
    private Button btnComedy;
    private Button btnRomance;
    private Button btnHorror;
    private Button btnAni;
    private Button btnCrime;
    private Button btnAdventure;
    private Button btnAllGenre;
    private Button btnSF;
    private Button btnMusic;
    private Button btnDocumentary;
    private Button btnHistory;
    private Button btnWar;
    private Button btnMystery;
    private Button btnFantasy;
    private Button btnFamily;

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

        btnAni = view.findViewById(R.id.btnAni);
        btnAni.setOnClickListener(onClickListener);

        btnCrime = view.findViewById(R.id.btnCrime);
        btnCrime.setOnClickListener(onClickListener);

        btnHorror = view.findViewById(R.id.btnHorror);
        btnHorror.setOnClickListener(onClickListener);

        btnRomance = view.findViewById(R.id.btnRomance);
        btnRomance.setOnClickListener(onClickListener);

        btnAdventure = view.findViewById(R.id.btnAdventure);
        btnAdventure.setOnClickListener(onClickListener);

        btnComedy = view.findViewById(R.id.btnComedy);
        btnComedy.setOnClickListener(onClickListener);

        btnAllGenre = view.findViewById(R.id.btn_all_genre);
        btnAllGenre.setOnClickListener(onClickListener);

        btnMusic = view.findViewById(R.id.btnMusic);
        btnMusic.setOnClickListener(onClickListener);

        btnSF = view.findViewById(R.id.btnSF);
        btnSF.setOnClickListener(onClickListener);

        btnDocumentary = view.findViewById(R.id.btnDocumentary);
        btnDocumentary.setOnClickListener(onClickListener);

        btnHistory = view.findViewById(R.id.btnHistory);
        btnHistory.setOnClickListener(onClickListener);

        btnWar = view.findViewById(R.id.btnWar);
        btnWar.setOnClickListener(onClickListener);

        btnMystery = view.findViewById(R.id.btnMystery);
        btnMystery.setOnClickListener(onClickListener);

        btnFantasy = view.findViewById(R.id.btnFantasy);
        btnFantasy.setOnClickListener(onClickListener);

        btnFamily = view.findViewById(R.id.btnFamily);
        btnFamily.setOnClickListener(onClickListener);

        // 파이어베이스 DB 초기화
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // 여기부터 해시태그 정보 가져오기
        //CollectionReference 는 파이어스토어의 컬렉션을 참조하는 객체다.
        CollectionReference productRef = db.collection("hashtag");
        //get()을 통해서 해당 컬렉션의 정보를 가져온다.
        productRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                //작업이 성공적으로 마쳤을때
                if (task.isSuccessful()) {
                    String str = "";
                    //컬렉션 아래에 있는 모든 정보를 가져온다.
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        str = document.getId().toString() + " " + str;
                        //document.getData() or document.getId() 등등 여러 방법으로
                        //데이터를 가져올 수 있다.
                    }
                    TextView textViewhash = view.findViewById(R.id.textViewvvv);
                    textViewhash.setText(str);
                    //그렇지 않을때
                } else {

                }
            }
        });

        Fragment01 fragment01;


        if (requestQueue == null) {

            requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        }

        return view;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {   //각각 클릭시 이벤트, 장르 코드만 바꾸어 검색 함수 실행한다.
            switch (v.getId()) {

                case R.id.btnAction:
                    genre = "&with_genres=" + 28;
                    mainActivity.GenreSearch(url + genre + language);
                    break;
                case R.id.btnAdventure:
                    genre = "&with_genres=" + 12;
                    mainActivity.GenreSearch(url + genre + language);
                    break;
                case R.id.btnAni:
                    genre = "&with_genres=" + 16;
                    mainActivity.GenreSearch(url + genre + language);
                    break;
                case R.id.btnComedy:
                    genre = "&with_genres=" + 35;
                    mainActivity.GenreSearch(url + genre + language);
                    break;
                case R.id.btnCrime:
                    genre = "&with_genres=" + 80;
                    mainActivity.GenreSearch(url + genre + language);
                    break;
                case R.id.btnRomance:
                    genre = "&with_genres=" + 10749;
                    mainActivity.GenreSearch(url + genre + language);
                    break;
                case R.id.btnSF:
                    genre = "&with_genres=" + 878;
                    mainActivity.GenreSearch(url + genre + language);
                    break;
                case R.id.btnMusic:
                    genre = "&with_genres=" + 10402;
                    mainActivity.GenreSearch(url + genre + language);
                    break;

                case R.id.btn_all_genre:

                    mainActivity.GenreSearch("https://api.themoviedb.org/3/trending/movie/week?api_key=3c314dc629a0e72e9328fe7c33981cf2&page=1&language=ko-KR");
                    break;
                case R.id.btnDocumentary:
                    genre = "&with_genres=" + 99;
                    mainActivity.GenreSearch(url + genre + language);
                    break;

                case R.id.btnHistory:
                    genre = "&with_genres=" + 36;
                    mainActivity.GenreSearch(url + genre + language);
                    break;

                case R.id.btnWar:
                    genre = "&with_genres=" + 10752;
                    mainActivity.GenreSearch(url + genre + language);
                    break;
                case R.id.btnMystery:
                    genre = "&with_genres=" + 9648;
                    mainActivity.GenreSearch(url + genre + language);
                    break;

                case R.id.btnFantasy:
                    genre = "&with_genres=" + 14;
                    mainActivity.GenreSearch(url + genre + language);
                    break;

                case R.id.btnFamily:
                    genre = "&with_genres=" + 10751;
                    mainActivity.GenreSearch(url + genre + language);
                    break;

            }
        }
    };


}