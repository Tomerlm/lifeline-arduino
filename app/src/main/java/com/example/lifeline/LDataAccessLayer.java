package com.example.lifeline;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.lifeline.model.BaseModel;
import com.example.lifeline.model.Patient;
import com.example.lifeline.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.ref.WeakReference;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;


public class LDataAccessLayer implements DataAccessLayer {

    private static final String TAG = LDataAccessLayer.class.getSimpleName();

    private static final String COLLECTION_USERS = "users";
    private static final String COLLECTION_PATIENTS = "patients";

    private static FirebaseUser currentUser = null;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private Gson gson = new GsonBuilder().serializeNulls().create();

    private WeakReference<Context> contextRef;

    public LDataAccessLayer(Context context) {
        this.contextRef = new WeakReference<>(context);
    }

    private <T extends BaseModel> DataWrapper<T> toObject(DocumentSnapshot doc, Class<T> clazz) {
        return DataWrapper.with(doc.toObject(clazz).withId(doc.getId()));
    }

    public FirebaseUser getCurrentUser() {
        Context ctx = contextRef.get();
        if (currentUser == null && ctx != null) {
            SharedPreferences prefs = ctx.getSharedPreferences(LDataAccessLayer.class.getSimpleName(), Context.MODE_PRIVATE);
            String userJson = prefs.getString("user", null);
            if (userJson == null) return null;

            currentUser = gson.fromJson(userJson, FirebaseUser.class);
        }
        return currentUser;
    }



    @Override
    public void login(String email, String password, DataCallback<FirebaseUser> callback) {
        Context ctx = contextRef.get();
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) ctx, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            callback.onData(DataWrapper.with(user));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            callback.onData(DataWrapper.exception(task.getException()));
                        }

                        // ...
                    }
                });
    }

    public void findUserByEmail(String email, DataCallback<User> callback){
        db.collection(COLLECTION_PATIENTS)
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if(!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                        callback.onData(toObject(doc, User.class));
                    } else {
                        String exceptionMassage = MessageFormat.format("{0} is not present in db.", email);
                        callback.onData(DataWrapper.exception(new Exception(exceptionMassage)));
                    }
                })
                .addOnFailureListener(e -> {
                    callback.onData(DataWrapper.exception(e));
                });
    }

//    public void sendRegistrationTokenToServer(String token, DataCallback<Void> cb) {
//        final DataCallback<Void> callback = cb == null ? (aVoid) -> {} : cb;
//        if (token == null) {
//            callback.onData(DataWrapper.with(null));
//            return;
//        }
//
//        if (currentUser != null) {
//            db.collection(DOCUMENT_USERS)
//                    .document(currentUser.getId())
//                    .update("registrationToken", token)
//                    .addOnSuccessListener(aVoid -> callback.onData(DataWrapper.with(null)))
//                    .addOnFailureListener(err -> callback.onData(DataWrapper.exception(err)));
//        } else {
//            // Cache the token for when the user logs in
//            callback.onData(DataWrapper.with(null));
//        }
//    }

    @Override
    public void logout(DataCallback<Void> callback) {

    }


    @Override
    public void register(String email, String password, boolean isEms, String username, String hospitalName, DataCallback<FirebaseUser> callback) {
        Context ctx = contextRef.get();
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) ctx, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            currentUser = firebaseAuth.getCurrentUser();
                            callback.onData(DataWrapper.with(currentUser));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            callback.onData(DataWrapper.exception(task.getException()));}
                    }
                });
    }

    @Override
    public void createPatient(Patient patient, DataCallback<Patient> callback) {
        db.collection(COLLECTION_PATIENTS)
                .add(patient)
                .addOnSuccessListener(documentReference -> {
                    documentReference.get().addOnSuccessListener(documentSnapshot -> {
                        callback.onData(toObject(documentSnapshot, Patient.class));

                    }).addOnFailureListener(e -> callback.onData(DataWrapper.exception(e)));

                }).addOnFailureListener(e -> callback.onData(DataWrapper.exception(e)));
    }

    @Override
    public void getAllPatients(DataCallback<List<Patient>> callback) {
        List<Patient> items = new ArrayList<>();
        db.collection(COLLECTION_PATIENTS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Patient patient = document.toObject(Patient.class).withId(document.getId());
                                items.add(patient);
                            }
                            callback.onData(DataWrapper.with(items));
                        } else {
                            Log.d(TAG, "Error getting document: ", task.getException());
                            callback.onData(DataWrapper.exception(task.getException()));
                        }
                    }
                });
    }


}
