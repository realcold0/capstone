package com.akj.sns_project.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.akj.sns_project.R;
import com.akj.sns_project.PostInfo;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

public class WritePostActivity extends BasicActivity {  //   글쓰기 액티비티 _ 대규
private static final String TAG = "WritePostActivity";
private FirebaseUser user;
private ArrayList<String> pathList = new ArrayList<>();
private LinearLayout parent;
private RelativeLayout buttonsBackgroundLayout;
private ImageView selectedImageView;
private EditText selectedEditText;
private RelativeLayout loaderLayout;
private int pathCount, successCount;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_write_post);

    parent = findViewById(R.id.contentsLayout);
    buttonsBackgroundLayout = findViewById(R.id.buttonsBackgroundLayout);
    loaderLayout = findViewById(R.id.loaderLayout);

    buttonsBackgroundLayout.setOnClickListener(onClickListener);
    findViewById(R.id.checkWrite).setOnClickListener(onClickListener);
    findViewById(R.id.image).setOnClickListener(onClickListener);
    findViewById(R.id.video).setOnClickListener(onClickListener);
    findViewById(R.id.imageModify).setOnClickListener(onClickListener);
    findViewById(R.id.picture).setOnClickListener(onClickListener);
    findViewById(R.id.gallery).setOnClickListener(onClickListener);
    findViewById(R.id.contentsEditText).setOnFocusChangeListener(onFocusChangeListener);
    findViewById(R.id.titleEditText).setOnFocusChangeListener(new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if(hasFocus){
                selectedEditText = null;
            }
        }
    });
}

@Override
public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    switch (requestCode) {
        case 0:
            long start = System.currentTimeMillis();

            if (resultCode == Activity.RESULT_OK) {
                String profilePath = data.getStringExtra("profilePath");
                pathList.add(profilePath);

                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                // 글 내용 작성하면 글 내용 작성칸이 세로로 길어짐
                LinearLayout linearLayout = new LinearLayout(WritePostActivity.this);
                linearLayout.setLayoutParams(layoutParams);
                linearLayout.setOrientation(LinearLayout.VERTICAL);

                if(selectedEditText == null){
                    parent.addView(linearLayout);
                }else{
                    for(int i = 0; i < parent.getChildCount(); i++){
                        if(parent.getChildAt(i) == selectedEditText.getParent()){
                            parent.addView(linearLayout, i + 1);
                            break;
                        }
                    }
                }

                // 게시글에 사진 추가하는 방법
                ImageView imageView = new ImageView(WritePostActivity.this);
                imageView.setLayoutParams(layoutParams);
                imageView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        buttonsBackgroundLayout.setVisibility(View.VISIBLE);
                        selectedImageView = (ImageView) view;
                    }
                });
                Glide.with(this).load(profilePath).override(1000).into(imageView);
                linearLayout.addView(imageView); // 사진을 하나 추가하고 나면 imageView를 더 추가해줌

                // 글을 쓰고나면 텍스트 박스가 여러줄 작성이 가능하게 됨
                EditText editText = new EditText(WritePostActivity.this);
                editText.setLayoutParams(layoutParams);
                editText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_CLASS_TEXT);
                editText.setHint("내용");
                editText.setOnFocusChangeListener(onFocusChangeListener);
                linearLayout.addView(editText);

                long end = System.currentTimeMillis();
                Log.w("WritePostActivity", "게시글 파일 불러오는 속도 " + ( end - start )/1000.0);
            }
            break;
        case 1:
            if(resultCode == Activity.RESULT_OK){
                String profilePath = data.getStringExtra("profilePath");
                Glide.with(this).load(profilePath).override(1000).into(selectedImageView);
            }
        break;
    }
}

