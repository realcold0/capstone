package com.akj.sns_project.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.akj.sns_project.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends BasicActivity implements GoogleApiClient.OnConnectionFailedListener {
    private SignInButton googleLoginButton;
    private FirebaseAuth mAuth;
    private GoogleApiClient googleApiClient;    // 구글 API 클라이언트 객체
    private static final int REQ_SIGN_GOOGLE = 100; // 구글 로그인 결과 코드
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {    // 로그인 액티비티가 실행되었을 때
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance(); //파이어베이스 인증 받아오는 것을 mAuth로 표현

        // 로그인xml에 클릭 listner를 달아주는 코드들
        findViewById(R.id.checkButton).setOnClickListener(onClickListener);
        findViewById(R.id.gotoPasswordResetButton).setOnClickListener(onClickListener);
        findViewById(R.id.googleLoginButton).setOnClickListener(onClickListener);
        findViewById(R.id.gotoSignUpButton).setOnClickListener(onClickListener);


        //구글 로그인 여기서부터
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

        auth = FirebaseAuth.getInstance();  // 파이어베이스 인증 객체 초기화

        googleLoginButton = findViewById(R.id.googleLoginButton);
        googleLoginButton.setOnClickListener(new View.OnClickListener() {  // 구글 로그인 버튼을 클릭했을 때 여기서 수행
            @Override
            public void onClick(View view) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);  // 구글에서 제공하는 액티비티 연결
                startActivityForResult(intent, REQ_SIGN_GOOGLE);        // 구글에서 제공하는 액티비티에서 제공하는 결과값을 받아옴

            }
        }); // 구글 로그인 마무리

    }

    // 클릭 이벤트 정리
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.checkButton:
                    login();
                    break;
                case R.id.gotoPasswordResetButton:
                    myStartActivity(PasswordResetActivity.class);
                    break;
                case R.id.gotoSignUpButton:
                    myStartActivity(SignUpActivity.class);
                    break;

            }
        }
    };

    // 로그인 함수
    private void login() {
        String email = ((EditText) findViewById(R.id.emailEditText)).getText().toString();      //텍스트 박스에 입력된 값을 email로 _ 대규
        String password = ((EditText) findViewById(R.id.passwordEditText)).getText().toString();    //텍스트 박스에 입력된 값을 Password로 _ 대규

        if (email.length() > 0 && password.length() > 0) {  // 텍스트 박스에 입력된 길이가 0 이상일때
            RelativeLayout loaderLayout = findViewById(R.id.loaderLayout);  // 실행될때 까지 로딩창 띄어줌
            loaderLayout.setVisibility(View.VISIBLE);                       // 여기까지가 로딩창 띄어주는것 _ 대규
            mAuth.signInWithEmailAndPassword(email, password)       // 로그인 과정 _ 대규
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            loaderLayout.setVisibility(View.GONE);  // 실행 완료되고 나서 로딩창 닫아줌 _ 대규
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();     // 로그인한 유저 정보 업데이트
                                startToast("로그인에 성공하였습니다");
                                myStartActivity(BoardActivity.class);    // 로그인 성공 후 메인 액티비티로
                                finish();                       // 로그인 후 메인화면에서 뒤로가기 버튼 누를 시 그대로 앱이 종료되게함
                            } else {
                                if (task.getException() != null) {
                                    loaderLayout.setVisibility(View.GONE);  // 실행 완료되고 나서 로딩창 닫아줌 _ 대규
                                    startToast("아이디 또는 비밀번호를 확인해주세요"); // 실패했을때 메세지
                                    // 실패했을때 UI
                                }
                            }
                        }
                    });
        } else {
            RelativeLayout loaderLayout = findViewById(R.id.loaderLayout);      // 로딩 창 _대규
            loaderLayout.setVisibility(View.GONE);                              // 로딩 창 지우기 _ 대규
            startToast("이메일 또는 비밀번호를 입력해주세요");
        }
    }


    // 구글 로그인 파트
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {   // 구글 로그인 인증을 요청 했을 때 결과 값을 되돌려 받는 곳
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQ_SIGN_GOOGLE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess())
            {
                GoogleSignInAccount account = result.getSignInAccount();    // account라는 데이터는 구글 로그인 정보를 담고있다. ( 닉네임, 프로필사진, 이메일주소...등)
                resultLogin(account);   // 로그인 결과 값 출력 수행하라는 메소드
            }
        }
    }

    private void resultLogin(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {    // 로그인이 성공했으면
                            Toast.makeText(LoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MypageActivity.class);
                            intent.putExtra("nickName", account.getDisplayName());
                            intent.putExtra("photoUrl",String.valueOf(account.getPhotoUrl()));  // String.valueOf() 특정 자료형을 String 형태로 변환할때
                            startActivity(intent);
                        }else{  // 로그인에 실패했으면
                            Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    // 구글 로그인 파트 마무리    이 파트는 firebase에서 제공하는 구글 로그인 파트 _ 대규

   private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

   private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);        // 로그인 후 메인화면에서 뒤로가기 버튼 누를 시 그대로 앱이 종료되게함 _ 대규
        startActivity(intent);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
