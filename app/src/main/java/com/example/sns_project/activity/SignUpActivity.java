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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.example.sns_project.Util.showToast;

public class SignUpActivity extends BasicActivity {
    private FirebaseAuth mAuth;
    private final static String TAG = "SignUp";
    private String email, password, passwordCheck;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setToolbarTitle("회원가입");
        mAuth = FirebaseAuth.getInstance();
        findViewById(R.id.signUpButton).setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.signUpButton:
                    Log.e("signUpButton", " Clicked");
                    signUp();
                    onBackPressed();
                    break;
            }
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
    }


    private void signUp() {
        email = ((EditText) findViewById(R.id.emailEditText)).getText().toString();
        password = ((EditText) findViewById(R.id.passwordEditText)).getText().toString();
        passwordCheck = ((EditText) findViewById(R.id.passwordCheckEditText)).getText().toString();

        if (email.length() > 0 && password.length() > 0 && passwordCheck.length() > 0) {
            if (password.equals(passwordCheck)) {
                final RelativeLayout loaderLayout = findViewById(R.id.loaderLayout);
                loaderLayout.setVisibility(View.VISIBLE);
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            showToast(SignUpActivity.this, "회원가입에 성공하였습니다.");
                            FirebaseUser user = mAuth.getCurrentUser();
                            loaderLayout.setVisibility(View.GONE);

//                            startNewActivity(LogInActivity.class);
                            onBackPressed();
//                   updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            if (task.getException() != null) {
                                showToast(SignUpActivity.this, task.getException().toString());
                                loaderLayout.setVisibility(View.GONE);

                            }
//                  updateUI(null);
                        }
                    }
                });
            } else {
                showToast(SignUpActivity.this, "비밀번호가 일치하지 않습니다.");
            }
        } else {
            showToast(SignUpActivity.this, "빈 칸이 있습니다.");
        }
    }
}
