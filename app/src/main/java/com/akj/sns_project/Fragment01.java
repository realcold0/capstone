package com.akj.sns_project;


import static com.akj.sns_project.Util.ADMIN_DK;
import static com.akj.sns_project.Util.ADMIN_JB;
import static com.akj.sns_project.Util.ADMIN_JY;
import static com.akj.sns_project.Util.ADMIN_SH;
import static com.akj.sns_project.Util.ADMIN_YJ;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.SearchView;
import android.widget.Toast;

import com.akj.sns_project.activity.LoginActivity;
import com.akj.sns_project.activity.MainActivity;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
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
    private StorageReference storageRef;
    private View view;
    private Button logoutButton;
    private FloatingActionButton floatingActionButton;
    private RecyclerView recyclerView;
    private RecyclerView posterRecyclerView;
    private PosterAdapter posterAdapter;
    private PostInfo postInfo;
    private int successCount;
    private FirebaseUser user;
    private String userUid;
    private String publisher;
    private SearchView movieSearch;
    private NavController navController;

    static RequestQueue requestQueue;
    private ArrayList<Poster> posters;
    MainActivity mainActivity;
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
        view = inflater.inflate(R.layout.activity_board, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser(); // 파이어베이스에서 유저정보를 받아오는데 _ 대규
        postInfo = (PostInfo) getActivity().getIntent().getSerializableExtra("postInfo");

        new Thread(new Runnable() {
            @Override
            public void run() {
                makeRequest("https://api.themoviedb.org/3/trending/movie/week?api_key=3c314dc629a0e72e9328fe7c33981cf2&page=1&language=ko-KR");
            }
        }).start();


        // 파이어베이스 초기화 함수들
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        if (requestQueue == null) {

            requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        }

        posterRecyclerView = view.findViewById(R.id.PosterList);
        posterRecyclerView.setHasFixedSize(true);
        posterRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));

        posters = new ArrayList<Poster>();
        posterAdapter = new PosterAdapter();

        posterRecyclerView.setAdapter(posterAdapter);


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

        movieSearch = view.findViewById(R.id.searchMovie);
        movieSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mainActivity.GenreSearch(SearchMovieQuery(query));
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        initRecyclerViewAndAdapter();

        // 유저정보
        user = FirebaseAuth.getInstance().getCurrentUser();
        userUid = user.getUid().toString();

        return view;
    }

    private String SearchMovieQuery(String query) //입력하고 엔터 눌렀을때 영화 이름 검색 쿼리 만들어서 화면 전환
    {
        String search = "https://api.themoviedb.org/3/search/movie?api_key=3c314dc629a0e72e9328fe7c33981cf2&query=" + query.replace(" ","+") + "&lnaguage=ko-KR";

        //https://api.themoviedb.org/3/search/movie?api_key=3c314dc629a0e72e9328fe7c33981cf2&query=써니&lnaguage=ko-KR
        return search;
    }

    private void initRecyclerViewAndAdapter() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
    }

    @Override
    public void onResume() {  // 게시글 올리자마자 업데이트 될 수 있도록
        super.onResume();
        postsUpdate();
    }

    OnPostListener onPostListener = new OnPostListener() { //인터페이스인 OnPostListener를 가져와서 구현해줌
        @Override
        public void onDelete(int position) {       // 게시글 삭제 기능_대규 여기서부터

            final String id = postList.get(position).getId();
            ArrayList<String> contentsList = postList.get(position).getContents();
            publisher = postList.get(position).getPublisher();
            if(userUid.equals(publisher)||userUid.equals(ADMIN_DK)||userUid.equals(ADMIN_JB)||userUid.equals(ADMIN_JY)||userUid.equals(ADMIN_SH)||userUid.equals(ADMIN_YJ)) {
                for (int i = 0; i < contentsList.size(); i++) {
                    String contents = contentsList.get(i);
                    if (Patterns.WEB_URL.matcher(contents).matches() && contents.contains("https://firebasestorage.googleapis.com/v0/b/sns-project-29021.appspot.com/o/posts")) {   // 글 내용에 사진이나 동영상이 있을 경우
                        // 앞에 조건만 있으면 URL들어오기만하면 다 이미지로 변환해버리니까 뒤에 파이어베이스에서 가져오는 주소인 사진들만 사진변환하게추가 11.23 대규
                        successCount++;
                        String[] list = contents.split("\\?"); // 저장되는 이미지 주소를 \\와 %2F로 잘라서 저장하여
                        String[] list2 = list[0].split("%2F");
                        String name = list2[list2.length - 1];
                        // Create a reference to the file to delete
                        StorageReference desertRef = storageRef.child("posts/" + id + "/" + name);
                        // Delete the file
                        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                successCount--;
                                storeUploader(id);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                startToast("삭제 실패");
                            }
                        });
                    }
                }
                storeUploader(id);
            }
            else{
                startToast("다른사람의 게시글을 삭제할 수 없습니다");
            }
        }

        @Override
        public void onModify(int position) {
            myStartActivity(WritePostActivity.class, postList.get(position));
        }   // 게시글 수정 기능_대규
        @Override
        public void onGoBlack(int position) {
            String controllNum = postList.get(position).getId();
            final DocumentReference docRef = postInfo == null ?
                    firebaseFirestore.collection("posts").document() :
                    firebaseFirestore.collection("posts").document(postInfo.getId());
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    PostInfo post = documentSnapshot.toObject(PostInfo.class);
                    firebaseFirestore.collection("blackposts").document(docRef.getId())
                            .set(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "Success : 검은색 게시판 이동");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error : 검은색 게시판 이동", e);
                                }
                            });
                }
            });
            final String id = postList.get(position).getId();
            ArrayList<String> contentsList = postList.get(position).getContents();

            for (int i = 0; i < contentsList.size(); i++) {
                String contents = contentsList.get(i);
                if (Patterns.WEB_URL.matcher(contents).matches() && contents.contains("https://firebasestorage.googleapis.com/v0/b/sns-project-29021.appspot.com/o/posts")) {   // 글 내용에 사진이나 동영상이 있을 경우
                    // 앞에 조건만 있으면 URL들어오기만하면 다 이미지로 변환해버리니까 뒤에 파이어베이스에서 가져오는 주소인 사진들만 사진변환하게추가 11.23 대규
                    successCount++;
                    String[] list = contents.split("\\?"); // 저장되는 이미지 주소를 \\와 %2F로 잘라서 저장하여
                    String[] list2 = list[0].split("%2F");
                    String name = list2[list2.length - 1];
                    // Create a reference to the file to delete
                    StorageReference desertRef = storageRef.child("posts/"+id+"/"+name);
                    // Delete the file
                    desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            successCount--;
                            storeUploader(id);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            startToast("화이트에서 제거 실패");
                        }
                    });
                }
            }
            storeUploader_Black(id);
        }
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
    private void postsUpdate() {
        if (firebaseUser != null) {
            CollectionReference collectionReference = firebaseFirestore.collection("posts");    // 파이어베이스 posts폴더를 사용
            //collectionReference.document().update()
            collectionReference.orderBy("like", Query.Direction.DESCENDING).get()  // 파이어베이스 posts안에 있는 내용을 createdAt 순서로 정렬
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                postList.clear();   // 초기화 하고 가져오는 방식으로 업데이트
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData().get("like"));
                                    postList.add(new PostInfo(  // 여기서부터
                                            document.getData().get("title").toString(),
                                            (ArrayList<String>) document.getData().get("contents"),
                                            document.getData().get("publisher").toString(),
                                            new Date(document.getDate("createdAt").getTime()),
                                            document.getId(),
                                            Integer.parseInt(document.getData().get("like").toString()),
                                            Integer.parseInt(document.getData().get("unlike").toString()),
                                            document.getData().get("saveLocation").toString(),
                                            (ArrayList<String>) document.getData().get("favorites"),
                                            (ArrayList<String>) document.getData().get("unfavorites")
                                    )); // 여기까지 postinfo 정해진 형식에 따라 가져온 데이터들 대입해줌 _ 대규
                                }
                                mainAdapter.notifyDataSetChanged();
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }

    private void storeUploader(String id) {
        if (successCount == 0) {
            firebaseFirestore.collection("posts").document(id)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            startToast("게시글을 삭제하였습니다.");
                            postsUpdate();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            startToast("게시글을 삭제하지 못하였습니다.");
                        }
                    });
        }
    }

    private void storeUploader_Black(String id){
        if(successCount == 0) {
            firebaseFirestore.collection("posts").document(id)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            startToast("검은색 이동 : 게시글을 삭제하였습니다.");
                            postsUpdate();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            startToast("검은색 이동 : 게시글을 삭제하지 못하였습니다.");
                        }
                    });
        }
    }

    private void myStartActivity(Class c) { // 액티비티 이동하는 함수
        Intent intent = new Intent(getActivity(), c);
        startActivity(intent);
    }

    private void myStartActivity(Class c, PostInfo postInfo) {  // 게시글 수정할때 사용하는 액티비티 이동함수
        Intent intent = new Intent(getActivity(), c);
        intent.putExtra("postInfo", postInfo);
        startActivity(intent);
    }

    private void startToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    public void request(String urlStr) {
        StringBuilder output = new StringBuilder();
        try {
            URL url = new URL(urlStr);

            HttpURLConnection connection = (HttpsURLConnection) url.openConnection();

            if (connection != null) {
                connection.setConnectTimeout(10000);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);

                int resCode = connection.getResponseCode();
                BufferedReader reader = new BufferedReader((new InputStreamReader(connection.getInputStream())));
                String line = null;
                while (true) {
                    line = reader.readLine();
                    if (line == null) {
                        break;
                    }

                    output.append(line + "\n");
                }
                reader.close();
                connection.disconnect();
            }

            Log.w("output popular", output.toString());
        } catch (Exception ex) {
            System.out.println("예외발생함" + ex.toString());
        }
    }

    public void makeRequest(String url) {
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.v("output popular", response); //요청 출력해보기
                Gson gson = new Gson();  //gson라이브러리 선언

                //ListView PosterList = view.findViewById(R.id.ListView);  리스트뷰 추가후 수정예정

                MovieList movieList = gson.fromJson(response, MovieList.class); //gson으로 Json파일 object로 변환


                ArrayList<Movie> movies = new ArrayList<Movie>();

                posters = new ArrayList<Poster>();

                for(int i =0;i < movieList.results.size(); i++)
                {
                    Movie movie = movieList.results.get(i);
                    movies.add(movie);

                }

                //Movie movie3 = movieList.results.get(2);


                //posters.add(new Poster(movie.title.toString(), movie.poster_path));
                //posters.add(new Poster(movie2.title.toString(), movie2.poster_path));
                //posters.add(new Poster(movie3.title.toString(), movie3.poster_path.toString()));
                //posters.add(new Poster(movie4.title.toString(), movie4.poster_path.toString()));

                posterAdapter = new PosterAdapter(getActivity(), movies);
                posterRecyclerView.setAdapter(posterAdapter);


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

