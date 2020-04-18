package com.example.lifeline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
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

    private LoginViewModel model;

    @BindView(R.id.login_btn)
    MaterialButton loginButton;

    @BindView(R.id.register_btn)
    MaterialButton registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = ViewModelProviders.of(this).get(LoginViewModel.class);
        dal.mLogin(wrapper -> {
            if (wrapper.success()) {
                startNextActivity();
            } else {
                runOnUiThread(() -> {
                    setContentView(R.layout.activity_login);
                    ButterKnife.bind(this);
                });
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        if(dal.getCurrentUser() != null){
            startNextActivity();
        }
    }

//    private void startNextActivity() {
//        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//        startActivity(intent);
//        finish();
//    }
    private void moveToRegisterActivity() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.login_btn)
    void signIn() {
        //progress = ProgressBottomSheet.show(this);
        model.getSignInClient(this).observe(this, googleSignInClient -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }

    private void doLogin(@NonNull GoogleSignInAccount account) {
        //progress = ProgressBottomSheet.show(this);
        model.loginWithGoogle(account).observe(LoginActivity.this, data -> {
            //progress.dismiss();
            dal.mLogin(wrapper -> {
                    moveToRegisterActivity();
            });
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == RC_SIGN_IN) {
            //progress.dismiss();
            GoogleSignIn.getSignedInAccountFromIntent(intent)
                    .addOnSuccessListener(this::doLogin)
                    .addOnFailureListener(e -> {
                        Log.w(TAG, "Google sign in failed", e);
                        if (e instanceof ApiException) {
                            ApiException apiException = (ApiException) e;
                            switch (apiException.getStatusCode()) {
                                case CommonStatusCodes.CANCELED:
                                    Toast.makeText(this, "Google sign in cancelled", Toast.LENGTH_LONG).show();
                                    break;
                                case CommonStatusCodes.INVALID_ACCOUNT:
                                    Toast.makeText(this, "Google sign in failed. Invalid Google account", Toast.LENGTH_LONG).show();
                                    break;
                                case CommonStatusCodes.NETWORK_ERROR:
                                    Toast.makeText(this, "There was a network error while trying to sign in. Please make sure you are connected to the internet", Toast.LENGTH_LONG).show();
                                    break;
                            }
                        }
                    });
        }
    }

    void startNextActivity() {
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        dal.findUserByEmail(email, wrapper -> {
            Intent intent;
            if (wrapper.success()) {
                intent = new Intent(LoginActivity.this, ArduinoidActivity.class);
            } else {
                Log.d(TAG, "Error getting user: " + wrapper.getException());
                intent = new Intent(LoginActivity.this, RegisterActivity.class);
            }

            startActivity(intent);
            finish();
        });
    }



}
