package com.example.sns_project.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.example.sns_project.FirebaseHelper;
import com.example.sns_project.PostInfo;
import com.example.sns_project.R;
import com.example.sns_project.listener.OnPostListener;
import com.example.sns_project.view.ReadContentsView;
import androidx.annotation.Nullable;

public class PostActivity extends BasicActivity {
    private PostInfo postInfo;
    private FirebaseHelper firebaseHelper;
    private ReadContentsView readContentsView;
    private LinearLayout contentsLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        postInfo = (PostInfo)getIntent().getSerializableExtra("postInfo");
        readContentsView = findViewById(R.id.readContentsView);
        contentsLayout=findViewById(R.id.contentsLayout);

        firebaseHelper = new FirebaseHelper(this);
        firebaseHelper.setOnPostListener(onPostListener);
        uiUpdate();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.post, menu);

        return super.onCreateOptionsMenu(menu);
    }

    OnPostListener onPostListener = new OnPostListener() {
        @Override
        public void onDelete(PostInfo postInfo) {
            Log.e("삭제", "성공");
        }

        @Override
        public void onModify() {
            Log.e("수정", "성공");
        }
    };

    private void uiUpdate(){
        setToolbarTitle(postInfo.getTitle());
        readContentsView.setPostInit(postInfo);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.modify:
                startNewActivity(WritePostActivity.class, postInfo);
                return true;

            case R.id.delete:
                firebaseHelper.storageDelete(postInfo);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == Activity.RESULT_OK) {
                    if(data != null){
                        postInfo = (PostInfo)data.getSerializableExtra("postInfo");
                        contentsLayout.removeAllViews();
                        uiUpdate();
                    }
                }
                break;
        }
    }

    private void startNewActivity(Class c, PostInfo postInfo) {
        Intent intent = new Intent(PostActivity.this, c);
        intent.putExtra("postInfo", postInfo);
        startActivityForResult(intent,0);
    }
}
