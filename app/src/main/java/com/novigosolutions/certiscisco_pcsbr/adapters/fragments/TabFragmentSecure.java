package com.novigosolutions.certiscisco_pcsbr.adapters.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.novigosolutions.certiscisco_pcsbr.R;

import java.text.DecimalFormat;

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
    TextView totalAmount;
    SearchView searchView;
    DecimalFormat dec = new DecimalFormat("#,##0.00");


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.secure_tab, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        loadList();

        return view;
    }

    private void loadList() {

    }
}
