package com.akj.sns_project;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.akj.sns_project.activity.BoardActivity;
import com.akj.sns_project.activity.LoginActivity;
import com.akj.sns_project.activity.MemberInitActivity;
import com.akj.sns_project.activity.WritePostActivity;
import com.akj.sns_project.adapter.MainAdapter;
import com.akj.sns_project.listener.OnPostListener;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;

// 준범 작성
// Fragment01.java 에서 메인화면에 최신영화 보여주는 기능 제외하고 가져와서 붙여넣음
public class Fragment03 extends Fragment {

    private View view;

    private BottomNavigationView bottomNavigationView;
    private BlackBoardFragment black;
    private WhiteBoardFragment white;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_03, container, false);

        black = new BlackBoardFragment();
        white = new WhiteBoardFragment();

        bottomNavigationView = view.findViewById(R.id.navigationView);

        //처음화면
        getParentFragmentManager().beginTransaction().add(R.id.frameLayout, white).commit(); //FrameLayout에 fragment.xml 띄우기

        //상단 네비게이션뷰 안의 아이템 설정
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    //item을 클릭시 id값을 가져와 FrameLayout에 fragment.xml띄우기
                    case R.id.item_white_fragment:
                        getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, white).commit();
                        break;

                    case R.id.item_black_fragment:
                        getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, black).commit();
                        break;

                }
                return true;
            }
        });

        return view;
    }
}