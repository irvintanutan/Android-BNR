package com.novigosolutions.certiscisco_pcsbr.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.novigosolutions.certiscisco_pcsbr.R;
import com.novigosolutions.certiscisco_pcsbr.models.Bags;
import com.novigosolutions.certiscisco_pcsbr.models.Box;
import com.novigosolutions.certiscisco_pcsbr.models.BoxBag;
import com.novigosolutions.certiscisco_pcsbr.models.Envelope;
import com.novigosolutions.certiscisco_pcsbr.models.EnvelopeBag;
import com.novigosolutions.certiscisco_pcsbr.models.Job;
import com.novigosolutions.certiscisco_pcsbr.objects.Summary;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class CollectionSummaryAdapter extends RecyclerView.Adapter<CollectionSummaryAdapter.MyViewHolder> {
    List<Summary> collectionSummaries;
    Boolean isSummaryScreen;
    Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_head, txt_summary;
        ImageView img_delete;

        public MyViewHolder(View view) {
            super(view);
            txt_head = view.findViewById(R.id.txt_head);
            txt_summary = view.findViewById(R.id.txt_summary);
            img_delete = view.findViewById(R.id.img_delete);
            img_delete.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            alert(this.getLayoutPosition());
        }
    }

    public CollectionSummaryAdapter(List<Summary> collectionSummaries, Boolean isSummaryScreen, Context context) {
        this.collectionSummaries = collectionSummaries;
        this.isSummaryScreen = isSummaryScreen;
        this.context=context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.summary_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.txt_head.setText(collectionSummaries.get(position).Head);
        holder.txt_summary.setText(collectionSummaries.get(position).Message);
        if (isSummaryScreen) holder.img_delete.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return collectionSummaries.size();
    }

    private void alert(final int pos) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Confirm");
        alertDialog.setMessage("Are you sure you want to delete?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                delete(pos);
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }

    private void delete(int pos) {
        if (collectionSummaries.get(pos).Collection_type.equals("Sealed Bag")) {
            Bags.removeSingle(collectionSummaries.get(pos).id);
            collectionSummaries.remove(pos);
            notifyDataSetChanged();
        } else if (collectionSummaries.get(pos).Collection_type.equals("Envelopes")) {
            Envelope.removeSingle(collectionSummaries.get(pos).id);
            collectionSummaries.remove(pos);
            notifyDataSetChanged();
        } else if (collectionSummaries.get(pos).Collection_type.equals("EnvelopeBag")) {
            Envelope.removeByBagid(EnvelopeBag.getById(collectionSummaries.get(pos).id).getId());
            EnvelopeBag.removeSingle(collectionSummaries.get(pos).id);
            collectionSummaries.remove(pos);
            notifyDataSetChanged();
        } else if (collectionSummaries.get(pos).Collection_type.equals("Box")) {
            Box.removeSingle(collectionSummaries.get(pos).id);
            collectionSummaries.remove(pos);
            notifyDataSetChanged();
        } else if (collectionSummaries.get(pos).Collection_type.equals("Pallet")) {
            Job.updatePalletCount((int) collectionSummaries.get(pos).id, 0);
            collectionSummaries.remove(pos);
            notifyDataSetChanged();
        } else if (collectionSummaries.get(pos).Collection_type.equals("CoinBox")) {
            BoxBag.removeSingle(collectionSummaries.get(pos).id);
            collectionSummaries.remove(pos);
            notifyDataSetChanged();
        }
    }
}