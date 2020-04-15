package com.example.lifeline;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.lifeline.model.Patient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddPatientActivity extends AppCompatActivity {

    private static final String ADD_PATIENT_KEY= "AddPatient";

    private Patient patient;
    
    private DataAccessLayer dal = new LDataAccessLayer(this);

    @BindView(R.id.patient_name_edittext)
    TextInputEditText patientNameText;

    @BindView(R.id.age_edittext)
    TextInputEditText ageText;

    @BindView(R.id.add_patient_button)
    MaterialButton addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.add_patient_button)
    void onAddPatientClick(){
        String name = patientNameText.getText().toString().trim();
        int age = Integer.parseInt(ageText.getText().toString());
        patient = new Patient(name, age, Patient.Status.GOOD, 0, 0);
        dal.createPatient(patient, wrapper -> {
            if(wrapper.success()){
                Intent intent = new Intent();
                intent.putExtra(ADD_PATIENT_KEY, patient);
                setResult(RESULT_OK, intent);
                finish();
            }
            else{
                Toast.makeText(this, "Error creating patient", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
