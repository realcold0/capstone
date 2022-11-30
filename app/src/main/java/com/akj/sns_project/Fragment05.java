package com.akj.sns_project;

import androidx.annotation.NonNull;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Fragment05 extends Fragment implements View.OnClickListener {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance(); // 인스턴스 초기화
    private View view;
    private Fragment_Post fragment_post;
    private Fragment05 fragment05;

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

        // DB에서 user 컬렉션 선택 후 로그인된 아이디에 맞는 정보 가져오기
        DocumentReference docRef = db.collection("users").document(user.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() { // 불러오기 성공시
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) { // 불러오기 성공시
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) { // 정보가 존재할 시
                        nickname.setText(document.get("name").toString()); // 닉네임 업데이트
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

        PostBtn.setOnClickListener(this);
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
        /*
        else if(view.getId() == R.id.button3){
            Log.d("Button3","work");
        }
         */
    }

 }


