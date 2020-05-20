package com.example.lifeline.ui.main;


import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lifeline.PatientDetailsActivity;
import com.example.lifeline.R;
import com.example.lifeline.model.Patient;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainFragment extends Fragment {

    @BindView(R.id.patients_recycler)
    RecyclerView patientsRecycler;


    private RecyclerView.Adapter recyclerAdapter = new PatientsRecyclerAdapter();

    private List<Patient> items = new ArrayList<>();

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    private MainViewModel mViewModel;

    private final String TAG = "MainFragment";

    private LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);
        ButterKnife.bind(this, view);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(patientsRecycler.getContext(),
                layoutManager.getOrientation());
        patientsRecycler.addItemDecoration(dividerItemDecoration);
        patientsRecycler.setAdapter(recyclerAdapter);
        patientsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

       // items = getItems();

        return view;
    }

    private List<Patient> getItems() {
        List<Patient> list = new ArrayList<>();
        //list.add(new Patient("patient 0", 32, "dead", 0, 10));
        //list.add(new Patient("patient 1", 32, "on his way", 100, 95));
        //list.add(new Patient("patient 2", 32, "delivered ot hospial", 60, 90));
        return list;
    }

    public void setPatients(List<Patient> items) {
        this.items = items;
        //pullToRefresh.setRefreshing(false);
        recyclerAdapter.notifyDataSetChanged();

//        if (items.isEmpty()) {
//            showEmptyState();
//        } else {
//            hideEmptyState();
//        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        // TODO: Use the ViewModel
    }

    private class PatientsRecyclerAdapter extends RecyclerView.Adapter<PatientsViewHolder> {

        @NonNull
        @Override
        public PatientsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.patients_recycler_viewholder, parent, false);
            return new PatientsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PatientsViewHolder holder, final int position) {
            Patient patient = items.get(position);

            holder.patientsNameText.setText(patient.getName());
            holder.statusText.setText(Patient.getStatusText(patient.getStatus()));
            holder.statusText.setTextColor(getColorByStatus(patient.getStatus()));
            holder.treatmentStatusText.setText(Patient.getTreatmentStatusText(patient.getTreatmentStatus()));
            holder.treatmentStatusText.setTextColor(getColorByTreatmentStatus(patient.getTreatmentStatus()));
            holder.lifelineIdText.setText(patient.getArduinoID());
            holder.itemView.setOnClickListener( v -> {
                startActivity(PatientDetailsActivity.patientDetailIntent(getContext(),items.get(position)));
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
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

    private int getColorByTreatmentStatus (Patient.TreatmentStatus status) {
        int color = getResources().getColor(R.color.black);
        switch (status){
            case ONGOING:
                color = getResources().getColor(R.color.statusCritical);
                break;
            case COMPLETED:
                color = getResources().getColor(R.color.statusGood);
                break;
            default:
        }
        return color;
    }

    private class PatientsViewHolder extends RecyclerView.ViewHolder {
        private TextView patientsNameText;
        private TextView statusText;
        private TextView treatmentStatusText;
        private TextView lifelineIdText;

        private PatientsViewHolder(View itemView) {
            super(itemView);
            patientsNameText = itemView.findViewById(R.id.patients_name_text);
            statusText = itemView.findViewById(R.id.patients_status_text);
            treatmentStatusText = itemView.findViewById(R.id.patients_treatment_status_text);
            lifelineIdText =  itemView.findViewById(R.id.patients_arduinoid_value_text);
        }
    }

}


