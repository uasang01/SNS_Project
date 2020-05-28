package com.example.sns_project.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.sns_project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.example.sns_project.Util.showToast;

public class LogInActivity extends BasicActivity {
    private String email,password;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        setToolbarTitle(getString(R.string.app_name));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mAuth = FirebaseAuth.getInstance();
        ((EditText)findViewById(R.id.emailEditText)).setText("uasang01@nate.com");
        ((EditText)findViewById(R.id.passwordEditText)).setText("325146");

        findViewById(R.id.logInButton).setOnClickListener(onClickListener);
        findViewById(R.id.signUpButton).setOnClickListener(onClickListener);
        findViewById(R.id.resetPasswordButton).setOnClickListener(onClickListener);

    }


    @Override
    public void onBackPressed() {

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.logInButton:
                    Log.e("logInUpButton"," Clicked");
                    logIn();
                    break;
                case R.id.signUpButton:
                    //회원가입 페이지로 이동
                    Log.e("signUpButton"," Clicked");
                    startNewActivity(SignUpActivity.class);
                    break;
                case R.id.resetPasswordButton:
                    Log.e("resetPasswordButton"," Clicked");
                    startNewActivity(ResetPasswordActivity.class);
                    break;
            }
        }
    };
    private void logIn(){
        email = ((EditText)findViewById(R.id.emailEditText)).getText().toString();
        password = ((EditText)findViewById(R.id.passwordEditText)).getText().toString();
        if(email.length()>0 && password.length()>0){
            final RelativeLayout loaderLayout = findViewById(R.id.loaderLayout);
            loaderLayout.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    loaderLayout.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        showToast(LogInActivity.this, "로그인에 성공하였습니다.");
                        FirebaseUser user = mAuth.getCurrentUser();
                        startNewActivity(MainActivity.class);
                    } else {
                        // If sign in fails, display a message to the user.
                        if(task.getException()!=null){
                            showToast(LogInActivity.this, task.getException().toString());
                        }
                    }
                }
            });
        }
    }
    private void startNewActivity(Class c){
        Intent intent = new Intent(LogInActivity.this,c);
        if(c==MainActivity.class){
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        startActivity(intent);
    }

}
