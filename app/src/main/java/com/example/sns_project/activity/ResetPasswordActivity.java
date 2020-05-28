package com.example.sns_project.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.sns_project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.example.sns_project.Util.showToast;

public class ResetPasswordActivity extends BasicActivity {
    //    Intent intent;
    FirebaseAuth mAuth;
    String email;
    final String TAG = "ResetPasswordActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        setToolbarTitle(getString(R.string.app_name));
        findViewById(R.id.sendButton).setOnClickListener(onClickListener);
        mAuth = FirebaseAuth.getInstance();

    }
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String emailAddress = "user@example.com";


    View.OnClickListener onClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.sendButton:
                    send();
                    break;
            }
        }
    };

    private void send(){
        email = ((EditText)findViewById(R.id.emailEditText)).getText().toString();
        if(email.length()>0){
            final RelativeLayout loaderLayout = findViewById(R.id.loaderLayout);
            loaderLayout.setVisibility(View.VISIBLE);
            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        showToast(ResetPasswordActivity.this,"이메일을 전송 완료");
                        loaderLayout.setVisibility(View.GONE);
                        onBackPressed();
                    }else{
                        showToast(ResetPasswordActivity.this,"이메일 전송 실패");
                        loaderLayout.setVisibility(View.GONE);
                    }
                }
            });
        }else {
            showToast(ResetPasswordActivity.this, "이메일을 확인하세요");
        }
    }
}
