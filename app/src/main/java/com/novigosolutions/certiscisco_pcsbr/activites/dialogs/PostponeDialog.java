package com.novigosolutions.certiscisco_pcsbr.activites.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.novigosolutions.certiscisco_pcsbr.R;
import com.novigosolutions.certiscisco_pcsbr.activites.DeliveryActivity;
import com.novigosolutions.certiscisco_pcsbr.interfaces.ApiCallback;
import com.novigosolutions.certiscisco_pcsbr.interfaces.DialogResult;
import com.novigosolutions.certiscisco_pcsbr.models.Branch;
import com.novigosolutions.certiscisco_pcsbr.models.Job;
import com.novigosolutions.certiscisco_pcsbr.models.Reschedule;
import com.novigosolutions.certiscisco_pcsbr.ui.SignatureView;
import com.novigosolutions.certiscisco_pcsbr.utils.CommonMethods;
import com.novigosolutions.certiscisco_pcsbr.utils.NetworkUtil;
import com.novigosolutions.certiscisco_pcsbr.utils.Preferences;
import com.novigosolutions.certiscisco_pcsbr.webservices.APICaller;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class PostponeDialog extends Dialog implements View.OnClickListener, ApiCallback {

    Context context;
    Button btn_rescedule, btn_cancel;
    DialogResult mDialogResult;
//    int PointId;
    String GroupKey;
    View llreasonother;
    //TextView txtDate;
    Spinner spinner;
    EditText edt_reason_other;
    ImageView img_erase;
    SignatureView signatureView;

    public PostponeDialog(Context context, String GroupKey, DialogResult mDialogResult) {
        super(context, R.style.Theme_AppCompat_Light_Dialog);
        this.context = context;
//        this.PointId = PointId;
        this.GroupKey = GroupKey;
        this.mDialogResult = mDialogResult;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.postpone_dialog);
        //lldate = findViewById(R.id.lldate);
        llreasonother = findViewById(R.id.llreasonother);
        edt_reason_other = findViewById(R.id.reason_other);
        btn_rescedule = findViewById(R.id.btn_rescedule);
        btn_cancel = findViewById(R.id.btn_cancel);
        //lldate.setOnClickListener(this);
        btn_rescedule.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        //txtDate = findViewById(R.id.txt_date);
        Calendar tomarrow = Calendar.getInstance();
        Date date = CommonMethods.getLoginDate(Preferences.getString("LoginDate", context));
        if (date != null) tomarrow.setTime(date);
        tomarrow.add(Calendar.DAY_OF_MONTH, 1);
        //txtDate.setText(CommonMethods.getPostPoneStringDate(tomarrow.getTime()));
        List<String> reasons = new ArrayList<>();
        reasons.add("Premise close");
        reasons.add("Customer not around");
        reasons.add("Others, Please specify");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, reasons);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner = findViewById(R.id.spn_reason);
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                if (position == 2) {
                    llreasonother.setVisibility(View.VISIBLE);
                } else {
                    llreasonother.setVisibility(View.GONE);
                    edt_reason_other.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        LinearLayout linearLayout = findViewById(R.id.ll);
        signatureView = new SignatureView(context);
        linearLayout.addView(signatureView);
        img_erase = findViewById(R.id.img_erase);
        img_erase.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_rescedule:
                String sign = signatureView.getByteArray();
//                if (txtDate.getText().toString().isEmpty()) {
//                    Toast.makeText(context, "Select date", Toast.LENGTH_SHORT).show();
//                } else
                if (spinner.getSelectedItemPosition() == 2 && edt_reason_other.getText().toString().isEmpty()) {
                    Toast.makeText(context, "Enter Reason", Toast.LENGTH_SHORT).show();
                } else if (sign == null) {
                    Toast.makeText(context, "Signature is empty", Toast.LENGTH_SHORT).show();
                } else {
                    List<Job> jobs = Job.getPendingDeliveryJobsOfPoint(GroupKey);
                    String TransportIds = "";
                    for (int i = 0; i < jobs.size(); i++) {
                        if (TransportIds.equals(""))
                            TransportIds = String.valueOf(jobs.get(i).TransportMasterId);
                        else
                            TransportIds = TransportIds + "," + jobs.get(i).TransportMasterId;
                    }
                    if (NetworkUtil.getConnectivityStatusString(context)) {
                        ((DeliveryActivity) context).showProgressDialog("Loading...");
                        JsonObject rejsonObject = new JsonObject();
                        rejsonObject.addProperty("TransportId", TransportIds);
                        rejsonObject.addProperty("RescheduleDt", getTomarrowDate());
                        rejsonObject.addProperty("Reason", spinner.getSelectedItemPosition() == 2 ? edt_reason_other.getText().toString() : spinner.getSelectedItem().toString());
                        rejsonObject.addProperty("SignImg", sign);
                        APICaller.instance().RequestForReSchedule(this, context, rejsonObject);
                    } else {
                        Reschedule reschedule = new Reschedule();
//                        reschedule.PointId = PointId;
                        reschedule.TransportId = TransportIds;
                        reschedule.RescheduleDt =getTomarrowDate();
                        reschedule.Reason = spinner.getSelectedItemPosition() == 2 ? edt_reason_other.getText().toString() : spinner.getSelectedItem().toString();
                        reschedule.SignImg = sign;
                        reschedule.GroupKey = GroupKey;
                        reschedule.save();
                        Branch.setAsReschedule(GroupKey);
                        dismiss();
                        if (mDialogResult != null) {
                            mDialogResult.onResult();
                        }
                        Toast.makeText(context, "Requested for reschedule", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
//            case R.id.lldate:
//                showDatePicker();
//                break;
            case R.id.img_erase:
                signatureView.clearSignature();
                break;
            case R.id.btn_cancel:
                dismiss();
                break;
            default:
                break;
        }
    }

    private String getTomarrowDate() {
        Calendar tomarrow = Calendar.getInstance();
        Date date = CommonMethods.getLoginDate(Preferences.getString("LoginDate", context));
        if (date != null) tomarrow.setTime(date);
        tomarrow.add(Calendar.DAY_OF_MONTH, 1);
        return CommonMethods.getPostPoneStringDate(tomarrow.getTime());
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(CommonMethods.getPostPoneDate(txtDate.getText().toString()));
//        DatePickerDialog dialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
//            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                Calendar newDate = Calendar.getInstance();
//                newDate.set(year, monthOfYear, dayOfMonth);
//                txtDate.setText(CommonMethods.getPostPoneStringDate(newDate.getTime()));
//            }
//        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
//        dialog.getDatePicker().setMinDate(tomarrow.getTimeInMillis());
//        dialog.show();
    }

    @Override
    public void onResult(int api_code, int result_code, String result_data) {
        ((DeliveryActivity) context).hideProgressDialog();
        if (result_code == 200) {
            JSONObject obj = null;
            try {
                obj = new JSONObject(result_data);
                if (obj.getString("Result").equals("Success")) {
                    Branch.setAsReschedule(GroupKey);
                    dismiss();
                    if (mDialogResult != null) {
                        mDialogResult.onResult();
                    }
                    Toast.makeText(context, "Requested for reschedule", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            ((DeliveryActivity) context).raiseSnakbar("Error : " + result_code);
        }
    }
}