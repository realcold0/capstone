package com.akj.sns_project;

import androidx.annotation.NonNull;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.akj.sns_project.activity.AdminActivity;
import com.akj.sns_project.activity.LoginActivity;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

public class Fragment05 extends Fragment implements View.OnClickListener {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance(); // 인스턴스 초기화
    private View view;
    private Fragment_Post fragment_post;
    private Fragment05 fragment05;
    private String AdminDK = "KnK0SPLNuGTnEQWoEbcCkkLGrFx2";
    private String userid = "";
    private String hash = "";
    private FirebaseFirestore firebaseFirestore;    // 파이어베이스스토어에서 정보 가져오기 위해 사용한 이름


    @Override
    public void onStart() { // 시작할 때
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser(); // 로그인 되어 있는지 확인
        if (currentUser != null) {
            //로그인 안되어 있으면 실행되는 조건문
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        fragment_post = new Fragment_Post();


        // xml 파일 연결
        View root = inflater.inflate(R.layout.fragment_05, container, false);

        // 파이어베이스 DB 초기화, 로그인 유저 불러오기
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // xml 파일에 있는 텍스트, 버튼 찾기
        TextView nickname = root.findViewById(R.id.nickname); // 닉네임
        ImageView userimage = root.findViewById(R.id.userImage);

        Button PostBtn = root.findViewById(R.id.button4);
        Button adminBtn = root.findViewById(R.id.button2);
        Button logoutBtn = root.findViewById(R.id.logoutButton);
        TextView hashText = root.findViewById(R.id.textView8);

        // DB에서 user 컬렉션 선택 후 로그인된 아이디에 맞는 정보 가져오기
        DocumentReference docRef = db.collection("users").document(user.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() { // 불러오기 성공시
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) { // 불러오기 성공시
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) { // 정보가 존재할 시
                        userid = document.getId();
                        nickname.setText(document.get("name").toString()); // 닉네임 업데이트
                        //아이디가 관리자일 경우 버튼생성
                        //무슨 버튼해야 하는거지? 왜 커밋안되지? 휴
                        if (nickname.getText().toString().equals("관리자")){
                            Button btn2=root.findViewById(R.id.button2);
                            btn2.setVisibility(View.VISIBLE);
                        }
                        if (document.get("photoUrl") != null) { // photoUrl 필드가 있는지 확인(필드확인: null 타입)
                            String photoUrl = document.get("photoUrl").toString(); // photoUrl 가져오기
                            if (photoUrl != "null") { // photoUrl의 값이 존재할 경우
                                Glide.with(root).load(photoUrl).into(userimage);
                            }
                        }
                    } else { // 정보가 존재하지 않을 시
                    }
                } else { // 불러오기 실패시
                }
            }
        });

        // 여기부터 해시태그 정보 가져오기
        CollectionReference productRef = db.collection("posts");
        //get()을 통해서 해당 컬렉션의 정보를 가져온다.
        productRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                //작업이 성공적으로 마쳤을때
                if (task.isSuccessful()) {
                    //document.getData() or document.getId() 등등 여러 방법으로
                    //데이터를 가져올 수 있다.
                    hash = "";
                    //컬렉션 아래에 있는 모든 정보를 가져온다.
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("yyyyyyyyyyyy", document.getData().get("id").toString() + " => " + userid);
                        if(document.getData().get("id").toString().equals(userid)) {
                            hash = hash + " " + document.getData().get("hashtag").toString();
                            Log.d("yyyyyyyyyyyy", hash);
                        }
                    }
                    hashText.setText(hash);
                    //그렇지 않을때
                } else {

                }
            }
        });

        PostBtn.setOnClickListener(this);
        adminBtn.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);
        return root;

    }


    // 버튼 클릭시 실행되는 함수
    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.button4){
            Intent intent = new Intent(this.getContext(), Fragment_Post.class);
            startActivity(intent);
            //getParentFragmentManager().beginTransaction().replace(R.id.frameLayout,fragment_post).commit();
        }
        else if(view.getId() == R.id.button2){
            Intent intent = new Intent(this.getContext(), AdminActivity.class);
            startActivity(intent);
            //getParentFragmentManager().beginTransaction().replace(R.id.frameLayout,fragment_post).commit();
        }
        else if(view.getId() == R.id.logoutButton){
            FirebaseAuth.getInstance().signOut();   // 파이어베이스에 로그아웃 신호 보내줌 _ 대규
            myStartActivity(LoginActivity.class);   // 로그인 액티비티로 이동 _ 대규
            startToast("로그아웃");
            ActivityCompat.finishAffinity(getActivity());
        }
        /*
        else if(view.getId() == R.id.button3){
            Log.d("Button3","work");
        }
         */
    }
    private void myStartActivity(Class c) { // 액티비티 이동하는 함수
        Intent intent = new Intent(getActivity(), c);
        startActivity(intent);
    }
    private void startToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

 }


