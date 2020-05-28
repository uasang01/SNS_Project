package com.example.sns_project.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.example.sns_project.R;
import com.example.sns_project.PostInfo;
import com.example.sns_project.view.PostContentsItemView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.example.sns_project.Util.GALLERY_IMAGE;
import static com.example.sns_project.Util.GALLERY_VIDEO;
import static com.example.sns_project.Util.INTENT_MEDIA;
import static com.example.sns_project.Util.INTENT_PATH;
import static com.example.sns_project.Util.isImageFile;
import static com.example.sns_project.Util.isStorageUrl;
import static com.example.sns_project.Util.isVideoFile;
import static com.example.sns_project.Util.showToast;
import static com.example.sns_project.Util.storageUriToName;

public class WritePostActivity extends BasicActivity {
    final String TAG ="WritePostActivity";
    private FirebaseUser user;
    private ArrayList<String> pathList = new ArrayList<String>();
    private  LinearLayout parent;
    private int successCount, pathCount;
    private RelativeLayout buttonsBackgroundLayout;
    private RelativeLayout loaderLayout;
    private ImageView selectedImageView;
    private EditText selectedEditText;
    private EditText postContentEditText;
    private EditText postTitleEditText;
    private PostInfo postInfo;
    private FirebaseStorage storage;
    private StorageReference storageRef;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_white_post);
        setToolbarTitle("게시글 작성");
        storage = FirebaseStorage.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        loaderLayout = findViewById(R.id.loaderLayout);
        parent = findViewById(R.id.contentsLinearLayout);
        buttonsBackgroundLayout = findViewById(R.id.buttonsBackgroundLayout);
        postContentEditText=findViewById(R.id.postContentEditText);
        postTitleEditText=findViewById(R.id.postTitleEditText);


        findViewById(R.id.checkButton).setOnClickListener(onClickListener);
        findViewById(R.id.imageButton).setOnClickListener(onClickListener);
        findViewById(R.id.videoButton).setOnClickListener(onClickListener);
        buttonsBackgroundLayout.setOnClickListener(onClickListener);
        findViewById(R.id.imageModifyButton).setOnClickListener(onClickListener);
        findViewById(R.id.videoModifyButton).setOnClickListener(onClickListener);
        findViewById(R.id.deleteButton).setOnClickListener(onClickListener);
        postContentEditText.setOnFocusChangeListener(onFocusChangeListener);
        postTitleEditText.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    selectedEditText = null;
                }
            }
        });

        postInfo = (PostInfo)getIntent().getSerializableExtra("postInfo");
        initPostPage();
    }

    private void initPostPage() {
        if (postInfo != null) {
            postTitleEditText.setText(postInfo.getTitle());
            ArrayList<String> contentsList = postInfo.getContent();
            for (int i = 0; i < contentsList.size(); i++) {
                String content = contentsList.get(i);
                if (isStorageUrl(content)) {
                    pathList.add(content);
                    PostContentsItemView postContentsItemView = new PostContentsItemView(this);
                    parent.addView(postContentsItemView);
                    postContentsItemView.setImage(content);
                    postContentsItemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selectedImageView = (ImageView) v;
                            if (buttonsBackgroundLayout.getVisibility() == View.VISIBLE) {
                                buttonsBackgroundLayout.setVisibility(View.GONE);
                            } else {
                                buttonsBackgroundLayout.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                    postContentsItemView.setOnFocusChangeListener(onFocusChangeListener);

                    if(i<contentsList.size()-1){
                        String nextContent = contentsList.get(i+1);
                        if(!isStorageUrl(nextContent)){
                            postContentsItemView.setText(nextContent);
                        }
                    }
                }else if(i==0){
                    postContentEditText.setText(content);
                }
            }
        }
    }

    View.OnClickListener onClickListener= new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.checkButton:
                    postUpload();
//                    finish();
                    break;
                case R.id.imageButton:
                    startNewActivityForResult(GalleryActivity.class,GALLERY_IMAGE, 0);
                    break;
                case R.id.videoButton:
                    startNewActivityForResult(GalleryActivity.class,GALLERY_VIDEO, 0);
                    break;
                case R.id.buttonsBackgroundLayout:
                    if(buttonsBackgroundLayout.getVisibility() == View.VISIBLE){
                        buttonsBackgroundLayout.setVisibility(View.GONE);
                    }else{
                        buttonsBackgroundLayout.setVisibility(View.VISIBLE);
                    }
                    break;
                case R.id.imageModifyButton:
                    startNewActivityForResult(GalleryActivity.class,GALLERY_IMAGE, 1);
                    buttonsBackgroundLayout.setVisibility(View.GONE);
                    break;
                case R.id.videoModifyButton:
                    startNewActivityForResult(GalleryActivity.class,GALLERY_VIDEO, 1);
                    buttonsBackgroundLayout.setVisibility(View.GONE);
                    break;
                case R.id.deleteButton:
                    final View selectedView = (View)selectedImageView.getParent();
                    String path = pathList.get(parent.indexOfChild(selectedView)-1);
                    StorageReference desertRef = storageRef.child("posts/"+postInfo.getId()+"/"+storageUriToName(path));
                    Log.e("hi",postInfo.getId()+" "+storageUriToName(path));
                    if(isStorageUrl(path)){
                        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // File deleted successfully
                                pathList.remove(parent.indexOfChild(selectedView)-1);
                                parent.removeView(selectedView);
                                buttonsBackgroundLayout.setVisibility(View.GONE);
                                showToast(WritePostActivity.this,"파일을 삭제하였습니다");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Uh-oh, an error occurred!
                                showToast(WritePostActivity.this,"파일을 삭제하는데 실패하였습니다");
                            }
                        });
                    }else{
                        pathList.remove(parent.indexOfChild(selectedView)-1);
                        parent.removeView(selectedView);
                        buttonsBackgroundLayout.setVisibility(View.GONE);
                        showToast(WritePostActivity.this,"파일을 삭제하였습니다");
                    }
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == Activity.RESULT_OK) {
                    String imagePath = data.getStringExtra(INTENT_PATH);
                    pathList.add(imagePath);

                    PostContentsItemView postContentsItemView = new PostContentsItemView(this);
                    if(selectedEditText!=null){
                        for(int i=0;i<parent.getChildCount();i++){
                            if(parent.getChildAt(i) == selectedEditText.getParent()){
                                parent.addView(postContentsItemView, i+1);
                                break;
                            }
                        }
                    }else{
                        parent.addView(postContentsItemView);
                    }

                    postContentsItemView.setImage(imagePath);
                    postContentsItemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selectedImageView = (ImageView) v;
                            if (buttonsBackgroundLayout.getVisibility() == View.VISIBLE) {
                                buttonsBackgroundLayout.setVisibility(View.GONE);
                            } else {
                                buttonsBackgroundLayout.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                    postContentsItemView.setOnFocusChangeListener(onFocusChangeListener);
                }
                break;
            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    String imagePath = data.getStringExtra(INTENT_PATH);
                    pathList.set(parent.indexOfChild((View)selectedImageView.getParent())-1, imagePath);
                    Glide.with(this).load(imagePath).override(1000).into(selectedImageView);
                }
                break;
        }
    }

    View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener(){
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(hasFocus){
                selectedEditText = (EditText)v;
            }
        }
    };

    private void postUpload(){
        final String title = ((EditText)findViewById(R.id.postTitleEditText)).getText().toString();

        if(title.length()>0) {
            loaderLayout.setVisibility(View.VISIBLE);
            final ArrayList<String> contentsList = new ArrayList<>();
            final ArrayList<String> formatList = new ArrayList<>();
            user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

            final DocumentReference documentReference = postInfo == null ?
                    firebaseFirestore.collection("posts").document() :
                    firebaseFirestore.collection("posts").document(postInfo.getId());

            final Date date = postInfo == null ?
                    new Date() :
                    postInfo.getCreatedAt();

            for(int i=0 ; i<parent.getChildCount() ; i++) {
                LinearLayout linearLayout = (LinearLayout) parent.getChildAt(i);
                for (int j = 0; j < linearLayout.getChildCount(); j++) {
                    View view = linearLayout.getChildAt(j);
                    if (view instanceof EditText) {
                        String text = ((EditText) view).getText().toString();
                        if (text.length() > 0) {
                            contentsList.add(text);
                            formatList.add("text");
                        }
                    } else if (!isStorageUrl(pathList.get(pathCount))) {
                        String path = pathList.get(pathCount);
                        successCount++;
                        contentsList.add(path);
                        if (isImageFile(path)) {
                            formatList.add("image");
                        } else if (isVideoFile(path)) {
                            formatList.add("video");
                        } else {
                            formatList.add("text");
                        }
                        String[] pathArray = pathList.get(pathCount).split("\\.");
                        final StorageReference mountainImagesRef = storageRef.child("posts/" + documentReference.getId() + "/" + pathCount + "." + pathArray[pathArray.length - 1]);
                        try {
                            InputStream stream = new FileInputStream(new File(pathList.get(pathCount)));
                            StorageMetadata metadata = new StorageMetadata.Builder().setCustomMetadata("index", "" + (contentsList.size() - 1)).build();
                            UploadTask uploadTask = mountainImagesRef.putStream(stream, metadata);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle unsuccessful uploads
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    final int index = Integer.parseInt(taskSnapshot.getMetadata().getCustomMetadata("index"));
                                    mountainImagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            successCount--;
                                            contentsList.set(index, uri.toString());
                                            if (successCount == 0) {
                                                PostInfo postInfo = new PostInfo(title, contentsList, formatList, user.getUid(), date);
                                                uploader(postInfo, documentReference);
                                            }
                                        }
                                    });
                                    Log.e("로그", "게시긍등록");
                                }
                            });
                        } catch (FileNotFoundException e) {
                            Log.e("로그", "에러" + e.toString());
                        }
                        pathCount++;
                    }
                }
            }
            if(successCount==0){
                uploader( new PostInfo(title, contentsList, formatList, user.getUid(), date), documentReference);
            }
        }else{
            showToast(WritePostActivity.this,"제목을 입력해 주세요");
        }
    }

    private void uploader(final PostInfo postInfo, DocumentReference documentReference){
        documentReference.set(postInfo.getPostInfo())
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "DocumentSnapshot successfully written!");
                    loaderLayout.setVisibility(View.GONE);
                    showToast(WritePostActivity.this, "게시글 등록했습니다");
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("postInfo", postInfo);
                    setResult(RESULT_OK,resultIntent);
                    finish();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Error writing document", e);
                    loaderLayout.setVisibility(View.GONE);
                    showToast(WritePostActivity.this,"게시글 등록에 실패했습니다");
                }
            });
    }

    private void startNewActivityForResult(Class c, int media, int requestCode){
        Intent intent = new Intent(this, c);
        intent.putExtra(INTENT_MEDIA ,media);
        startActivityForResult(intent,requestCode);
    }
}
