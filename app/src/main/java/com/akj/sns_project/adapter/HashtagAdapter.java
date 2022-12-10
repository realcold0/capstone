package com.akj.sns_project.adapter;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.akj.sns_project.R;
import com.akj.sns_project.activity.WritePostActivity;

import java.util.ArrayList;

public class HashtagAdapter extends RecyclerView.Adapter<HashtagAdapter.ViewHolder> {

    private ArrayList<String> mFriendList = new ArrayList<>();

    @NonNull
    @Override
    public HashtagAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hashtag, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HashtagAdapter.ViewHolder holder, int position) {
        //holder.onBind(mFriendList.get(position));
        String string = mFriendList.get(position);
        holder.HashText.setText(string);  //받아온 어레이의 텍스트를 각각 넣어준다.


        holder.HashText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity)v.getContext();  //현재 액티비티를 가져온다.
                TextView textView = activity.findViewById(R.id.textView7rename);  //위에 보여줄 텍스트 박스를 넣어준다.
                if(textView.getText() == null)   //아무것도 추가 안했을때
                {
                    textView.setText(string);
                }
                else //1개 추가 하고 나서
                {
                    textView.append(string);
                }

            }
        });


    }

    public void setFriendList(ArrayList<String> list){
        this.mFriendList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mFriendList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView HashText;
        TextView showList;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            HashText = (TextView) itemView.findViewById(R.id.HashText);

        }

        /*
        void onBind(String item){
            HashText.setText(item);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(), WritePostActivity.class);
                    intent.putExtra("hash", item);
                    WritePostActivity aPost = (WritePostActivity) WritePostActivity.Apost;
                    aPost.finish();
                    itemView.getContext().startActivity(intent);

                }
            });


        }*/
    }
}

