
package com.akj.sns_project.adapter;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.akj.sns_project.PostInfo;
import com.akj.sns_project.R;
import com.akj.sns_project.ReplyInfo;
import com.akj.sns_project.activity.PostActivity;
import com.akj.sns_project.listener.OnPostListener;
import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.MainViewHolder> {
    private ArrayList<ReplyInfo> mDataset;
    private Activity activity;
    private OnPostListener onPostListener;

    //RecyclerView와 cardView를 이용하여 게시글들을 보며줄 것으로 선언
    static class MainViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        MainViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    public ReplyAdapter(Activity activity, ArrayList<ReplyInfo> myDataset) { // 배열로 들어온 데이터들을 불러오는 작업
        mDataset = myDataset;
        this.activity = activity;
    }

    @Override //viewType이 계속 0만 주기 때문에 사용하려면 override해야함.
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override//RecyclerView와 cardView를 만들어주는 작업. 보이는 부분만 load함.
    public ReplyAdapter.MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //layout을 view객체로 만들기 위해 layoutInflater를 이용한다.
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        final ReplyAdapter.MainViewHolder mainViewHolder = new ReplyAdapter.MainViewHolder(cardView);
        cardView.setOnClickListener(new View.OnClickListener() { //하나의 카드뷰를 클릭 시 intent로 해당하는 값을 BoardActivity로 넘겨줌
            @Override
            public void onClick(View v) {

            }
        });

        return mainViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ReplyAdapter.MainViewHolder holder, int position) {
        //item_post에 실제 db들의 값들을 넣어주는 작업
        //CardView에 title값 넣어주기
        CardView cardView = holder.cardView;

        // 게시물을 추가한 날짜 넣어주기
        TextView createdAtTextView = cardView.findViewById(R.id.createAtTextView);
        createdAtTextView.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(mDataset.get(position).getCreatedAt()));

        // contentsLayout에 contents(게시글 내용)값 넣어주기
        TextView replyTextView = cardView.findViewById(R.id.commentTextView);
        LinearLayout contentsLayout = cardView.findViewById(R.id.contentsLayout);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        String contentsList = mDataset.get(position).getContents();
        replyTextView.setText(contentsList);

    }

    @Override //자동 override됨. 데이터들의 수를 세줌.
    public int getItemCount() {
        return mDataset.size();
    }

}

