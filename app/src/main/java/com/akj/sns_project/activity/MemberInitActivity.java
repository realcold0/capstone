package com.akj.sns_project.activity;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.akj.sns_project.MemberInfo;
import com.akj.sns_project.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class MemberInitActivity extends BasicActivity { // 멤버 회원정보 저장해주는 액티비티
    private static final String TAG = "MemberInitActivity";
    private ImageView profileImageVIew;
    private RelativeLayout loaderLayout;
    private String profilePath;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {    // clickListner들 관리
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_init);

        loaderLayout = findViewById(R.id.loaderLayout);
        profileImageVIew = findViewById(R.id.profileImageView);
        profileImageVIew.setOnClickListener(onClickListener);

        findViewById(R.id.checkButton).setOnClickListener(onClickListener);
        findViewById(R.id.gallery).setOnClickListener(onClickListener);
        findViewById(R.id.picture).setOnClickListener(onClickListener);
    }

    @Override public void onBackPressed(){  // 뒤로가기 버튼 시 메인액티비티로
        super.onBackPressed();
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {// 사진을 프로필 사진 imageview에 넣어주는 기능 _ 대규
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0: {
                if (resultCode == Activity.RESULT_OK) {
                    profilePath = data.getStringExtra("profilePath");
                    Glide.with(this).load(profilePath).centerCrop().override(500).into(profileImageVIew);
                }
                break;
            }
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.checkButton:
                    storageUploader();  // 파이어베이스에 회원정보 업데이트 해줌_ 대규
                    break;
                case R.id.profileImageView:
                    CardView cardview = findViewById(R.id.buttonsCardview); // 이미지 뷰 눌렀을때 밑에 촬영버튼, 갤러리버튼 있는지
                    if(cardview.getVisibility() == View.VISIBLE){   // 있으면 삭제
                        cardview.setVisibility(View.GONE);
                    }
                    else{
                        cardview.setVisibility(View.VISIBLE);   // 없으면 보여줌
                    }
                    break;
                case R.id.picture:
                    myStartActivity(CameraActivity.class);  // 촬영버튼 클릭시 카메라 실행
                    break;
                case R.id.gallery:
                    myStartActivity(GalleryActivity.class, "image");    // 갤러리에서 이미지만 보여주는 갤러리 실행
                    break;
            }
        }
    };


    private void storageUploader() {    // 파이어베이스에 정보 업로드하는 함수
        // 입력받는 회원 정보들
        final String name = ((EditText) findViewById(R.id.nameEditText)).getText().toString();
        final String phoneNumber = ((EditText) findViewById(R.id.phoneNumberEditText)).getText().toString();
        final String birthDay = ((EditText) findViewById(R.id.birthDayEditText)).getText().toString();
        final String address = ((EditText) findViewById(R.id.addressEditText)).getText().toString();

        if (name.length() > 0 && phoneNumber.length() > 9 && birthDay.length() > 5 && address.length() > 0) {   // 입력조건들 충족 시 파이어베이스 입력 _ 대규
            loaderLayout.setVisibility(View.VISIBLE);   // 회원정보 업로드가 완료될때까지 로딩창 보여줌
            FirebaseStorage storage = FirebaseStorage.getInstance();    // 파이어베이스 저장소 정보 받아옴
            StorageReference storageRef = storage.getReference();
            user = FirebaseAuth.getInstance().getCurrentUser(); // 파이어베이스 인증을 통해서 유저정보 받아옴
            final StorageReference mountainImagesRef = storageRef.child("users/" + user.getUid() + "/profileImage.jpg");

            if(profilePath == null){    // 프로필 사진없이 입력시
                MemberInfo memberInfo = new MemberInfo(name, phoneNumber, birthDay, address);
                storeUploader(memberInfo);
            }else{
                try {
                    // 프로필 사진 있을 경우 파이어베이스 올리는 방식 _ 대규
                    InputStream stream = new FileInputStream(new File(profilePath));
                    UploadTask uploadTask = mountainImagesRef.putStream(stream);
                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return mountainImagesRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {    // 파이어베이스에 정보 추가
                       @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                // memberinfo에 해당하는 정보들을 입력받고 등록한다
                                MemberInfo memberInfo = new MemberInfo(name, phoneNumber, birthDay, address, downloadUri.toString());
                                storeUploader(memberInfo);
                            } else {
                                startToast("회원정보 전송에 실패했습니다");
                            }
                        }
                    });
                } catch (FileNotFoundException e) {
                    Log.e("로그", "에러: " + e.toString());
                }
            }
        } else {
            startToast("회원정보를 입력해주세요.");
        }
    }

    //파이어베이스 파이어스토어에 users 폴더에 로그인된 UID정보에 유저정보 업로드하는 함수
    private void storeUploader(MemberInfo memberInfo){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(user.getUid()).set(memberInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        startToast("회원정보 등록을 성공하였습니다.");
                        loaderLayout.setVisibility(View.GONE);  // 회원정보 등록에 성공하면 로딩창을 끔
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        startToast("회원정보 등록에 실패하였습니다.");
                        loaderLayout.setVisibility(View.GONE);  // 회원정보 등록에 실패해도 로딩창을 끔
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    private void myStartActivity(Class c){
        Intent intent = new Intent(this,c);
        startActivityForResult(intent, 0);
    }

    private void myStartActivity(Class c, String media ){
        Intent intent = new Intent(this,c);
        intent.putExtra("media",media);
        startActivityForResult(intent, 0);
    }

    private void startToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}
