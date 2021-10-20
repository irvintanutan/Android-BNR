package com.novigosolutions.certiscisco_pcsbr.expandable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.novigosolutions.certiscisco_pcsbr.R;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class CageAdapter extends ExpandableRecyclerViewAdapter<CageViewHolder, ItemsViewHolder> {

    public CageAdapter(List<? extends ExpandableGroup> groups) {
        super(groups);
    }

    @Override
    public CageViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expandable_cage, parent, false);
        return new CageViewHolder(view);
    }

    @Override
    public ItemsViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expandable_items, parent, false);
        return new ItemsViewHolder(view);
    }

    @Override
    public void onBindChildViewHolder(ItemsViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {
        final Items item = (Items) group.getItems().get(childIndex);
        holder.setItemsDetails(item);
    }

    @Override
    public void onBindGroupViewHolder(CageViewHolder holder, int flatPosition, ExpandableGroup group) {
        final Cage cage = (Cage) group;
        holder.setCageTitle(cage);
    }
}
