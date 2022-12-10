package com.akj.sns_project;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akj.sns_project.activity.LoginActivity;
import com.akj.sns_project.activity.WritePostActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

// 준범 작성
// Fragment01.java 에서 메인화면에 최신영화 보여주는 기능 제외하고 가져와서 붙여넣음
public class Fragment03 extends Fragment {

    private View view;

    private BottomNavigationView whiteBlackNavigationView;
    private BlackBoardFragment black;
    private WhiteBoardFragment white;

    //타이머 변수 선언
    private TextView countdownText;

    private CountDownTimer countDownTimer;

    private boolean timerRunning;
    private boolean isZero;
    private boolean firstState;

    private long time = 0;
    private long tempTime = 0;

    FrameLayout timer;
    //

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_03, container, false);

        countdownText = view.findViewById(R.id.countdown_text);

        timer = view.findViewById(R.id.timer);

        firstState = true;
        timerRunning = false;
        isZero = false;

        black = new BlackBoardFragment();
        white = new WhiteBoardFragment();

        whiteBlackNavigationView = view.findViewById(R.id.navigationView);

        //처음화면
        getParentFragmentManager().beginTransaction().add(R.id.frameLayout, white).commit(); //FrameLayout에 fragment.xml 띄우기

        timer.setVisibility(timer.GONE);

        //상단 네비게이션뷰 안의 아이템 설정
        whiteBlackNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    //item을 클릭시 id값을 가져와 FrameLayout에 fragment.xml띄우기
                    case R.id.item_white_fragment:

                        getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, white).commit();
                        // 타이머 일시정지
                        if(timer.getVisibility() == timer.VISIBLE) {
                            timer.setVisibility(timer.GONE);
                            stopTimer();
                        }
                        break;

                    case R.id.item_black_fragment:
                        if(isZero){
                            Toast.makeText(getActivity(), "남은 시간이 없습니다.", Toast.LENGTH_SHORT).show();
                            whiteBlackNavigationView.setSelectedItemId(R.id.item_white_fragment);
                            getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, white).commit();
                        }

                        getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, black).commit();
                        // 타이머 시작
                        timer.setVisibility(timer.VISIBLE);
                        startTimer();
                        break;
                }
                return true;
            }
        });

        return view;
    }

    // 이 아래로 타이머 관련 코드
    // 타이머 시작
    private void startTimer(){
        if(firstState){
            // 여기서 시간 세팅하세요.
            String sHour = new String("1");
            String sMin = new String("0");
            String sSec = new String("0");

            time = (Long.parseLong(sHour)*3600000)+(Long.parseLong(sMin)*60000)+(Long.parseLong(sSec)*1000)+1000;
        }
        else{
            time = tempTime;
        }

        countDownTimer = new CountDownTimer(time, 1000) {
            @Override
            public void onTick(long millsUntilFinished) {
                tempTime = millsUntilFinished;
                updateTimer();
            }

            @Override
            public void onFinish() {  }
        }.start();
        
        timerRunning = true;
        firstState = false;
    }

    // 타이머 멈춤
    public void stopTimer(){
        countDownTimer.cancel();
        timerRunning = false;
    }

    // 시간 업데이트
    private void updateTimer(){
        int hour = (int) tempTime / 3600000;
        int minutes = (int) tempTime % 3600000 / 60000;
        int seconds = (int) tempTime % 3600000 % 60000 / 1000;
        
        if(hour == 0 && minutes == 0 && seconds == 0){
            isZero = true;
            Toast.makeText(getActivity(), "남은 시간이 없습니다.", Toast.LENGTH_SHORT).show();
            whiteBlackNavigationView.setSelectedItemId(R.id.item_white_fragment);
            getParentFragmentManager().beginTransaction().replace(R.id.frameLayout, white).commit();

        }

        String timeLeftText = "";

        timeLeftText = ""+hour+":";

        if(minutes < 10) timeLeftText += "0";
        timeLeftText += minutes + ":";

        if(seconds < 10) timeLeftText += "0";
        timeLeftText += seconds;

        countdownText.setText(timeLeftText);
    }
    

}