package com.example.lifeline;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.email_edittext)
    TextInputEditText emailEditText;

    @BindView(R.id.password_edittext)
    TextInputEditText passEditText;

    @BindView(R.id.login_btn)
    MaterialButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.login_btn)
    void onLoginBtnClicked(){
        //TODO: firebase auth
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        String text = passEditText.getText().toString();
        if(passEditText.getText().toString().equals("1")){
            intent = new Intent(LoginActivity.this, MainActivity.class);
        }
        startActivity(intent);
        finish();


    }

}
