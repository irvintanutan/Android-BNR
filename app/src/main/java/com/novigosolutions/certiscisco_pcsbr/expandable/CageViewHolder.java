package com.novigosolutions.certiscisco_pcsbr.expandable;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.novigosolutions.certiscisco_pcsbr.R;
import com.novigosolutions.certiscisco_pcsbr.interfaces.DialogResult;
import com.novigosolutions.certiscisco_pcsbr.models.Bags;
import com.novigosolutions.certiscisco_pcsbr.models.Box;
import com.novigosolutions.certiscisco_pcsbr.models.BoxBag;
import com.novigosolutions.certiscisco_pcsbr.models.Cage;
import com.novigosolutions.certiscisco_pcsbr.models.Envelope;
import com.novigosolutions.certiscisco_pcsbr.models.EnvelopeBag;
import com.novigosolutions.certiscisco_pcsbr.models.Job;
import com.novigosolutions.certiscisco_pcsbr.models.Wagon;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

public class CageViewHolder extends GroupViewHolder implements View.OnClickListener {

    private TextView cageTitle, head, summary;
    private ImageView arrow, delete;
    private LinearLayout container;
    private Context context;
    DialogResult mDialogResult;
    String cageNo, cageSeal;
    String itemType, barcode;
    Long id;
    boolean hasItems = false;
    boolean isCollection = false;

    public CageViewHolder(View itemView, Context context) {
        super(itemView);
        this.context = context;
        cageTitle = itemView.findViewById(R.id.txtCageTitle);
        arrow = itemView.findViewById(R.id.arrow);
        container = itemView.findViewById(R.id.container);
        head = itemView.findViewById(R.id.txt_head);
        summary = itemView.findViewById(R.id.txt_summary);
        delete = itemView.findViewById(R.id.img_delete);

        delete.setOnClickListener(view -> alert());
    }

    public void setCageTitle(ExpandableGroup group, DialogResult mDialogResult, boolean isCollection) {
        this.mDialogResult = mDialogResult;
        this.isCollection = isCollection;

        if (!isCollection) {
            delete.setVisibility(View.GONE);
        }

        if (group != null) {
            if (group.getItems().isEmpty()) {
                arrow.setVisibility(View.GONE);
                cageTitle.setVisibility(View.GONE);
                container.setVisibility(View.VISIBLE);

                String lines[] = group.getTitle().split("\\r?\\n");

                try {
                    itemType = lines[1];
                    barcode = lines[2];
                    id = Long.parseLong(lines[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                head.setText(itemType);
                summary.setText(barcode);

            } else {
                hasItems = true;
                container.setVisibility(View.GONE);
                arrow.setVisibility(View.VISIBLE);
                cageTitle.setVisibility(View.VISIBLE);
                cageTitle.setText(group.getTitle());

                String split[] = group.getTitle().split("\\r?\\n");

                cageNo = split[1].replace("CAGENO : ", "");
                cageSeal = split[2].replace("CAGESEAL : ", "");
            }
        }
    }


    private void alert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Confirm");
        alertDialog.setMessage("Are you sure you want to delete?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (hasItems) {
                    deleteCage();
                } else
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

    private void deleteCage() {
        Cage.removeByCageNoCageSeal(cageNo, cageSeal);
        Bags.removeByCageNoCageSeal(cageNo, cageSeal);
        Box.removeByCageNoCageSeal(cageNo, cageSeal);
        BoxBag.removeByCageNoCageSeal(cageNo, cageSeal);
        Envelope.removeByCageNoCageSeal(cageNo, cageSeal);
        EnvelopeBag.removeByCageNoCageSeal(cageNo, cageSeal);
        Wagon.removeByCageNoCageSeal(cageNo, cageSeal);
    }

    private void delete() {

        if (itemType.equals("Sealed Bag")) {
            Bags.removeSingle(id);
        } else if (itemType.equals("Envelopes")) {
            Envelope.removeSingle(id);
        } else if (itemType.equals("EnvelopeBag")) {
            EnvelopeBag.removeSingle(id);
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

    @Override
    public void expand() {
        animateExpand();
    }

    @Override
    public void collapse() {
        animateCollapse();
    }

    private void animateExpand() {
        RotateAnimation rotate =
                new RotateAnimation(0, 90, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        arrow.setAnimation(rotate);
    }

    private void animateCollapse() {
        RotateAnimation rotate =
                new RotateAnimation(90, 0, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        arrow.setAnimation(rotate);
    }
}