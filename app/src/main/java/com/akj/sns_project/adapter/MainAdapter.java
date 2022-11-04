package com.akj.sns_project.adapter;

import android.app.Activity;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.akj.sns_project.PostInfo;
import com.akj.sns_project.R;
import com.akj.sns_project.listener.OnPostListener;
import com.bumptech.glide.Glide;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {
    private ArrayList<PostInfo> mDataset;
    private Activity activity;
    private FirebaseFirestore firebaseFirestore;
    private OnPostListener onPostListener;

    //RecyclerView와 cardView를 이용하여 게시글들을 보며줄 것으로 선언
    static class MainViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;

        MainViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    public MainAdapter(Activity activity, ArrayList<PostInfo> myDataset) { // 배열로 들어온 데이터들을 불러오는 작업
        mDataset = myDataset;
        this.activity = activity;
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    // 게시물 업데이트해주는 listner를 초기화해줌
    public void setOnPostListener(OnPostListener onPostListener){ // 인터페이스 초기화해줌
        this.onPostListener = onPostListener;
    }

    @Override //viewType이 계속 0만 주기 때문에 사용하려면 override해야함.
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override//RecyclerView와 cardView를 만들어주는 작업. 보이는 부분만 load함.
    public MainAdapter.MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //layout을 view객체로 만들기 위해 layoutInflater를 이용한다.
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        final MainViewHolder mainViewHolder = new MainViewHolder(cardView);
        cardView.setOnClickListener(new View.OnClickListener() { //하나의 카드뷰를 클릭 시 intent로 해당하는 값을 BoardActivity로 넘겨줌
            @Override
            public void onClick(View v) {

            }
        });

        cardView.findViewById(R.id.menu).setOnClickListener(new View.OnClickListener() {    // cardview의 menu버튼
            @Override
            public void onClick(View view) {
                showPopup(view, mainViewHolder.getAdapterPosition());    // 설정창 나오게 하는거
            }
        });

        return mainViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MainViewHolder holder, int position) {
        //item_post에 실제 db들의 값들을 넣어주는 작업

        //CardView에 title값 넣어주기
        CardView cardView = holder.cardView;
        TextView titleTextView = cardView.findViewById(R.id.titleTextView);
        titleTextView.setText(mDataset.get(position).getTitle());

        // 게시물을 추가한 날짜 넣어주기
        TextView createdAtTextView = cardView.findViewById(R.id.createAtTextView);
        createdAtTextView.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(mDataset.get(position).getCreatedAt()));

        // contentsLayout에 contents(게시글 내용)값 넣어주기
        LinearLayout contentsLayout = cardView.findViewById(R.id.contentsLayout);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ArrayList<String> contentsList = mDataset.get(position).getContents();

        // 게시글 넣을때 게시글 안에 사진이나 동영상 있는지
        if(contentsLayout.getTag() == null || !contentsLayout.getTag().equals(contentsList)){
            contentsLayout.setTag(contentsList);
            contentsLayout.removeAllViews();
            if(contentsList.size() > 0){
                for (int i = 0; i < contentsList.size(); i++) {
                    String contents = contentsList.get(i);
                    if (Patterns.WEB_URL.matcher(contents).matches()) {   // 글 내용에 사진이나 동영상이 있을 경우
                        ImageView imageView = new ImageView(activity);
                        imageView.setLayoutParams(layoutParams);
                        imageView.setAdjustViewBounds(true);
                        imageView.setScaleType(ImageView.ScaleType.FIT_XY); // 게시글 사진이 꽉차게 나오게끔
                        contentsLayout.addView(imageView);
                        Glide.with(activity).load(contents).override(1000).thumbnail(0.1f).into(imageView);
                    } else {          // 내용에 글만 있을 경우
                        TextView textView = new TextView(activity);
                        textView.setLayoutParams(layoutParams);
                        textView.setText(contents);
                        contentsLayout.addView(textView);
                    }
                }
            }
        }

    }

    @Override //자동 override됨. 데이터들의 수를 세줌.
    public int getItemCount() {
        return mDataset.size();
    }

    //popup메뉴를 만들기 위한 메서드. view로 받아오기 위해 activity사용함. 여기서 popup메뉴는 수정 삭제가 내려오는 메뉴임.
    private void showPopup(View v, final int position) {
        //db값을 갖고오고, 선택된 post값을 알아오기 위해 사용함. view와 위치값(position)을 갖고와서 사용하기. 하나의 postID를 알아야함.
        PopupMenu popup = new PopupMenu(activity, v);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override //popup메뉴 내의 삭제버튼, 수정버튼을 눌렀을 때 삭제,수정기능 구현
            public boolean onMenuItemClick(MenuItem menuItem) {
                String id = mDataset.get(position).getId();
                switch (menuItem.getItemId()) {
                    case R.id.modify: //modify버튼을 눌렀을 때
                        onPostListener.onModify(id); //인터페이스의 onModify를 이용
                        return true;
                    case R.id.delete: //delete버튼을 눌렀을 때
                        onPostListener.onDelete(id); //인터페이스의 onDelete를 이용
                        return true;
                    default:
                        return false;
                }
            }
        });

        MenuInflater inflater = popup.getMenuInflater(); //inflater를 이용하여 view화 시킴
        inflater.inflate(R.menu.post, popup.getMenu()); //popup메뉴를 보여줌.
        popup.show();
    }
}