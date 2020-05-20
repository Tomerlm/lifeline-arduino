package com.example.lifeline;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.lifeline.model.BaseModel;
import com.example.lifeline.model.Connection;
import com.example.lifeline.model.Patient;
import com.example.lifeline.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.ref.WeakReference;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.firebase.firestore.Query.Direction;


public class LDataAccessLayer implements DataAccessLayer {

    private static final String TAG = LDataAccessLayer.class.getSimpleName();

    private static final String COLLECTION_USERS = "users";
    private static final String COLLECTION_PATIENTS = "patients";
    private static final String COLLECTION_CONNECTIONS = "connections";

    private static User currentUser = null;

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

    public User getCurrentUser() {
        Context ctx = contextRef.get();
        if (currentUser == null && ctx != null) {
            SharedPreferences prefs = ctx.getSharedPreferences(LDataAccessLayer.class.getSimpleName(), Context.MODE_PRIVATE);
            String userJson = prefs.getString("user", null);
            if (userJson == null) return null;

            currentUser = gson.fromJson(userJson, User.class);
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

    @Override
    public void mLogin(DataCallback<User> callback) {
        if (currentUser != null) {
            callback.onData(DataWrapper.with(currentUser));
            return;
        }
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            findUserByEmail(firebaseUser.getEmail(), wrapper -> {
                if (wrapper.success()) {
                    currentUser = wrapper.get();
                    Context context = contextRef.get();
                    if (context != null) {
                        SharedPreferences prefs = context.getSharedPreferences(LDataAccessLayer.class.getSimpleName(), Context.MODE_PRIVATE);
                        String userJson = gson.toJson(currentUser, User.class);
                        prefs.edit().putString("user", userJson).apply();
                    }
                    FirebaseInstanceId.getInstance()
                            .getInstanceId()
                            .addOnSuccessListener(instanceIdResult -> {
                                String token = instanceIdResult.getToken();
                                sendRegistrationTokenToServer(token, null);
                            });

                    callback.onData(DataWrapper.with(wrapper.get()));
                } else {
                    callback.onData(DataWrapper.exception(wrapper.getException()));
                }
            });
        } else {
            callback.onData(DataWrapper.exception(new Exception("User not logged in with firebase")));
        }
    }

    public void findUserByEmail(String email, DataCallback<User> callback){
        db.collection(COLLECTION_USERS)
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
        firebaseAuth.signOut();
        currentUser = null;
        callback.onData(DataWrapper.with(null));
    }


    @Override
    public void register(FirebaseUser fbUser, boolean isEms, String username, String hospitalName, DataCallback<User> callback) {
        Context ctx = contextRef.get();
        User user = new User(fbUser, isEms, username, hospitalName);
        db.collection(COLLECTION_USERS)
                .add(user)
                .addOnSuccessListener(docRef -> {
                    docRef.get().addOnSuccessListener(doc -> {
                        currentUser = doc.toObject(User.class).withId(doc.getId());
                        callback.onData(DataWrapper.with(currentUser));

                    }).addOnFailureListener(e -> callback.onData(DataWrapper.exception(e)));
                }).addOnFailureListener(e -> {
            callback.onData(DataWrapper.exception(e));
        });
    }

    @Override
    public void createPatient(Patient patient, DataCallback<Patient> callback) {
        db.collection(COLLECTION_PATIENTS)
                .add(patient)
                .addOnSuccessListener(documentReference -> {
                    documentReference.get().addOnSuccessListener(documentSnapshot -> {
                        callback.onData(toObject(documentSnapshot, Patient.class));
                        Connection connection = new Connection(patient.getArduinoID(), 12, false , documentSnapshot.getId(), currentUser.getId());
                        db.collection(COLLECTION_CONNECTIONS).document(patient.getArduinoID()).set(connection);
                    }).addOnFailureListener(e -> callback.onData(DataWrapper.exception(e)));

                }).addOnFailureListener(e -> callback.onData(DataWrapper.exception(e)));
    }

    @Override
    public void getAllPatients(DataCallback<List<Patient>> callback) {
        List<Patient> items = new ArrayList<>();

        db.collection(COLLECTION_PATIENTS).orderBy("treatmentStatus", Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Patient patient = document.toObject(Patient.class).withId(document.getId());
                                boolean cond;
                                if(!currentUser.isEms()){
                                    cond = !patient.getDestHospital().equals(currentUser.getHospitalName());
                                } else {
                                    cond = !patient.getArduinoID().equals(currentUser.getArduinoID());
                                }
                                if(cond){
                                    break;
                                }
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

    @Override
    public void sendRegistrationTokenToServer(String token, DataCallback<Void> cb) {
        final DataCallback<Void> callback = cb == null ? (aVoid) -> {} : cb;
        if (token == null) {
            callback.onData(DataWrapper.with(null));
            return;
        }

        if (currentUser != null) {
            db.collection(COLLECTION_USERS)
                    .document(currentUser.getId())
                    .update("registrationToken", token)
                    .addOnSuccessListener(aVoid -> callback.onData(DataWrapper.with(null)))
                    .addOnFailureListener(err -> callback.onData(DataWrapper.exception(err)));
        } else {
            // Cache the token for when the user logs in
            callback.onData(DataWrapper.with(null));
        }
    }

    @Override
    public void setArduinoID(String arduinoID, DataCallback<User> callback){
        if(currentUser == null){
            Log.e(TAG, "Error no user");
           return;
        }
        currentUser.setArduinoID(arduinoID);
        db.collection(COLLECTION_USERS)
                .document(currentUser.getId())
                .update("arduinoID", arduinoID)
                .addOnSuccessListener(aVoid -> callback.onData(DataWrapper.with(null)))
                .addOnFailureListener(err -> callback.onData(DataWrapper.exception(err)));
    }

    @Override
    public void setTreatmentStatusCompleted(Patient patient, DataCallback<Patient> callback){
        if(currentUser == null){
            Log.e(TAG, "Error no user");
            return;
        }
        patient.setTreatmentStatus(Patient.TreatmentStatus.COMPLETED);
        db.collection(COLLECTION_PATIENTS)
                .document(patient.getId())
                .update("treatmentStatus", "COMPLETED")
                .addOnSuccessListener(aVoid -> callback.onData(DataWrapper.with(patient)))
                .addOnFailureListener(err -> callback.onData(DataWrapper.exception(err)));
    }

    @Override
    public void updatePatientStatus(Patient patient, Patient.Status status, DataCallback<Patient> callback){
        if(currentUser == null){
            Log.e(TAG, "Error no user");
            return;
        }
        patient.setStatus(status);
        db.collection(COLLECTION_PATIENTS)
                .document(patient.getId())
                .update("status", status)
                .addOnSuccessListener(aVoid -> callback.onData(DataWrapper.with(patient)))
                .addOnFailureListener(err -> callback.onData(DataWrapper.exception(err)));
    }

@Override
    public void setManualControl(Connection connection, DataCallback<Patient> callback){
        db.collection(COLLECTION_CONNECTIONS).document(connection.getArduinoID()).set(connection)
                .addOnSuccessListener(aVoid -> callback.onData(DataWrapper.with(null)))
                .addOnFailureListener(err -> callback.onData(DataWrapper.exception(err)));
    }




}
