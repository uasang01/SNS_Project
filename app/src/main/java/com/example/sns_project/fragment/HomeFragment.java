package com.example.sns_project.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sns_project.PostInfo;
import com.example.sns_project.R;
import com.example.sns_project.activity.LogInActivity;
import com.example.sns_project.activity.WritePostActivity;
import com.example.sns_project.adapter.PostAdapter;
import com.example.sns_project.listener.OnPostListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

public class HomeFragment extends Fragment {
    private final static String TAG = "HomeFragment";

    private PostAdapter postAdapter;
    private ArrayList<PostInfo> postList;
    private boolean updating;
    private boolean topScrolled;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        postList = new ArrayList<>();
        postAdapter = new PostAdapter(getActivity(), postList);
        postAdapter.setOnPostListener(onPostListener);

        RecyclerView recyclerView = view.findViewById(R.id.mainRecyclerView);
        view.findViewById(R.id.floatingActionButton).setOnClickListener(onClickListener);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(postAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                int firstVisitableItemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();

                if (newState == 1 && firstVisitableItemPosition == 0) {
                    topScrolled = true;
                }

                if (newState == 0 && topScrolled) {
                    updatePost(true);
                    topScrolled = false;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisitableItemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                int lastVisitableItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();

                if (totalItemCount - 3 <= lastVisitableItemPosition && !updating) {
                    updatePost(false);
                }

                if (0 < firstVisitableItemPosition) {
                    topScrolled = false;
                }
            }
        });
        updatePost(false);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private OnPostListener onPostListener = new OnPostListener() {
        @Override
        public void onDelete(PostInfo postInfo) {
            postList.remove(postInfo);
            postAdapter.notifyDataSetChanged();
            Log.e("삭제", "성공");
        }

        @Override
        public void onModify() {
            Log.e("수정", "성공");
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        postAdapter.videoStop();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.floatingActionButton) {
                startNewActivity(WritePostActivity.class);
            }
        }
    };

    private void updatePost(final boolean clear) {
        updating = true;
        Date date = postList.size() == 0 || clear ? new Date() : postList.get(postList.size() - 1).getCreatedAt();
        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("posts");
        collectionReference
                .orderBy("createdAt", Query.Direction.DESCENDING).whereLessThan("createdAt", date).limit(10).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (clear) {
                                postList.clear();
                            }

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
//                                    String title, ArrayList<String> content, String publisher, Date createdAt
                                postList.add(new PostInfo(
                                        document.getId(),
                                        document.getData().get("title").toString(),
                                        (ArrayList<String>) document.getData().get("content"),
                                        (ArrayList<String>) document.getData().get("format"),
                                        document.getData().get("publisher").toString(),
                                        new Date(document.getDate("createdAt").getTime())));
                            }
                            postAdapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        updating = false;
                    }
                });

    }

    private void startNewActivity(Class c) {
        Intent intent = new Intent(getActivity(), c);
        if (c == LogInActivity.class) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        startActivityForResult(intent, 0);
    }
}
