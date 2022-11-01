package com.akj.sns_project.activity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.akj.sns_project.Fragment01;
import com.akj.sns_project.Fragment02;
import com.akj.sns_project.Fragment03;
import com.akj.sns_project.Fragment04;
import com.akj.sns_project.Fragment05;
import com.akj.sns_project.PostInfo;
import com.akj.sns_project.R;
import com.akj.sns_project.adapter.MainAdapter;
import com.akj.sns_project.listener.OnPostListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends BasicActivity {
    private static final String TAG = "MainActivity";
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private RecyclerView recyclerView;
    private MainAdapter mainAdapter;
    private ArrayList<PostInfo> postList;
    // 프레그먼트 선언 - 준범
    Fragment01 fragment01;
    Fragment02 fragment02;
    Fragment03 fragment03;
    Fragment04 fragment04;
    Fragment05 fragment05;

    BottomNavigationView bottomNavigationView; // 아래 네비게이션 바 - 준범

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 프레그먼트 변경으로 화면 전환 - 준범
        // 여기서부터 - 준범
        fragment01 = new Fragment01();
        fragment02 = new Fragment02();
        fragment03 = new Fragment03();
        fragment04 = new Fragment04();
        fragment05 = new Fragment05();

        getSupportFragmentManager().beginTransaction().add(R.id.main_frame, new Fragment01()).commit(); //FrameLayout에 fragment.xml 띄우기

        bottomNavigationView = findViewById(R.id.bottomNavi);

        //바텀 네비게이션뷰 안의 아이템 설정
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    //item을 클릭시 id값을 가져와 FrameLayout에 fragment.xml띄우기
                    case R.id.item_fragment1:

                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, fragment01).commit();
                        break;
                    case R.id.item_fragment2:

                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, fragment02).commit();
                        break;
                    case R.id.item_fragment3:

                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, fragment03).commit();
                        break;
                    case R.id.item_fragment4:

                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, fragment04).commit();
                        break;
                    case R.id.item_fragment5:

                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, fragment05).commit();
                        break;
                }
                return true;
            }
        });
        // 여기까지는 건드리기x - 준범

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser(); // 유저정보 받아오는 것 _ 대규

        if (firebaseUser == null) {     // 로그인이 안되어 있으면 로그인 액티비티부터 시작
            myStartActivity(LoginActivity.class);
        } else {
            firebaseFirestore = FirebaseFirestore.getInstance();
            DocumentReference documentReference = firebaseFirestore.collection("users").document(firebaseUser.getUid());
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                            if (document.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            } else {
                                Log.d(TAG, "No such document");     // 파이어베이스 유저정보에 유저정보가 없으면 멤베정보 입력하는 액티비티를 보여줌
                                myStartActivity(MemberInitActivity.class);
                            }
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());    // 정보 불러오는거 실패했을 경우
                    }
                }
            });
        }

        postList = new ArrayList<>();

        mainAdapter = new MainAdapter(MainActivity.this, postList);
        mainAdapter.setOnPostListener(onPostListener);

        recyclerView = findViewById(R.id.recyclerView);
        findViewById(R.id.floatingActionButton).setOnClickListener(onClickListener);
        findViewById(R.id.logoutButton).setOnClickListener(onClickListener);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.setAdapter(mainAdapter);
    }

    @Override
    protected void onResume() {  // 게시글 올리자마자 업데이트 될 수 있도록
        super.onResume();
        postsUpdate();

    }

    OnPostListener onPostListener = new OnPostListener() {
        @Override
        public void onDelete(String id) {       // 게시글 삭제 기능_대규
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
                    break;

                case R.id.floatingActionButton:
                    myStartActivity(WritePostActivity.class);   // 글쓰기 버튼 클릭 시 이동 _ 대규
                    break;
            }
        }
    };

    private void postsUpdate(){
        if (firebaseUser != null) {
            CollectionReference collectionReference = firebaseFirestore.collection("posts");
            collectionReference.orderBy("createdAt", Query.Direction.DESCENDING).get()  // 파이어베이스 posts안에 있는 내용을 createdAt 순서로 정렬
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                postList.clear();   // 초기화 하고 가져오는 방식으로 업데이트
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    postList.add(new PostInfo(
                                            document.getData().get("title").toString(),
                                            (ArrayList<String>) document.getData().get("contents"),
                                            document.getData().get("publisher").toString(),
                                            new Date(document.getDate("createdAt").getTime()),
                                            document.getId())); // postinfo 정해진 형식에 따라 가져온 데이터들 대입해줌 _ 대규
                                }
                                mainAdapter.notifyDataSetChanged();

                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }

    private void myStartActivity(Class c, String id) {
        Intent intent = new Intent(this, c);
        intent.putExtra("id",id);
        startActivity(intent);
    }

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}


