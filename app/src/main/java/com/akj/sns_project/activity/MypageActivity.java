package com.akj.sns_project.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.akj.sns_project.R;
import com.bumptech.glide.Glide;

public class MypageActivity extends AppCompatActivity { // 구글 아이디로 로그인시 넘어가는 페이지 어떻게 활용할지 고민중_ 대규

    private TextView tv_result;
    private ImageView iv_profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);


        Intent intent = getIntent();
        String nickName = intent.getStringExtra("nickName");    // MainActivity로부터 닉네임 전달받음
        String photoUrl = intent.getStringExtra("photoUrl");    // MainActivity로부터 프로필 Url 전달받음

        tv_result = findViewById(R.id.tv_result);
        tv_result.setText(nickName);        // 닉네임 textview에 세팅


        iv_profile = findViewById(R.id.iv_profile);
        Glide.with(this).load(photoUrl).into(iv_profile);   // 프로필 url을 이미지뷰에 세팅
    }
}