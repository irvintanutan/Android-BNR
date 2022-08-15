package com.novigosolutions.certiscisco_pcsbr.adapters.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.novigosolutions.certiscisco_pcsbr.R;
import com.novigosolutions.certiscisco_pcsbr.activites.SecureDetailsActivity;
import com.novigosolutions.certiscisco_pcsbr.activites.SelectedJobListActivity;
import com.novigosolutions.certiscisco_pcsbr.activites.SummaryActivity;
import com.novigosolutions.certiscisco_pcsbr.adapters.SecureAdapter;
import com.novigosolutions.certiscisco_pcsbr.constant.ClickListener;
import com.novigosolutions.certiscisco_pcsbr.constant.RecyclerTouchListener;
import com.novigosolutions.certiscisco_pcsbr.models.Job;

import java.text.DecimalFormat;
import java.util.List;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 *
 * @author irvin
 * @date 2/7/17
 */
public class TabFragmentSecure extends Fragment {
    View view;
    RecyclerView recyclerView;
    LinearLayout nothing;
    DecimalFormat dec = new DecimalFormat("#,##0.00");
    SecureAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.secure_tab, container, false);
        nothing = view.findViewById(R.id.nothing);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        loadList();

        return view;
    }

    private void loadList() {
        List<Job> unSecuredJobs = Job.getSecuredJobs();
        if (!unSecuredJobs.isEmpty()) nothing.setVisibility(View.GONE);
        adapter = new SecureAdapter(unSecuredJobs, getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new ClickListener() {

            @Override
            public void onClick(View view, int position) {

                Job job  = unSecuredJobs.get(position);

                Intent intent = new Intent(getContext(), SummaryActivity.class);

                intent.putExtra("TransportMasterId", job.TransportMasterId);
                intent.putExtra("GroupKey", job.GroupKey);
                intent.putExtra("summaryType", 1);
                intent.putExtra("isDelivery", 0);
                intent.putExtra("isCollection", 1);
                intent.putExtra("isSummary", true);
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }


        }));
    }
}
