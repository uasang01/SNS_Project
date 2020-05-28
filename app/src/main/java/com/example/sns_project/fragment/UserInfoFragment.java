package com.example.sns_project.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.sns_project.R;
import com.example.sns_project.UserInfo;
import com.example.sns_project.activity.LogInActivity;
import com.example.sns_project.activity.MemberInitActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class UserInfoFragment extends Fragment {
    private final static String TAG = "UserListFragment";
    private UserInfo userInfo;
    private ImageView profileImageView;
    private TextView nameTextView;
    private TextView phoneTextView;
    private TextView birthdayTextView;
    private TextView addressTextView;

    public UserInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_info, container, false);

        profileImageView = view.findViewById(R.id.profileImageView);
        nameTextView = view.findViewById(R.id.nameTextView);
        phoneTextView = view.findViewById(R.id.phoneTextView);
        birthdayTextView = view.findViewById(R.id.birthdayTextView);
        addressTextView = view.findViewById(R.id.addressTextView);
        Button logoutButton = view.findViewById(R.id.logOutButton);
        Button modifyMyInfoButton = view.findViewById(R.id.modifyMyInfoButton);

        logoutButton.setOnClickListener(onClickListener);
        modifyMyInfoButton.setOnClickListener(onClickListener);

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        if (document.getData() != null) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            if(document.getData().get("photoUrl") != null){
                                Log.e("왜","안오니");

                                userInfo = new UserInfo(
                                        document.getData().get("name").toString(),
                                        document.getData().get("phone").toString(),
                                        document.getData().get("birthday").toString(),
                                        document.getData().get("address").toString(),
                                        document.getData().get("photoUrl").toString());
                            }else{
                                userInfo = new UserInfo(
                                        document.getData().get("name").toString(),
                                        document.getData().get("phone").toString(),
                                        document.getData().get("birthday").toString(),
                                        document.getData().get("address").toString());
                            }
                            updateUserInfo();
                        }
                    } else {
                        if (document.getData() == null) {
                            Log.d(TAG, "No such document");
                        }
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        return view;
    }

    public void updateUserInfo(){
        if(userInfo.getPhotoUrl() != null){
            Glide.with(getActivity()).load(userInfo.getPhotoUrl()).centerCrop().override(500).into(profileImageView);
        }
        nameTextView.setText(userInfo.getName());
        phoneTextView.setText(userInfo.getPhone());
        birthdayTextView.setText(userInfo.getBirthday());
        addressTextView.setText(userInfo.getAddress());
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.logOutButton:
                    FirebaseAuth.getInstance().signOut();
                    startNewActivity(LogInActivity.class);
                    break;
                case R.id.modifyMyInfoButton:
                    Activity activity = getActivity();
                    Intent intent = new Intent(activity, MemberInitActivity.class);
                    intent.putExtra("userInfo", userInfo);
                    activity.setResult(activity.RESULT_OK,intent);
                    activity.startActivityForResult(intent, 2);
                    break;
            }
        }
    };
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 2:
                assert data != null;
                userInfo = (UserInfo) data.getSerializableExtra("userInfo");
                updateUserInfo();
                break;
        }
    }
    @Override
    public void onAttach(Context context) {
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

    private void startNewActivity(Class c) {
        Intent intent = new Intent(getActivity(), c);
        if (c == LogInActivity.class) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        startActivityForResult(intent, 0);
    }
}
