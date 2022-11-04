package com.akj.sns_project.activity;

import androidx.annotation.NonNull;
import android.os.Bundle;
import android.view.MenuItem;
import com.akj.sns_project.Fragment01;
import com.akj.sns_project.Fragment02;
import com.akj.sns_project.Fragment03;
import com.akj.sns_project.Fragment04;
import com.akj.sns_project.Fragment05;
import com.akj.sns_project.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;



public class MainActivity extends BasicActivity {   //fragment 코드들
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

    }

}


