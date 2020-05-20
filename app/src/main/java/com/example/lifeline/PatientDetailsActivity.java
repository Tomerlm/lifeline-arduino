package com.example.lifeline;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lifeline.model.Connection;
import com.example.lifeline.model.Patient;
import com.example.lifeline.model.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PatientDetailsActivity extends AppCompatActivity {

    final String TAG = PatientDetailsActivity.class.getSimpleName();

    private User user;

    private static final String KEY_PATIENT = "KEY_PATIENT";

    private Patient patient;

    DocumentReference patientDocRef;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private DataAccessLayer dal = new LDataAccessLayer(this);

    private int currentResRate = 12;

    private boolean isManualControllingLifeline = false;

    @BindView(R.id.detail_patients_name)
    MaterialTextView patientNameView;

    @BindView(R.id.detail_bpm_value)
    MaterialTextView bpmValueText;

    @BindView(R.id.detail_oxi_value)
    MaterialTextView oxiValueText;

    @BindView(R.id.detail_patients_status)
    MaterialTextView statusText;

    @BindView(R.id.control_lifeline_button)
    MaterialButton controlLifelineBtn;

    @BindView(R.id.set_completed_button)
    MaterialButton setCompletedBtn;

    @BindView(R.id.lifeline_control_layout)
    LinearLayout controlLayout;

    @BindView(R.id.set_rate_slider)
    AppCompatSeekBar rateSeekbar;

    @BindView(R.id.set_condition_spinner)
    Spinner setConditionSpinner;

    @BindView(R.id.set_condition_label)
    TextView setCondLabel;

    @BindView(R.id.current_rate_text)
    MaterialTextView currentRateText;

    ArrayAdapter<CharSequence> spinnerAdapter;

    private Patient.Status selectedStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_details);
        ButterKnife.bind(this);

        user = dal.getCurrentUser();

        patient = (Patient) getIntent().getSerializableExtra(KEY_PATIENT);

        patientDocRef = db.collection("patients").document(patient.getId());
        patientDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "Current data: " + snapshot.getData());
                    patient = snapshot.toObject(Patient.class).withId(snapshot.getId());
                    updateUI();
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });

        initUI();

        spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.patient_status_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        setConditionSpinner.setAdapter(spinnerAdapter);
        setConditionSpinner.setSelection(getDefaultSpinnerPos(patient.getStatus()),false);
        setConditionSpinner.setOnItemSelectedListener(( new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedStatus = Patient.getStatusFromText((String)parent.getItemAtPosition(position));
                dal.updatePatientStatus(patient, selectedStatus, wrapper -> {return;});
                initUI();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedStatus = patient.getStatus();
            }
        }));


    }

    private void initUI() {
        setCompletedBtn.setVisibility(View.INVISIBLE);
        controlLifelineBtn.setVisibility(View.INVISIBLE);
        controlLayout.setVisibility(View.INVISIBLE);
        setCondLabel.setVisibility(View.INVISIBLE);
        setConditionSpinner.setVisibility(View.INVISIBLE);
        rateSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String s = String.format(Locale.US,"Current rate: %d rpm", progress);
                currentRateText.setText(s);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                currentResRate = seekBar.getProgress();
                Connection connection = new Connection(patient.getArduinoID(),
                        currentResRate,
                        true,
                        patient.getId(),
                        user.getId());
                dal.setManualControl(connection, wrapper -> {
                    return;
                });
            }
        });
        if (user.isEms()) {
            if (patient.getTreatmentStatus() != Patient.TreatmentStatus.COMPLETED) {
                setCompletedBtn.setVisibility(View.VISIBLE);
                setCondLabel.setVisibility(View.VISIBLE);
                setConditionSpinner.setVisibility(View.VISIBLE);
                if (patient.getStatus() == Patient.Status.RESPIRATED) {
                    controlLifelineBtn.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void updateUI() {
        patientNameView.setText(patient.getName());
        bpmValueText.setText(String.format(Locale.ENGLISH,"%d/minute", patient.getBpm()));
        String oxi = String.format(Locale.ENGLISH,"%d", patient.getOxygenPercentage()) + "%";
        oxiValueText.setText(oxi);
        statusText.setText(Patient.getStatusText(patient.getStatus()));
        statusText.setTextColor(getColorByStatus(patient.getStatus()));

    }

    public static Intent patientDetailIntent(Context context, Patient p) {
        return new Intent(context, PatientDetailsActivity.class)
                .putExtra(KEY_PATIENT, p);
    }

    @OnClick(R.id.set_completed_button)
    void setTreatmentStatusCompleted(){
        dal.setTreatmentStatusCompleted(patient, wrapper -> {
            if(wrapper.success()){
                finish();
            }
            else{
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.control_lifeline_button)
    void onControlLifelineClick(){
        Connection connection = new Connection(patient.getArduinoID(),
                rateSeekbar.getProgress(),
                !isManualControllingLifeline,
                patient.getId(),
                user.getId());
        dal.setManualControl(connection , wrapper ->{
            if(wrapper.success()){
                String btnText;
                int layoutVisible;
                if(!isManualControllingLifeline){
                    btnText = "STOP CONTROLLING";
                    layoutVisible = View.VISIBLE;
                }
                else {
                    btnText = "CONTROL LIFELINE";
                    layoutVisible = View.INVISIBLE;

                }
                controlLifelineBtn.setText(btnText);
                controlLayout.setVisibility(layoutVisible);
                isManualControllingLifeline = !isManualControllingLifeline;
                return;
            }
            else {
                Toast.makeText(this, "Error establishing control", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int getDefaultSpinnerPos(Patient.Status status){
        switch(status){
            case GOOD:
                return 0;
            case MILD:
                return 1;
            case CRITICAL:
                return 2;
            case RESPIRATED:
                return 3;
            case UNDETERMINED:
                return 4;
        }
        return 0;
    }

    private int getColorByStatus(Patient.Status status) {
        int color = getResources().getColor(R.color.black);
        switch (status){
            case GOOD:
                color = getResources().getColor(R.color.statusGood);
                break;
            case MILD:
                color = getResources().getColor(R.color.statusMild);
                break;
            case CRITICAL:
                color = getResources().getColor(R.color.statusCritical);
                break;
            case RESPIRATED:
                color = getResources().getColor(R.color.statusRespirated);
                break;
            default:
        }
        return color;
    }
}
