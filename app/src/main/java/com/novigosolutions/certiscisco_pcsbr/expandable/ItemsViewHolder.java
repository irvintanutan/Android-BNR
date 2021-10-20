package com.novigosolutions.certiscisco_pcsbr.expandable;

import android.view.View;
import android.widget.TextView;

import com.novigosolutions.certiscisco_pcsbr.R;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

public class ItemsViewHolder extends ChildViewHolder {

    private TextView head;
    private TextView summary;

    public ItemsViewHolder(View itemView) {
        super(itemView);
        head = itemView.findViewById(R.id.txt_head);
        summary = itemView.findViewById(R.id.txt_summary);
    }

    public void setItemsDetails(Items item) {
        head.setText(item.getHead());
        summary.setText(item.getSummary());
    }
}
