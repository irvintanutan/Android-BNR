package com.novigosolutions.certiscisco_pcsbr.adapters.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.novigosolutions.certiscisco_pcsbr.R;
import com.novigosolutions.certiscisco_pcsbr.activites.ConfirmationActivity;
import com.novigosolutions.certiscisco_pcsbr.activites.DeliveryActivity;
import com.novigosolutions.certiscisco_pcsbr.activites.SecureDetailsActivity;
import com.novigosolutions.certiscisco_pcsbr.adapters.SecureAdapter;
import com.novigosolutions.certiscisco_pcsbr.constant.ClickListener;
import com.novigosolutions.certiscisco_pcsbr.constant.RecyclerTouchListener;
import com.novigosolutions.certiscisco_pcsbr.models.Job;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author irvin
 * @date 2/7/17
 */
public class TabFragmentUnSecure extends Fragment {
    View view;
    LinearLayout nothing;
    RecyclerView recyclerView;
    SecureAdapter adapter;
    FloatingActionButton fab;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.secure_tab, container, false);
        nothing = view.findViewById(R.id.nothing);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(layoutManager);

        loadList();

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void loadList() {
        List<Job> unSecuredJobs = Job.getAllJobsSecureVehicle().stream().filter(job ->(job.IsFloatDeliveryOrder == true && job.finished == false) ||
                (job.IsCollectionOrder == true && job.IsSecured == false)).collect(Collectors.toList());
        if (!unSecuredJobs.isEmpty()) nothing.setVisibility(View.GONE);
        adapter = new SecureAdapter(unSecuredJobs, getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new ClickListener() {

            @Override
            public void onClick(View view, int position) {

                Job job = unSecuredJobs.get(position);

                Intent intent = new Intent(getActivity(), SecureDetailsActivity.class);
                intent.putExtra("TransportMasterId", job.TransportMasterId);
                intent.putExtra("GroupKey", job.GroupKey);
                startActivity(intent);
                getActivity().finish();
            }

            @Override
            public void onLongClick(View view, int position) {

            }


        }));
    }

}
