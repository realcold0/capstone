package com.akj.sns_project.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.provider.ContactsContract;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.akj.sns_project.PostInfo;
import com.akj.sns_project.R;
import com.akj.sns_project.activity.PostActivity;
import com.akj.sns_project.activity.WritePostActivity;
import com.akj.sns_project.listener.OnPostListener;
import com.bumptech.glide.Glide;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {
    private ArrayList<PostInfo> mDataset;
    private Activity activity;
    private OnPostListener onPostListener;
    private ImageView imageView;
    private int addlikeCount = 0;
    private int addunlikeCount = 0;
    private String likeAction = "";
    private String unlikeAction = "";

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
    }

    // 게시물 업데이트해주는 listner를 초기화해줌
    public void setOnPostListener(OnPostListener onPostListener) { // 인터페이스 초기화해줌
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
                Intent intent = new Intent(activity, PostActivity.class);
                intent.putExtra("postInfo", mDataset.get(mainViewHolder.getAdapterPosition()));
                activity.startActivity(intent);
            }
        });

        cardView.findViewById(R.id.menu).setOnClickListener(new View.OnClickListener() {    // cardview의 menu버튼
            @Override
            public void onClick(View view) {
                showPopup(view, mainViewHolder.getAdapterPosition());    // 설정창 나오게 하는거
            }
        });

        cardView.findViewById(R.id.likeManage).setOnClickListener(new View.OnClickListener() {    // 여기 수정함 11.21
            @Override
            public void onClick(View view) {
                countup(mainViewHolder, mainViewHolder.getAdapterPosition());
            }
        });

        cardView.findViewById(R.id.unlikeManage).setOnClickListener(new View.OnClickListener() {    // 여기 수정함 11.21
            @Override
            public void onClick(View view) {
                countdown(mainViewHolder, mainViewHolder.getAdapterPosition());
            }
        });

        return mainViewHolder;
    }

    public void countup(@NonNull final MainViewHolder holder, int position) {    // 여기 수정함 11.21  이제 셋팅한 카운트 숫자를 파이어베이스로 넣어야함
        CardView cardView = holder.cardView;
        TextView likeCount = cardView.findViewById(R.id.likeCount);
        TextView unlikeCount = cardView.findViewById(R.id.unlikeCount);

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        final DocumentReference documentReference = mDataset.get(position) == null ? firebaseFirestore.collection("posts").document() : firebaseFirestore.collection("posts").document(mDataset.get(position).getId());

        if(likeAction == "" && unlikeAction == ""){
            addlikeCount = 1;
            addunlikeCount = 0;
            likeAction = "liked";
            likeCount.setText(String.valueOf(mDataset.get(position).getlike() + addlikeCount));
            unlikeCount.setText(String.valueOf(mDataset.get(position).getUnlike() + addunlikeCount));

        }else if(likeAction == "" && unlikeAction == "unliked"){
            addlikeCount = 1;
            addunlikeCount = -1;
            likeAction = "liked";
            unlikeAction = "";
            likeCount.setText(String.valueOf(mDataset.get(position).getlike() + addlikeCount));
            unlikeCount.setText(String.valueOf(mDataset.get(position).getUnlike() + addunlikeCount));

        }else if (likeAction == "liked" && unlikeAction == ""){
            addlikeCount = -1;
            addunlikeCount = 0;
            likeAction = "";
            likeCount.setText(String.valueOf(mDataset.get(position).getlike() + addlikeCount));
            unlikeCount.setText(String.valueOf(mDataset.get(position).getUnlike() + addunlikeCount));
        }
        int likenum = mDataset.get(position).getlike() + addlikeCount;
        mDataset.get(position).setlike(likenum);
        int unlikenum = mDataset.get(position).getUnlike() + addunlikeCount;
        mDataset.get(position).setunlike(unlikenum);
        documentReference.set(mDataset.get(position));
    }

    public void countdown(@NonNull final MainViewHolder holder, int position) {    // 여기 수정함 11.21
        CardView cardView = holder.cardView;
        TextView likeCount = cardView.findViewById(R.id.likeCount);
        TextView unlikeCount = cardView.findViewById(R.id.unlikeCount);

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        final DocumentReference documentReference = mDataset.get(position) == null ? firebaseFirestore.collection("posts").document() : firebaseFirestore.collection("posts").document(mDataset.get(position).getId());

        if(unlikeAction == "" && likeAction ==""){
            addunlikeCount = 1;
            addlikeCount = 0;
            unlikeAction = "unliked";
            likeCount.setText(String.valueOf(mDataset.get(position).getlike() + addlikeCount));
            unlikeCount.setText(String.valueOf(mDataset.get(position).getUnlike() + addunlikeCount));

        }else if(unlikeAction == "" && likeAction == "liked"){
            addunlikeCount = 1;
            addlikeCount = -1;
            unlikeAction = "unliked";
            likeAction = "";
            likeCount.setText(String.valueOf(mDataset.get(position).getlike() + addlikeCount));
            unlikeCount.setText(String.valueOf(mDataset.get(position).getUnlike() + addunlikeCount));

        }else if(unlikeAction == "unliked" && likeAction == ""){
            addunlikeCount = -1;
            addlikeCount = 0;
            unlikeAction = "";
            likeCount.setText(String.valueOf(mDataset.get(position).getlike() + addlikeCount));
            unlikeCount.setText(String.valueOf(mDataset.get(position).getUnlike() + addunlikeCount));
        }
        int likenum = mDataset.get(position).getlike() + addlikeCount;
        mDataset.get(position).setlike(likenum);
        int unlikenum = mDataset.get(position).getUnlike() + addunlikeCount;
        mDataset.get(position).setunlike(unlikenum);
        documentReference.set(mDataset.get(position));
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

        // 11.20 수정부분 좋아요 갯수 넣어주기
        TextView likeCount = cardView.findViewById(R.id.likeCount);
        likeCount.setText(String.valueOf(mDataset.get(position).getlike()));

        // 11.20 수정부분 싫어요 갯수 넣어주기
        TextView unlikeCount = cardView.findViewById(R.id.unlikeCount);
        unlikeCount.setText(String.valueOf(mDataset.get(position).getUnlike()));

        // 게시글 넣을때 게시글 안에 사진이나 동영상 있는지
        if (contentsLayout.getTag() == null || !contentsLayout.getTag().equals(contentsList)) {
            contentsLayout.setTag(contentsList);
            contentsLayout.removeAllViews();
            final int MORE_INDEX = 2; // 더보기 제한할 숫자 11.23 대규
            for (int i = 0; i < contentsList.size(); i++) {
                if(i==MORE_INDEX){ // 글 길어지면 더보기버튼 눌러서 볼수있게하는거 11.23 대규
                    TextView textView = new TextView(activity);
                    textView.setLayoutParams(layoutParams);
                    textView.setText("더보기...");
                    contentsLayout.addView(textView);
                    break;
                }
                String contents = contentsList.get(i);
                if (Patterns.WEB_URL.matcher(contents).matches() && contents.contains("https://firebasestorage.googleapis.com/v0/b/sns-project-29021.appspot.com/o/posts")) {   // 글 내용에 사진이나 동영상이 있을 경우
                    // 앞에 조건만 있으면 URL들어오기만하면 다 이미지로 변환해버리니까 뒤에 파이어베이스에서 가져오는 주소인 사진들만 사진변환하게추가 11.23 대규
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
                    textView.setTextColor(Color.rgb(0,0,0));
                    contentsLayout.addView(textView);
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
                        onPostListener.onModify(position); //인터페이스의 onModify를 이용
                        return true;
                    case R.id.delete: //delete버튼을 눌렀을 때
                        onPostListener.onDelete(position); //인터페이스의 onDelete를 이용
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