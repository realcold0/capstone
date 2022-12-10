package com.akj.sns_project.adapter;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
        holder.onBind(mFriendList.get(position));
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            HashText = (TextView) itemView.findViewById(R.id.HashText);
        }

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
        }
    }
}
