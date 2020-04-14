package com.example.lifeline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;


import com.google.firebase.auth.FirebaseAuth;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;

    private final String TAG = LoginActivity.ExtraData.class.getSimpleName();

    private FirebaseAuth mAuth;

    private LDataAccessLayer dal = new LDataAccessLayer(this);

    @BindView(R.id.email_edittext)
    TextInputEditText emailEditText;

    @BindView(R.id.password_edittext)
    TextInputEditText passEditText;

    @BindView(R.id.login_btn)
    MaterialButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(dal.getCurrentUser() != null){
            startNextActivity();
        }
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        if(dal.getCurrentUser() != null){
            startNextActivity();
        }
    }

    private void startNextActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    private void moveToRegisterActivity() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.login_btn)
    void onLoginBtnClicked(){
        //TODO: firebase auth
        String email = emailEditText.getText().toString();
        String password = passEditText.getText().toString();
        if(email == null || email.equals("") || password == null || password.equals("")){
            Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        dal.login(email, password, wrapper -> {
            if (wrapper.success()) {
                startNextActivity();
            } else {
                Log.e(TAG, wrapper.getException().toString());
//                if(wrapper.getException().getCause().equals("Bad Input")){
//                    Toast.makeText(this, "Input cannot be empty", Toast.LENGTH_SHORT).show();
//                    return;
//                }
                moveToRegisterActivity();
            }
        });
//        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
//        String text = passEditText.getText().toString();
//        if(passEditText.getText().toString().equals("1")){
//            intent = new Intent(LoginActivity.this, MainActivity.class);
//        }
//        startActivity(intent);
//        finish();
    }



}
