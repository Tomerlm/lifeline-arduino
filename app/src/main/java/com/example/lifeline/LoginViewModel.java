package com.example.lifeline;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.example.lifeline.DataWrapper;
import com.example.lifeline.R;
import com.example.lifeline.DataAccessLayer;
import com.example.lifeline.LDataAccessLayer;

public class LoginViewModel extends ViewModel {

    private static final String TAG = LoginViewModel.class.getSimpleName();

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private DataAccessLayer dal = new LDataAccessLayer(null);

    private MutableLiveData<GoogleSignInClient> googleSignInClientLiveData;
    private MutableLiveData<DataWrapper<FirebaseUser>> firebaseUserLiveData;

    LiveData<GoogleSignInClient> getSignInClient(@NonNull Context context) {
        if (googleSignInClientLiveData == null) {
            googleSignInClientLiveData = new MutableLiveData<>();
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(context.getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            googleSignInClientLiveData.setValue(GoogleSignIn.getClient(context, gso));
        }

        return googleSignInClientLiveData;
    }

    LiveData<DataWrapper<FirebaseUser>> loginWithGoogle(GoogleSignInAccount googleSignInAccount) {
        if (firebaseUserLiveData == null) {
            firebaseUserLiveData = new MutableLiveData<>();
            AuthCredential credential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
            firebaseAuthenticate(credential);
        }

        return firebaseUserLiveData;
    }

    private void firebaseAuthenticate(AuthCredential authCredential) {
        firebaseAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        firebaseUserLiveData.setValue(DataWrapper.with(user));
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        firebaseUserLiveData.setValue(DataWrapper.exception(task.getException()));
                    }
                });
    }

}
