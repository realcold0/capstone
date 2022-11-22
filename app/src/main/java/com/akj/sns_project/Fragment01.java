package com.akj.sns_project;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.akj.sns_project.activity.BoardActivity;
import com.akj.sns_project.activity.LoginActivity;
import com.akj.sns_project.activity.MemberInitActivity;
import com.akj.sns_project.activity.WritePostActivity;
import com.akj.sns_project.adapter.MainAdapter;
import com.akj.sns_project.adapter.PosterAdapter;
import com.akj.sns_project.listener.OnPostListener;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;


public class Fragment01 extends Fragment {
//프래그먼트 로드
    private static final String TAG = "BoardActicity";
    private FirebaseUser firebaseUser;              // 파이어베이스 유저 정보 가져오기 위해 생성한 이름
    private FirebaseFirestore firebaseFirestore;    // 파이어베이스스토어에서 정보 가져오기 위해 사용한 이름
    private MainAdapter mainAdapter;                // mainadapter 사용하기 위한 이름
    private ArrayList<PostInfo> postList;           // 게시글 정보들을 저장하기 위한 이름

    private View view;
    private Button logoutButton;
    private FloatingActionButton floatingActionButton;
    private RecyclerView recyclerView;

    static RequestQueue requestQueue;
    private ArrayList<Poster> posters;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.activity_board, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser(); // 파이어베이스에서 유저정보를 받아오는데 _ 대규



        new Thread(new Runnable() {
            @Override
            public void run() {
                makeRequest("https://api.themoviedb.org/3/trending/all/week?api_key=3c314dc629a0e72e9328fe7c33981cf2&page=1&language=ko-KR");
            }
        }).start();

        if(requestQueue == null)
        {

            requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        }






