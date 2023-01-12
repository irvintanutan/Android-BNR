package com.novigosolutions.certiscisco_pcsbr.activites.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.novigosolutions.certiscisco_pcsbr.R;
import com.novigosolutions.certiscisco_pcsbr.constant.UserLog;
import com.novigosolutions.certiscisco_pcsbr.interfaces.DialogResult;
import com.novigosolutions.certiscisco_pcsbr.models.Job;
import com.novigosolutions.certiscisco_pcsbr.service.UserLogService;


public class PalletDialog extends Dialog implements View.OnClickListener {

    Context context;
    Button btn_done, btn_cancel;
    EditText edt_qty;

    DialogResult mDialogResult;
    int TransportMasterId;

    public PalletDialog(Context context, int TransportMasterId, DialogResult mDialogResult) {
        super(context, R.style.Theme_AppCompat_Light_Dialog);
        this.context = context;
        this.TransportMasterId = TransportMasterId;
        this.mDialogResult = mDialogResult;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.pallet_dialog);
        btn_done = findViewById(R.id.btn_done);
        btn_cancel = findViewById(R.id.btn_cancel);
        edt_qty = findViewById(R.id.edt_qty);
        btn_done.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        int palletCount = Job.getSingle(TransportMasterId).palletCount;
        if (palletCount > 0)
            edt_qty.setText(String.valueOf(palletCount));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_done:
                if (edt_qty.length() > 0) {
                    UserLogService.save(UserLog.COLLECTION.toString(), "QUANTITY " + edt_qty.getText().toString(), "PALLET", context);

                    Job.updatePalletCount(TransportMasterId, Integer.parseInt(edt_qty.getText().toString()));
                    if (mDialogResult != null) {
                        mDialogResult.onResult();
                    }
                    dismiss();
                } else {
                    Toast.makeText(context, "Enter quantity", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_cancel:
                dismiss();
                break;
            default:
                break;
        }
    }
}