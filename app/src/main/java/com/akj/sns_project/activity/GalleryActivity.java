
package com.akj.sns_project.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akj.sns_project.R;
import com.akj.sns_project.activity.BasicActivity;
import com.akj.sns_project.adapter.GalleryAdapter;

import java.util.ArrayList;

public class GalleryActivity extends BasicActivity {        // 사진 올리기 or 프로필 사진 선택시 갤러리 열어주는 액티비티 _ 대규

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        if (ContextCompat.checkSelfPermission(GalleryActivity.this,     // 갤러리 접근 권한 허용얻는 코드 _ 대규
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(GalleryActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1); // request 코드를 1로 보내주고 onRequestPermissionsResult 여기로 가게됨 _ 대규
            if (ActivityCompat.shouldShowRequestPermissionRationale(GalleryActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {       // 갤러리 접근 권한 허용얻는 코드 _ 대규

            } else {
                startToast("권한을 허용해 주세요");
            }
        } else {    // 권한을 얻고 갤러리 사진들 불러오는기능 _ 대규
            recyclerInit();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {  // 권한이 있을 경우 갤러리 사진들 불러와줌 _ 대규
                    recyclerInit();
                } else {    // 권한 없을 시 다음 실행 _ 대규
                    finish();
                    startToast("권한을 허용해 주세요");
                }
            }
        }
    }

    private void recyclerInit(){
        final int numberOfColumns = 3;

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));    // 갤러리 3열로 쭉 보여줌 _ 대규

        RecyclerView.Adapter mAdapter = new GalleryAdapter(this, getImagesPath(this));
        recyclerView.setAdapter(mAdapter);
    }

    public ArrayList<String> getImagesPath(Activity activity) { // 사진 가져오는 부분 _ 대규
        Uri uri;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        Cursor cursor;
        int column_index_data;
        String PathOfImage = null;
        Intent intent = getIntent();
        String[] projection;

        if (intent.getStringExtra("media").equals("video")) { // 동영상만 보여주게한다 _ 대규
            uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            projection = new String[]{MediaStore.MediaColumns.DATA, MediaStore.Video.Media.BUCKET_DISPLAY_NAME};
        } else { // 이미지만 보여주게 한다 _ 대규
            uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            projection = new String[]{MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
        }

        cursor = activity.getContentResolver().query(uri, projection, null, null, null);    // 컨텐츠 불러올 수 있게하는 코드 구글 컨텐츠 제공자 참고 _ 대규
                //  MediaStore.Images.Media.BUCKET_DISPLAY_NAME + "DESC");//MediaStore.Images.ImageColumns.DATE_TAKEN + "DESC"); 정렬 예시
        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

        while (cursor.moveToNext()) {
            PathOfImage = cursor.getString(column_index_data);

            listOfAllImages.add(PathOfImage);
        }
        return listOfAllImages;
    }

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}