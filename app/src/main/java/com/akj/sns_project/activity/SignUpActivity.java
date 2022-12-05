package com.akj.sns_project.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.akj.sns_project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends BasicActivity { // 회원가입 액티비티
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // clickListener들
        findViewById(R.id.checkButton).setOnClickListener(onClickListener);
        findViewById(R.id.gotoLoginButton).setOnClickListener(onClickListener);
    }

    @Override public void onBackPressed() {     // 뒤로가기 버튼을 눌렀을 때 종료
        super.onBackPressed();
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.checkButton:      // 회원가입 하는 버튼 _ 대규
                    signUp();
                    break;

                case R.id.gotoLoginButton:      // 로그인하러 이동하기 _ 대규
                    myStartActivity(LoginActivity.class);
                    break;
            }
        }
    };

    private void signUp(){  // 회원가입 함수
        // 텍스트박스안의 글자들
        String email = ((EditText)findViewById(R.id.emailEditText)).getText().toString();
        String password = ((EditText)findViewById(R.id.passwordEditText)).getText().toString();
        String passwordCheck = ((EditText)findViewById(R.id.passwordCheckEditText)).getText().toString();

        // 텍스트박스안의 글자수들 조건에 따라서 실행
        if(email.length() > 0 && password.length() > 0 && passwordCheck.length() > 0){  // 아이디 비밀번호 입력받고 조건 확인 후 진행
            if(password.equals(passwordCheck)){ // 패스워드 텍스트박스와 패스워드 확인 텍스트 박스 값이 같을 경우
                final RelativeLayout loaderLayout = findViewById(R.id.loaderLayout);
                loaderLayout.setVisibility(View.VISIBLE);

                mAuth.createUserWithEmailAndPassword(email, password)   // 파이어베이스에서 제공하는 로그인 코드
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    loaderLayout.setVisibility(View.GONE);
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    startToast("회원가입에 성공하였습니다");
                                    myStartActivity(LoginActivity.class);
                                    // 성공했을때 UI
                                } else {
                                    if (task.getException() != null) {
                                        loaderLayout.setVisibility(View.GONE);
                                        startToast(task.getException().toString());
                                        // 실패했을때 UI
                                    }
                                }
                            }
                        });
            }else{
                startToast("비밀번호와 비밀번호 확인이 일치하지 않습니다");
            }
        }else{
            startToast("이메일 또는 비밀번호를 입력해주세요");
        }
    }

    private void startToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void myStartActivity(Class c){
        Intent intent = new Intent(this,c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);        // 로그인 후 메인화면에서 뒤로가기 버튼 누를 시 그대로 앱이 종료되게함
        startActivity(intent);
    }
}
