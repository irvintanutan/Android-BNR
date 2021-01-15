package com.novigosolutions.certiscisco_pcsbr.activites;

import android.app.Dialog;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.novigosolutions.certiscisco_pcsbr.R;
import com.novigosolutions.certiscisco_pcsbr.interfaces.ApiCallback;
import com.novigosolutions.certiscisco_pcsbr.models.Break;
import com.novigosolutions.certiscisco_pcsbr.utils.CommonMethods;
import com.novigosolutions.certiscisco_pcsbr.webservices.APICaller;

public class BreakActivity extends BaseActivity{
    TextView txt_from_to, txt_Time, txt_remarks, txt_duration;
    Handler handler;
    Runnable runnable;
    long duration;
    int BreakId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_break);
        txt_from_to = findViewById(R.id.txt_from_to);
        txt_Time = findViewById(R.id.txt_Time);
        txt_remarks = findViewById(R.id.txt_remarks);
        txt_duration = findViewById(R.id.txt_duration);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            BreakId = extras.getInt("BreakId");
            Log.e("BreakId", ":" + BreakId);
            Break aBreak = Break.getSingle(BreakId);
            txt_from_to.setText(CommonMethods.getHourMinitue(aBreak.StartTime) + " to " + CommonMethods.getHourMinitue(aBreak.EndTime));
            txt_remarks.setText(aBreak.Remarks);
            txt_duration.setText(aBreak.Duration);
            duration = (CommonMethods.getBreakTime(aBreak.EndTime).getTime() - CommonMethods.getServerTimeInms(this)) / 1000;
            handler = new Handler();
            runnable = new Runnable() {
                public void run() {
                    txt_Time.setText(String.format("%d:%02d", duration / 60, duration % 60));
                    duration--;
                    if (duration < 0) {
                        handler.removeCallbacks(runnable);
                        Break.setCompleted(BreakId);
                        finish();
                    }
                    handler.postDelayed(this, 1000);
                }
            };
            handler.postDelayed(runnable, 0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
        handler = null;
        runnable = null;
    }

    public void takeabreakdialogue() {
        int breaktime = 4545;
        final TextView txttime;
        // Bitmap image = BlurBuilder.blur(getApplication().getWindow().getDecorView());
        final Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.take_a_break);
        Window window = dialog.getWindow();
        txttime = window.findViewById(R.id.timer);
        WindowManager.LayoutParams wlp = window.getAttributes();
        //window.setBackgroundDrawable(new BitmapDrawable(getResources(), image));
        wlp.gravity = Gravity.CENTER;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        dialog.show();

        final int interval = 1000; // 1 Second
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
//                breaktime--;
//                if (breaktime < 1)
//                    dialog.dismiss();
//                else
//                    txttime.setText(String.format("%d:%d", breaktime / 60, breaktime % 60));
//                handler.postDelayed(this, interval);
            }
        };
        txttime.setText(String.format("%d:%d", breaktime / 60, breaktime % 60));
        handler.postAtTime(runnable, System.currentTimeMillis() + interval);
        handler.postDelayed(runnable, interval);
    }

    @Override
    public void onBackPressed() {
    }
}