View.OnClickListener onClickListener = new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.checkWrite:
                storageUpload();
                break;
            case R.id.image:
                myStartActivity(GalleryActivity.class, "image",0);
                break;

            case R.id.video:
                myStartActivity(GalleryActivity.class, "video",0);
                break;

            case R.id.buttonsBackgroundLayout:
                if(buttonsBackgroundLayout.getVisibility() == View.VISIBLE){
                    buttonsBackgroundLayout.setVisibility(View.GONE);
                }
                break;

            case R.id.imageModify:
                myStartActivity(GalleryActivity.class, "image", 1);
                buttonsBackgroundLayout.setVisibility(View.GONE);
                break;

            case R.id.picture:
                myStartActivity(GalleryActivity.class, "video", 1);
                buttonsBackgroundLayout.setVisibility(View.GONE);
                break;

            case R.id.gallery:
                parent.removeView((View) selectedImageView.getParent());
                buttonsBackgroundLayout.setVisibility(View.GONE);
                break;
        }
    }
};

    View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(hasFocus){
                selectedEditText = (EditText) v;
            }
        }
    };

    // 게시글 업로드 기능
    private void storageUpload() {
        long start = System.currentTimeMillis();

        final String title = ((EditText) findViewById(R.id.titleEditText)).getText().toString();

        if (title.length() > 0) {
            loaderLayout.setVisibility(View.VISIBLE); // 로딩창 보여줌

            final ArrayList<String> contentsList = new ArrayList<>();

            // 파이어베이스에서 유저 정보를 가져옴
            user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            String id = getIntent().getStringExtra("id");
            DocumentReference dr;
            if(id == null){
                dr = firebaseFirestore.collection("posts").document();
            }else{
                dr = firebaseFirestore.collection("posts").document(id);
            }
            final DocumentReference documentReference = dr;


            for(int i = 0; i < parent.getChildCount(); i++){
                LinearLayout linearLayout = (LinearLayout)parent.getChildAt(i);
                for(int ii = 0; ii < linearLayout.getChildCount(); ii++){
                    View view = linearLayout.getChildAt(ii);
                    if(view instanceof EditText){
                        String text = ((EditText)view).getText().toString();
                        if(text.length() > 0){
                            contentsList.add(text);
                        }
                    } else {
                        contentsList.add(pathList.get(pathCount));
                        String[] pathArray = pathList.get(pathCount).split("\\.");
                        final StorageReference mountainImagesRef = storageRef.child("posts/" + documentReference.getId() + "/"+pathCount+"."+pathArray[pathArray.length - 1]);
                        try {
                            InputStream stream = new FileInputStream(new File(pathList.get(pathCount)));
                            StorageMetadata metadata = new StorageMetadata.Builder().setCustomMetadata("index",""+(contentsList.size()-1)).build();
                            UploadTask uploadTask = mountainImagesRef.putStream(stream, metadata);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle unsuccessful uploads
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    final int index = Integer.parseInt(taskSnapshot.getMetadata().getCustomMetadata("index"));
                                    mountainImagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            contentsList.set(index, uri.toString());
                                            successCount++;
                                            if(pathList.size() == successCount){

                                                PostInfo postInfo = new PostInfo(title, contentsList, user.getUid(), new Date());
                                                storeUpload(documentReference, postInfo);
                                                for(int a = 0; a < contentsList.size(); a++){
                                                    Log.e("로그: ","콘덴츠: "+contentsList.get(a));
                                                }
                                            }
                                        }
                                    });
                                }
                            });
                        } catch (FileNotFoundException e) {
                            Log.e("로그", "에러: " + e.toString());
                        }
                        pathCount++;
                    }
                }
            }
            if(pathList.size() == 0){   // 사진없이 글만 올리는 경우
                PostInfo postInfo = new PostInfo(title, contentsList, user.getUid(), new Date());
                storeUpload(documentReference, postInfo);
            }
        } else {
            startToast("제목을 입력해주세요.");
        }

        long end = System.currentTimeMillis();
        Log.w("WritePostActivity", "게시글 업로드 속도 " + ( end - start )/1000.0);
    }

// 파이어베이스에서 제공하는 게시글 업로드 함수 코드
private void storeUpload(DocumentReference documentReference, PostInfo postInfo){
    documentReference.set(postInfo)
        .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot successfully written!");
                loaderLayout.setVisibility(View.GONE);
                finish();
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loaderLayout.setVisibility(View.GONE);
                Log.w(TAG, "Error writing document", e);
            }
        });
}


private void myStartActivity(Class c, String media, int requestCode){
    Intent intent = new Intent(this,c);
    intent.putExtra("media",media);
    startActivityForResult(intent, requestCode);
}

private void startToast(String msg){
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
}
}