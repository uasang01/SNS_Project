package com.example.sns_project.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.bumptech.glide.Glide;
import com.example.sns_project.UserInfo;
import com.example.sns_project.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import static com.example.sns_project.Util.INTENT_PATH;
import static com.example.sns_project.Util.showToast;

public class MemberInitActivity extends BasicActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private static final String TAG = "MemberInitActivity";
    private ImageView profileImageView;
    private String profilePath;
    private RelativeLayout loaderLayout;
    private RelativeLayout buttonsBackgroundLayout;
    private UserInfo userInfo;
    private Boolean hasProfile = false;

    private EditText nameEditText;
    private EditText phoneEditText;
    private EditText birthdayEditText;
    private EditText addressEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        setToolbarTitle("회원정보");
        mAuth = FirebaseAuth.getInstance();
        loaderLayout = findViewById(R.id.loaderLayout);
        buttonsBackgroundLayout = findViewById(R.id.buttonsBackgroundLayout);
        buttonsBackgroundLayout.setOnClickListener(onClickListener);
        profileImageView = findViewById(R.id.profileImageView);
        profileImageView.setOnClickListener(onClickListener);

        nameEditText = findViewById(R.id.nameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        birthdayEditText = findViewById(R.id.birthDayEditText);
        addressEditText = findViewById(R.id.addressEditText);

        userInfo = (UserInfo)getIntent().getSerializableExtra("userInfo");

        if(userInfo!= null){
            if(userInfo.getPhotoUrl() != null){
                hasProfile = true;
                Glide.with(this).load(userInfo.getPhotoUrl()).centerCrop().override(500).into(profileImageView);
            }

            nameEditText.setText(userInfo.getName());
            phoneEditText.setText(userInfo.getPhone());
            birthdayEditText.setText(userInfo.getBirthday());
            addressEditText.setText(userInfo.getAddress());
        }

        findViewById(R.id.finishButton).setOnClickListener(onClickListener);
        findViewById(R.id.cameraButton).setOnClickListener(onClickListener);
        findViewById(R.id.galleyButton).setOnClickListener(onClickListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == Activity.RESULT_OK) {
                    profilePath = data.getStringExtra(INTENT_PATH);

                    Glide.with(this).load(profilePath).centerCrop().override(500).into(profileImageView);
                }
                break;
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.finishButton:
                    Log.e("finishButton", " Clicked");
                    profileUpdate();
                    break;
                case R.id.profileImageView:
                    if (buttonsBackgroundLayout.getVisibility() == View.VISIBLE) {
                        buttonsBackgroundLayout.setVisibility(View.GONE);
                    } else {
                        buttonsBackgroundLayout.setVisibility(View.VISIBLE);
                    }
                    break;
                case R.id.buttonsBackgroundLayout:
                    buttonsBackgroundLayout.setVisibility(View.GONE);
                    break;
                case R.id.cameraButton:
                    Log.e("camera", "clicked");
                    startNewActivityForResult(CameraActivity.class, 0);
                    break;
                case R.id.galleyButton:
                    startNewActivityForResult(GalleryActivity.class, 0);
                    break;
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startNewActivityForResult(GalleryActivity.class, 0);
                } else {
                    showToast(MemberInitActivity.this, "권한을 허가해 주세요.");
                }
            }
        }
    }

    private void profileUpdate() {
        final String name = ((EditText) findViewById(R.id.nameEditText)).getText().toString();
        final String phone = ((EditText) findViewById(R.id.phoneEditText)).getText().toString();
        final String birthday = ((EditText) findViewById(R.id.birthDayEditText)).getText().toString();
        final String address = ((EditText) findViewById(R.id.addressEditText)).getText().toString();

        if (name.length() > 0 && phone.length() > 9 && birthday.length() > 5 && address.length() > 0) {
            loaderLayout.setVisibility(View.VISIBLE);
            final FirebaseAuth mAuth = FirebaseAuth.getInstance();
            user = mAuth.getCurrentUser();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            final StorageReference mountainImagesRef = storageRef.child("users/" + user.getUid() + "profileImage.jpg");

            if (profilePath == null) {
                if(hasProfile){
                    userInfo.setName(name);
                    userInfo.setBirthday(birthday);
                    userInfo.setPhone(phone);
                    userInfo.setAddress(address);
                } else {
                    userInfo = new UserInfo(name, phone, birthday, address);
                }
                uploader(userInfo);
            } else {
                try {
                    InputStream stream = new FileInputStream(new File(profilePath));
                    UploadTask uploadTask = mountainImagesRef.putStream(stream);
                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            // Continue with the task to get the download URL
                            return mountainImagesRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUrl = task.getResult();
                                userInfo = new UserInfo(name, phone, birthday, address, downloadUrl.toString());
                                uploader(userInfo);
                            } else {
                                // Handle failures
                                showToast(MemberInitActivity.this, "회원정보 등록에 실패했습니다.");
                            }
                        }
                    });
                } catch (FileNotFoundException e) {
                    Log.e("로그", "에러" + e.toString());
                }
            }
        } else {
            showToast(MemberInitActivity.this, "칸을 채워주세요");
        }
    }

    private void uploader(final UserInfo userInfo) {
        db = FirebaseFirestore.getInstance();
        db.collection("users").document(user.getUid()).set(userInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showToast(MemberInitActivity.this, "회원정보 등록을 완료했습니다.");
                        loaderLayout.setVisibility(View.GONE);
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("userInfo", userInfo);
                        setResult(RESULT_OK,resultIntent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                        showToast(MemberInitActivity.this, "회원정보 등록을 실패했습니다.");
                        loaderLayout.setVisibility(View.GONE);
                    }
                });
    }

    private void startNewActivityForResult(Class c, int resultCode) {
        Intent intent = new Intent(MemberInitActivity.this, c);
        startActivityForResult(intent, resultCode);
    }
}
