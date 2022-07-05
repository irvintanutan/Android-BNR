package com.novigosolutions.certiscisco_pcsbr.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.novigosolutions.certiscisco_pcsbr.R;
import com.novigosolutions.certiscisco_pcsbr.objects.SecureObject;

import org.w3c.dom.Text;

import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class SecuredUnsealedListAdapter extends RecyclerView.Adapter<SecuredUnsealedListAdapter.MyViewHolder> {
    List<SecureObject> secureObjects;
    Context context = null;
    String colorGreen = "#43A047";
    String colorWhite = "#FFFFFF";
    private int finalCash = 0;
    private AlertDialog finalDialog = null;
    UnsealedListAdapter.UnsealedClickCallback unsealedClickCallback;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_seal;
        LinearLayout llmain;
        Button qty;

        public MyViewHolder(View view) {
            super(view);
            txt_seal = view.findViewById(R.id.txt_seal);
            llmain = view.findViewById(R.id.ll_main);
            qty = view.findViewById(R.id.qty);
            qty.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            checkOut(this.getLayoutPosition());
        }
    }

    public SecuredUnsealedListAdapter(List<SecureObject> secureObjects, Context context) {
        this.secureObjects = secureObjects;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.secure_box_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String type = secureObjects.get(position).Type;


        holder.qty.setText(Integer.toString(secureObjects.get(position).dummyQuantity));

        if (type.equalsIgnoreCase("BOX")) {
            holder.txt_seal.setText("Box " + secureObjects.get(position).Barcode);
        } else {
            holder.txt_seal.setText(type);
        }
        if (secureObjects.get(position).IsScanned) {
            holder.llmain.setBackgroundColor(Color.parseColor(colorGreen));
        } else {
            holder.llmain.setBackgroundColor(Color.parseColor(colorWhite));
        }



    }

    @Override
    public int getItemCount() {
        return secureObjects.size();
    }

    public interface UnsealedClickCallback {
        void onSelect();
    }

    public void setUnsealedClickCallback(UnsealedListAdapter.UnsealedClickCallback unsealedClickCallback) {
        this.unsealedClickCallback = unsealedClickCallback;
    }


    public void checkOut(int position) {

        finalCash = 0;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View alertLayout = inflater.inflate(R.layout.quantity_view, null);


        final TextView qtyText = alertLayout.findViewById(R.id.qtyText);
        final CardView pay1 = alertLayout.findViewById(R.id.pay1);
        final CardView pay5 = alertLayout.findViewById(R.id.pay5);
        final CardView pay10 = alertLayout.findViewById(R.id.pay10);
        final CardView pay20 = alertLayout.findViewById(R.id.pay20);
        final CardView pay50 = alertLayout.findViewById(R.id.pay50);
        final CardView pay100 = alertLayout.findViewById(R.id.pay100);
        final CardView pay200 = alertLayout.findViewById(R.id.pay200);
        final CardView pay500 = alertLayout.findViewById(R.id.pay500);
        final CardView pay1000 = alertLayout.findViewById(R.id.pay1000);
        final CardView pay0 = alertLayout.findViewById(R.id.pay0);


        final CardView checkOut = alertLayout.findViewById(R.id.checkOut);
        final ImageView close = alertLayout.findViewById(R.id.close);


        checkOut.setOnClickListener(v -> {
            SecureObject secureObject = secureObjects.get(position);
            if (secureObject.Quantity == finalCash) {
                secureObject.IsScanned = true;
            } else {
                secureObject.IsScanned = false;
            }
            secureObject.dummyQuantity = finalCash;

            if(unsealedClickCallback!=null) {
                unsealedClickCallback.onSelect();
            }

            notifyDataSetChanged();
            finalDialog.dismiss();
        });

        close.setOnClickListener(view1 -> finalDialog.dismiss());


        pay1.setOnClickListener(view12 -> {
            finalCash *= 10;
            finalCash += 1;
            qtyText.setText(Integer.toString(finalCash));
        });


        pay5.setOnClickListener(view13 -> {
            finalCash *= 10;
            finalCash += 2;
            qtyText.setText(Integer.toString(finalCash));
        });

        pay10.setOnClickListener(view14 -> {
            finalCash *= 10;
            finalCash += 3;
            qtyText.setText(Integer.toString(finalCash));

        });


        pay20.setOnClickListener(view15 -> {
            finalCash *= 10;
            finalCash += 4;
            qtyText.setText(Integer.toString(finalCash));

        });

        pay50.setOnClickListener(view16 -> {
            finalCash *= 10;
            finalCash += 5;
            qtyText.setText(Integer.toString(finalCash));

        });

        pay100.setOnClickListener(view17 -> {
            finalCash *= 10;
            finalCash += 6;
            qtyText.setText(Integer.toString(finalCash));

        });

        pay200.setOnClickListener(view18 -> {
            finalCash *= 10;
            finalCash += 7;
            qtyText.setText(Integer.toString(finalCash));


        });

        pay500.setOnClickListener(view19 -> {
            finalCash *= 10;
            finalCash += 8;
            qtyText.setText(Integer.toString(finalCash));

        });


        pay1000.setOnClickListener(view110 -> {
            finalCash *= 10;
            finalCash += 9;
            qtyText.setText(Integer.toString(finalCash));
        });

        pay0.setOnClickListener(view111 -> {
            finalCash *= 10;
            qtyText.setText(Integer.toString(finalCash));
        });


        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);
        finalDialog = alert.create();
        finalDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        finalDialog.show();

    }
}

