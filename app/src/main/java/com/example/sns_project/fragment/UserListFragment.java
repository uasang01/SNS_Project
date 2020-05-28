package com.example.sns_project.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sns_project.PostInfo;
import com.example.sns_project.R;
import com.example.sns_project.UserInfo;
import com.example.sns_project.activity.LogInActivity;
import com.example.sns_project.activity.MemberInitActivity;
import com.example.sns_project.activity.WritePostActivity;
import com.example.sns_project.adapter.PostAdapter;
import com.example.sns_project.adapter.UserListAdapter;
import com.example.sns_project.listener.OnPostListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class UserListFragment extends Fragment {
    private final static String TAG = "UserListFragment";


    private FirebaseFirestore firebaseFirestore;
    private UserListAdapter userListAdapter;
    private ArrayList<UserInfo> userList;
    private boolean updating;
    private boolean topScrolled;

    public UserListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_list, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        userList = new ArrayList<>();
        userListAdapter = new UserListAdapter(getActivity(), userList);

        RecyclerView recyclerView = view.findViewById(R.id.userListRecyclerView);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(userListAdapter);
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
                    updateUserList();
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
                    updateUserList();
                }

                if (0 < firstVisitableItemPosition) {
                    topScrolled = false;
                }
            }
        });
        updateUserList();

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void updateUserList() {
        updating = true;
        firebaseFirestore = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = firebaseFirestore.collection("users");
        collectionReference
                .orderBy("name", Query.Direction.DESCENDING).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            userList.clear();

                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                userList.add(new UserInfo(
                                        Objects.requireNonNull(document.getData().get("name")).toString(),
                                        Objects.requireNonNull(document.getData().get("phone")).toString(),
                                        Objects.requireNonNull(document.getData().get("birthday")).toString(),
                                        Objects.requireNonNull(document.getData().get("address")).toString(),
                                        document.getData().get("photoUrl") == null ? null : Objects.requireNonNull(document.getData().get("photoUrl")).toString()));
                            }
                            userListAdapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        updating = false;
                    }
                });
    }

}
