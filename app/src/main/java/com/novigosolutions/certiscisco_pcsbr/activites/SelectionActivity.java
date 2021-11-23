package com.novigosolutions.certiscisco_pcsbr.activites;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.novigosolutions.certiscisco_pcsbr.R;
import com.novigosolutions.certiscisco_pcsbr.adapters.DataAdapter;
import com.novigosolutions.certiscisco_pcsbr.adapters.GridAdapter;
import com.novigosolutions.certiscisco_pcsbr.models.Job;
import com.novigosolutions.certiscisco_pcsbr.models.MenuForm;
import com.novigosolutions.certiscisco_pcsbr.utils.Constants;
import com.novigosolutions.certiscisco_pcsbr.utils.NetworkUtil;
import com.novigosolutions.certiscisco_pcsbr.utils.Preferences;
import com.novigosolutions.certiscisco_pcsbr.webservices.APICaller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class SelectionActivity extends AppCompatActivity {

    private List<MenuForm> form;
    ImageView imgnetwork;
    TextView mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        setuptoolbar();

//        try {
//            Job.UpdateDateFormats();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

        List<Job> jobs = Job.getAllJobs();

        for (Job job  : jobs)  {
            //if (job.OrderNo.equals("")) {
                Log.e("TESTING " , job.OrderNo + " "  + job.ActualFromTime);
           // }
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);


        form = new ArrayList<>();

        form.add(new MenuForm("Delivery", R.drawable.ic_delivery2, "Delivery"));
        form.add(new MenuForm("Collection", R.drawable.ic_collection2, "Collection"));

        RecyclerView.Adapter adapter = new DataAdapter(form, this);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

            });

            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (child != null && gestureDetector.onTouchEvent(e)) {
                    int position = rv.getChildAdapterPosition(child);

                    switch (position) {

                        case 0:
                            delivery();
                            break;
                        case 1:
                            collection();
                            break;
                        default:
                    }

                }

                return false;
            }

            private void collection() {
                Constants.isCollection = true;
                Intent intent = new Intent(SelectionActivity.this, SelectedJobListActivity.class);
                intent.putExtra("isCollection", 1);
                intent.putExtra("isDelivered", 0);
                startActivity(intent);
            }

            private void delivery() {
                Constants.isCollection = false;
                Intent intent = new Intent(SelectionActivity.this, SelectedJobListActivity.class);
                intent.putExtra("isCollection", 0);
                intent.putExtra("isDelivered", 1);
                startActivity(intent);
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(SelectionActivity.this, HomeActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    private void setuptoolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Select Category");
        TextView UserName = (TextView) toolbar.findViewById(R.id.UserName);
        UserName.setText(Preferences.getString("UserName", SelectionActivity.this));
        imgnetwork = (ImageView) toolbar.findViewById(R.id.imgnetwork);
    }
}