        if (firebaseUser == null) {// 위에서 받아온 유저정보가 NULL값이면 == 로그인이 안되어 있으면 로그인 액티비티부터 시작
            myStartActivity(LoginActivity.class);
        } else {
            //여기서부터
            firebaseFirestore = FirebaseFirestore.getInstance();    // 로그인이 되어 있으면 FirebaseStore에서 정보를 받아오는데
            DocumentReference documentReference = firebaseFirestore.collection("users").document(firebaseUser.getUid());    // 파이어베이스스토어의 users폴더 안에서 유저의 UID를 받아온다
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {  // 불러오는 것에 성공했다면
                        DocumentSnapshot document = task.getResult(); // 결과를 여기에 넣고
                        if (document != null) { // 넣은 결과 값이 null이 아니라면
                            if (document.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData()); // 로그로 불러온 정보를 보여주고
                            } else {
                                Log.d(TAG, "No such document");     // 파이어베이스 유저정보에 유저정보가 없으면 멤버정보 입력하는 액티비티를 보여줌
                                myStartActivity(MemberInitActivity.class);
                            }
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());    // 정보 불러오는거 실패했을 경우
                    }
                }
            });
            // 여기까지는 파이어베이스에서 제공하는 코드로 파이어스토어에 저장되어 있는 데이터 읽어오는 코드
        }

        postList = new ArrayList<>();   // 게시글을 저장하기 위해 선언한 배열

        //게시물 업데이트(새로고침)을 위한 메서드. 데이터가 업데이트 되면 adapter를 다시 바꿔줘야함.
        //MainAdaper에서 넘겨줌.
        mainAdapter = new MainAdapter(getActivity(), postList);
        mainAdapter.setOnPostListener(onPostListener); //onPostListener를 넘겨주면 MainAdapter에서도 쓸수있음.

        logoutButton = view.findViewById(R.id.logoutButton);
        floatingActionButton = view.findViewById(R.id.floatingActionButton);
        recyclerView = view.findViewById(R.id.recyclerView);

        logoutButton.setOnClickListener(onClickListener);
        floatingActionButton.setOnClickListener(onClickListener);

        recyclerView.setHasFixedSize(true); // 글을 불러오고 나서는 recyclerview를 글 갯수에 따라서 크기를 조절한다
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity())); // recyclerview를 수직으로 보여주는 linearlayoutmanager
        recyclerView.setAdapter(mainAdapter);

        initRecyclerViewAndAdapter();

        return view;
    }

    private void initRecyclerViewAndAdapter() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),2);
        recyclerView.setLayoutManager(gridLayoutManager);
    }

    @Override
    public void onResume() {  // 게시글 올리자마자 업데이트 될 수 있도록
        super.onResume();
        postsUpdate();
    }

    OnPostListener onPostListener = new OnPostListener() { //인터페이스인 OnPostListener를 가져와서 구현해줌
        @Override
        public void onDelete(String id) {       // 게시글 삭제 기능_대규 여기서부터
            firebaseFirestore.collection("posts").document(id)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        // 여기까지 파이어베이스스토어에서 제공하는 글 삭제 기능
                        @Override
                        public void onSuccess(Void aVoid) { // 성공적으로 글 삭제시 토스트 메세지로 글 삭제에 성공했다고 알려줌
                            startToast("게시글을 삭제하였습니다.");
                            postsUpdate();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() { // 글 삭제 실패시
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            startToast("게시글을 삭제하지 못하였습니다.");
                        }
                    });
        }

        @Override
        public void onModify(String id) {
            myStartActivity(WritePostActivity.class,id);
        }   // 게시글 수정 기능_대규
    };

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.logoutButton:
                    FirebaseAuth.getInstance().signOut();   // 파이어베이스에 로그아웃 신호 보내줌 _ 대규
                    myStartActivity(LoginActivity.class);   // 로그인 액티비티로 이동 _ 대규
                    startToast("로그아웃");
                    break;

                case R.id.floatingActionButton:
                    myStartActivity(WritePostActivity.class);   // 글쓰기 버튼 클릭 시 이동 _ 대규
                    break;
            }
        }
    };

    //실제 게시물을 보여주고 업데이트 해주는 코드
    private void postsUpdate(){
        if (firebaseUser != null) {
            CollectionReference collectionReference = firebaseFirestore.collection("posts");    // 파이어베이스 posts폴더를 사용
            collectionReference.orderBy("createdAt", Query.Direction.DESCENDING).get()  // 파이어베이스 posts안에 있는 내용을 createdAt 순서로 정렬
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                postList.clear();   // 초기화 하고 가져오는 방식으로 업데이트
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    postList.add(new PostInfo(  // 여기서부터
                                            document.getData().get("title").toString(),
                                            (ArrayList<String>) document.getData().get("contents"),
                                            document.getData().get("publisher").toString(),
                                            new Date(document.getDate("createdAt").getTime()),
                                            document.getId())); // 여기까지 postinfo 정해진 형식에 따라 가져온 데이터들 대입해줌 _ 대규
                                }
                                mainAdapter.notifyDataSetChanged();
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }
    private void myStartActivity(Class c) { // 액티비티 이동하는 함수
        Intent intent = new Intent(getActivity(), c);
        startActivity(intent);
    }

    private void myStartActivity(Class c, String id) {  // 게시글 수정할때 사용하는 액티비티 이동함수
        Intent intent = new Intent(getActivity(), c);
        intent.putExtra("id",id);
        startActivity(intent);
    }
    private void startToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }
    public void request(String urlStr)
    {
        StringBuilder output  = new StringBuilder();
        try{
            URL url = new URL(urlStr);

            HttpURLConnection connection = (HttpsURLConnection)url.openConnection();

            if(connection !=null)
            {
                connection.setConnectTimeout(10000);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);

                int resCode = connection.getResponseCode();
                BufferedReader reader = new BufferedReader((new InputStreamReader(connection.getInputStream())));
                String line = null;
                while(true)
                {
                    line = reader.readLine();
                    if(line ==null)
                    {
                        break;
                    }

                    output.append(line +"\n");
                }
                reader.close();
                connection.disconnect();
            }

            Log.w("output popular",output.toString());
        }
        catch (Exception ex)
        {
            System.out.println("예외발생함" + ex.toString());
        }
    }

    public void makeRequest(String url)
    {
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.v("output popular", response); //요청 출력해보기
                Gson gson = new Gson();  //gson라이브러리 선언

                //ListView PosterList = view.findViewById(R.id.ListView);  리스트뷰 추가후 수정예정

                MovieList movieList = gson.fromJson(response, MovieList.class); //gson으로 Json파일 object로 변환
                Movie movie = movieList.results.get(0);

                posters = new ArrayList<Poster>();
                posters.add(new Poster(movie.title.toString(),"https://image.tmdb.org/t/p/w500" + movie.poster_path.toString()));

                final PosterAdapter posterAdapter = new PosterAdapter(getActivity(),posters);
                //PosterList.setAdapter(posterAdapter); 리스트뷰 추가후 수정예정

                Log.v("Poster", posters.get(0).toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error ", error.getMessage());
            }
        }
        ){
            protected  Map<String, String> getParams() throws AuthFailureError{
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }
        };
        request.setShouldCache(false);
        Log.v("SendRequest","요청 보냄");
        //requestQueue.add(request);
        AppController.getInstance(getActivity()).addToRequestQueue(request);  //gson리퀘스트 큐에 넣기
    }
}