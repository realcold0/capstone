package com.akj.sns_project;

import static com.akj.sns_project.Util.ADMIN_DK;
import static com.akj.sns_project.Util.ADMIN_JB;
import static com.akj.sns_project.Util.ADMIN_JY;
import static com.akj.sns_project.Util.ADMIN_SH;
import static com.akj.sns_project.Util.ADMIN_YJ;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.akj.sns_project.activity.LoginActivity;
import com.akj.sns_project.activity.MemberInitActivity;
import com.akj.sns_project.activity.WriteBlackPostActivity;
import com.akj.sns_project.activity.WritePostActivity;
import com.akj.sns_project.adapter.BlackAdapter;
import com.akj.sns_project.adapter.MainAdapter;
import com.akj.sns_project.listener.OnPostListener;

import com.android.volley.RequestQueue;

import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.Date;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class BlackBoardFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = "BlackBoardActicity";
    private FirebaseUser firebaseUser;              // 파이어베이스 유저 정보 가져오기 위해 생성한 이름
    private FirebaseFirestore firebaseFirestore;    // 파이어베이스스토어에서 정보 가져오기 위해 사용한 이름
    private BlackAdapter blackAdapter;                // blackadapter 사용하기 위한 이름
    private ArrayList<PostInfo> postList;           // 게시글 정보들을 저장하기 위한 이름
    private StorageReference storageRef;
    private View view;
    private FloatingActionButton floatingActionButton;
    private RecyclerView recyclerView;
    private int successCount;
    private FirebaseUser user;
    private String userUid;
    private String publisher;

    static RequestQueue requestQueue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_black_board, container, false);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // 파이어베이스 초기화 함수들
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        if (requestQueue == null) {

            requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        }

        if (firebaseUser == null) {     // 위에서 받아온 유저정보가 NULL값이면 == 로그인이 안되어 있으면 로그인 액티비티부터 시작
            myStartActivity(LoginActivity.class);

        } else {
            //여기서부터
            firebaseFirestore = FirebaseFirestore.getInstance();    // 로그인이 되어 있으면 FirebaseStore에서 정보를 받아오는데
            DocumentReference documentReference = firebaseFirestore.collection("users").document(firebaseUser.getUid());    // 파이어베이스스토어의 users폴더 안에서 유저의 UID를 받아온다
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {  // 불러오는 것에 성공했다면
                        DocumentSnapshot document = task.getResult(); // 결과를 여기에 넣고
                        if (document != null) { // 넣은 결과 값이 null이 아니라면
                            if (document.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData()); // 로그로 불러온 정보를 보여주고
                            } else {
                                Log.d(TAG, "No such document");     // 파이어베이스 유저정보에 유저정보가 없으면 멤버정보 입력하는 액티비티를 보여줌
                                myStartActivity(MemberInitActivity.class);
                            }
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());    // 정보 불러오는거 실패했을 경우
                    }
                }
            });
            // 여기까지는 파이어베이스에서 제공하는 코드로 파이어스토어에 저장되어 있는 데이터 읽어오는 코드
        }
        postList = new ArrayList<>();   // 게시글을 저장하기 위해 선언한 배열

        //게시물 업데이트(새로고침)을 위한 메서드. 데이터가 업데이트 되면 adapter를 다시 바꿔줘야함.
        //blackAdaper에서 넘겨줌.
        blackAdapter = new BlackAdapter(getActivity(), postList);
        blackAdapter.setOnPostListener(onPostListener); //onPostListener를 넘겨주면 blackAdapter에서도 쓸수있음.

        floatingActionButton = view.findViewById(R.id.floatingActionButton);
        recyclerView = view.findViewById(R.id.recyclerView);

        floatingActionButton.setOnClickListener(onClickListener);

        recyclerView.setHasFixedSize(true); // 글을 불러오고 나서는 recyclerview를 글 갯수에 따라서 크기를 조절한다
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity())); // recyclerview를 수직으로 보여주는 linearlayoutmanager
        recyclerView.setAdapter(blackAdapter);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userUid = user.getUid().toString();

        initRecyclerViewAndAdapter();
        SearchView searchView = view.findViewById(R.id.search_view);
        Button Btn = view.findViewById(R.id.button6);
        Btn.setOnClickListener(this);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userUid = user.getUid().toString();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                blackAdapter = new BlackAdapter(getActivity(), search(query));
                //postList = search(query);
                recyclerView.setAdapter(blackAdapter);
                //mainAdapter.notifyDataSetChanged();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });
        return view;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.button6){
            blackAdapter = new BlackAdapter(getActivity(), postList);
            recyclerView.setAdapter(blackAdapter);
            //getParentFragmentManager().beginTransaction().replace(R.id.frameLayout,fragment_post).commit();
        }
    }

    private ArrayList<PostInfo> search(String query){
        ArrayList<PostInfo> postlist = new ArrayList<>();
        for(int i = 0; i < postList.size(); i++) {
            String title = postList.get(i).getTitle();
            if(title.toLowerCase().contains(query.toLowerCase())){
                postlist.add(postList.get(i));
            }
        }
        return postlist;
    }

    private void initRecyclerViewAndAdapter() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);
    }

    @Override
    public void onResume() {  // 게시글 올리자마자 업데이트 될 수 있도록
        super.onResume();
        postsUpdate();
    }

    OnPostListener onPostListener = new OnPostListener() { //인터페이스인 OnPostListener를 가져와서 구현해줌
        @Override
        public void onDelete(int position) {       // 게시글 삭제 기능_대규 여기서부터
            final String id = postList.get(position).getId();
            ArrayList<String> contentsList = postList.get(position).getContents();
            publisher = postList.get(position).getPublisher();
            if(userUid.equals(publisher)||userUid.equals(ADMIN_DK)||userUid.equals(ADMIN_JB)||userUid.equals(ADMIN_JY)||userUid.equals(ADMIN_SH)||userUid.equals(ADMIN_YJ)) {
                for (int i = 0; i < contentsList.size(); i++) {
                    String contents = contentsList.get(i);
                    if (Patterns.WEB_URL.matcher(contents).matches() && contents.contains("https://firebasestorage.googleapis.com/v0/b/sns-project-29021.appspot.com/o/blackposts")) {   // 글 내용에 사진이나 동영상이 있을 경우
                        // 앞에 조건만 있으면 URL들어오기만하면 다 이미지로 변환해버리니까 뒤에 파이어베이스에서 가져오는 주소인 사진들만 사진변환하게추가 11.23 대규
                        successCount++;
                        String[] list = contents.split("\\?"); // 저장되는 이미지 주소를 \\와 %2F로 잘라서 저장하여
                        String[] list2 = list[0].split("%2F");
                        String name = list2[list2.length - 1];
                        // Create a reference to the file to delete
                        StorageReference desertRef = storageRef.child("blackposts/" + id + "/" + name);
                        // Delete the file
                        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                successCount--;
                                storeUploader(id);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                startToast("삭제 실패");
                            }
                        });
                    }
                }
                storeUploader(id);
            }
            else{
                startToast("다른사람의 게시글을 삭제할 수 없습니다");
            }
        }

        @Override
        public void onModify(int position) {
            myStartActivity(WritePostActivity.class, postList.get(position));
        }   // 게시글 수정 기능_대규
        @Override
        public void onGoBlack(int position) {        }
    };

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.floatingActionButton:
                    myStartActivity(WriteBlackPostActivity.class);   // 글쓰기 버튼 클릭 시 이동 _ 대규
                    break;
            }
        }
    };

    //실제 게시물을 보여주고 업데이트 해주는 코드
    private void postsUpdate() {
        if (firebaseUser != null) {
            CollectionReference collectionReference = firebaseFirestore.collection("blackposts");    // 파이어베이스 blackposts폴더를 사용
            collectionReference.orderBy("createdAt", Query.Direction.DESCENDING).get()  // 파이어베이스 blackposts안에 있는 내용을 createdAt 순서로 정렬
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                postList.clear();   // 초기화 하고 가져오는 방식으로 업데이트
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData().get("like"));
                                    postList.add(new PostInfo(  // 여기서부터
                                            document.getData().get("title").toString(),
                                            (ArrayList<String>) document.getData().get("contents"),
                                            document.getData().get("publisher").toString(),
                                            new Date(document.getDate("createdAt").getTime()),
                                            document.getId(),
                                            Integer.parseInt(document.getData().get("like").toString()),
                                            Integer.parseInt(document.getData().get("unlike").toString()),
                                            document.getData().get("saveLocation").toString(),
                                            (ArrayList<String>) document.getData().get("favorites"),
                                            (ArrayList<String>) document.getData().get("unfavorites")
                                    ));
                                }
                                blackAdapter.notifyDataSetChanged();
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }

    private void storeUploader(String id){
        if(successCount == 0) {
            firebaseFirestore.collection("blackposts").document(id)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            startToast("게시글을 삭제하였습니다.");
                            postsUpdate();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            startToast("게시글을 삭제하지 못하였습니다.");
                        }
                    });
        }
    }


    private void myStartActivity(Class c) { // 액티비티 이동하는 함수
        Intent intent = new Intent(getActivity(), c);
        startActivity(intent);
    }

    private void myStartActivity(Class c, PostInfo postInfo) {  // 게시글 수정할때 사용하는 액티비티 이동함수
        Intent intent = new Intent(getActivity(), c);
        intent.putExtra("postInfo", postInfo);// 검은 게시판용 info 파일 만들기
        startActivity(intent);
    }

    private void startToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

}