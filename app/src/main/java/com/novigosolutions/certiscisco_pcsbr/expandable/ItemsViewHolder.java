package com.novigosolutions.certiscisco_pcsbr.expandable;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.novigosolutions.certiscisco_pcsbr.R;
import com.novigosolutions.certiscisco_pcsbr.interfaces.DialogResult;
import com.novigosolutions.certiscisco_pcsbr.models.Bags;
import com.novigosolutions.certiscisco_pcsbr.models.Box;
import com.novigosolutions.certiscisco_pcsbr.models.BoxBag;
import com.novigosolutions.certiscisco_pcsbr.models.Consignment;
import com.novigosolutions.certiscisco_pcsbr.models.ConsignmentBag;
import com.novigosolutions.certiscisco_pcsbr.models.Envelope;
import com.novigosolutions.certiscisco_pcsbr.models.EnvelopeBag;
import com.novigosolutions.certiscisco_pcsbr.models.Job;
import com.novigosolutions.certiscisco_pcsbr.models.Wagon;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

public class ItemsViewHolder extends ChildViewHolder {

    private TextView head;
    private TextView summary;
    private ImageView delete;
    private Context context;
    private Long id;
    private String itemType;
    DialogResult mDialogResult;

    public ItemsViewHolder(View itemView, Context context) {
        super(itemView);
        this.context = context;
        head = itemView.findViewById(R.id.txt_head);
        summary = itemView.findViewById(R.id.txt_summary);
        delete = itemView.findViewById(R.id.img_delete);
        delete.setOnClickListener(view -> alert());
    }

    public void setItemsDetails(Items item, DialogResult mDialogResult, boolean isCollection) {
        this.mDialogResult = mDialogResult;

        if (!isCollection) {
            delete.setVisibility(View.GONE);
        }

        head.setText(item.getHead());
        summary.setText(item.getSummary());
        id = item.getId();
        itemType = item.head;

    }

    private void alert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Confirm");
        alertDialog.setMessage("Are you sure you want to delete?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                delete();

                if (mDialogResult != null) {
                    mDialogResult.onResult();
                }
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }

    private void delete() {
        if (itemType.equals("Sealed Bag")) {
            Bags.removeSingle(id);
        } else if (itemType.equals("Envelopes")) {
            Envelope.removeSingle(id);
        } else if (itemType.equals("EnvelopeBag")) {
            EnvelopeBag.removeSingle(id);
        } else if (itemType.equals("ConsignmentBag")) {
            ConsignmentBag.removeSingle(id);
        } else if (itemType.equals("Box")) {
            Box.removeSingle(id);
        } else if (itemType.equals("Pallet")) {
            Job.updatePalletCount(Integer.parseInt(String.valueOf(id)), 0);
        } else if (itemType.equals("CoinBox")) {
            BoxBag.removeSingle(id);
        } else if (itemType.equals("Wagon")) {
            Wagon.removeSingle(id);
        }
    }
}
