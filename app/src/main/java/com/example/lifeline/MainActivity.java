package com.example.lifeline;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.example.lifeline.model.Patient;
import com.example.lifeline.ui.main.MainFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    int REQUEST_CODE_ADD_PATIENT = 99;
    private static final String ADD_PATIENT_KEY= "AddPatient";

    List<Patient> patients = new ArrayList<>();

    MainFragment mainFragment;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.fab)
    FloatingActionButton addPatientButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        ButterKnife.bind(this);
        progressBar.setVisibility(View.INVISIBLE);
        if (savedInstanceState == null) {
            mainFragment = MainFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, mainFragment)
                .commitNow();
        }
    }

    @OnClick(R.id.fab)
    void onAddPatientClick() {
        Intent intent = new Intent(MainActivity.this, AddPatientActivity.class);
        startActivityForResult(intent, REQUEST_CODE_ADD_PATIENT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_ADD_PATIENT){
            if(resultCode == RESULT_OK){
                //order changed, need update
//                Order changedOrder = (Order) data.getSerializableExtra(ORDER_STATUS_KEY);
//                orders.set(orders.indexOf(changedOrder), changedOrder);
//                adapter.notifyItemChanged(orders.indexOf(changedOrder));
                Patient newPatient = (Patient) data.getSerializableExtra(ADD_PATIENT_KEY);
                patients.add(newPatient);
                if(mainFragment == null){
                    mainFragment = new MainFragment();

                }
                mainFragment.setPatients(patients);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mainFragment).commit();


            }
        }
    }
}
