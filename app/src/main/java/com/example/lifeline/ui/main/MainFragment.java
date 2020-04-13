package com.example.lifeline.ui.main;


import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

    private List<Patient> items;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    private MainViewModel mViewModel;

    private final String TAG = "MainFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);
        ButterKnife.bind(this, view);

        patientsRecycler.setAdapter(recyclerAdapter);
        patientsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        items = getItems();

        return view;
    }

    private List<Patient> getItems() {
        List<Patient> list = new ArrayList<>();
        list.add(new Patient("patient 0", 32, "dead", 0, 10));
        list.add(new Patient("patient 1", 32, "on his way", 100, 95));
        list.add(new Patient("patient 2", 32, "delivered ot hospial", 60, 90));
        return list;
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
            holder.statusText.setText(patient.getStatus());
            holder.itemView.setOnClickListener( v -> {
                startActivity(PatientDetailsActivity.patientDetailIntent(getContext(),items.get(position)));
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    private class PatientsViewHolder extends RecyclerView.ViewHolder {
        private TextView patientsNameText;
        private TextView statusText;

        private PatientsViewHolder(View itemView) {
            super(itemView);
            patientsNameText = itemView.findViewById(R.id.patients_name_text);
            statusText = itemView.findViewById(R.id.patients_status_text);
        }
    }

}


