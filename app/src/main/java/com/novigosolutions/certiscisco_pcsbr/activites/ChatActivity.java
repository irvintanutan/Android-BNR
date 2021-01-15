package com.novigosolutions.certiscisco_pcsbr.activites;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.novigosolutions.certiscisco_pcsbr.R;
import com.novigosolutions.certiscisco_pcsbr.adapters.ChatAdapter;
import com.novigosolutions.certiscisco_pcsbr.interfaces.ApiCallback;
import com.novigosolutions.certiscisco_pcsbr.interfaces.MessageClickListener;
import com.novigosolutions.certiscisco_pcsbr.interfaces.NetworkChangekListener;
import com.novigosolutions.certiscisco_pcsbr.interfaces.RecyclerViewClickListener;
import com.novigosolutions.certiscisco_pcsbr.models.ChatMessage;
import com.novigosolutions.certiscisco_pcsbr.recivers.NetworkChangeReceiver;
import com.novigosolutions.certiscisco_pcsbr.utils.CommonMethods;
import com.novigosolutions.certiscisco_pcsbr.utils.Constants;
import com.novigosolutions.certiscisco_pcsbr.utils.NetworkUtil;
import com.novigosolutions.certiscisco_pcsbr.utils.Preferences;
import com.novigosolutions.certiscisco_pcsbr.webservices.APICaller;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ChatActivity extends BaseActivity implements View.OnClickListener, MessageClickListener, NetworkChangekListener, ApiCallback {
    EditText edt_message;
    List<ChatMessage> chatlist;
    ChatAdapter chatAdapter;
    RecyclerView msgRecyclerView;
    ImageView imgnetwork;
    ChatMessage chatMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setuptoolbar();
        msgRecyclerView = findViewById(R.id.msgRecyclerView);
        if (Preferences.getBoolean("TWOWAY", ChatActivity.this)) {
            View form = findViewById(R.id.form);
            form.setVisibility(View.VISIBLE);
            edt_message = findViewById(R.id.edt_message);
            ImageView sendButton = findViewById(R.id.btn_send);
            sendButton.setOnClickListener(this);
        }
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setStackFromEnd(true);
        msgRecyclerView.setLayoutManager(mLayoutManager);
        msgRecyclerView.setItemAnimator(new DefaultItemAnimator());
        chatlist = ChatMessage.getAllMessage();
        chatAdapter = new ChatAdapter(chatlist, this);
        msgRecyclerView.setAdapter(chatAdapter);
        if (NetworkUtil.getConnectivityStatusString(ChatActivity.this))
            APICaller.instance().GetMessages(this);
    }

    private void setuptoolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Message");
        TextView UserName = (TextView) toolbar.findViewById(R.id.UserName);
        UserName.setText(Preferences.getString("UserName", this));
        imgnetwork = toolbar.findViewById(R.id.imgnetwork);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(msgReceiver,
                new IntentFilter("mesagereciverevent"));
        NetworkChangeReceiver.changekListener = this;
        onNetworkChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(msgReceiver);
    }

    private BroadcastReceiver msgReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refresh();
            msgRecyclerView.smoothScrollToPosition(chatlist.size() - 1);
        }
    };

    private void refresh() {
        List<ChatMessage> tempList = ChatMessage.getAllMessage();
        chatlist.clear();
        chatlist.addAll(tempList);
        chatAdapter.notifyDataSetChanged();
    }

    public void sendTextMessage() {
        if (NetworkUtil.getConnectivityStatusString(ChatActivity.this)) {
            String message = edt_message.getEditableText().toString();
            Log.e("sending:", message);
            if (!message.equalsIgnoreCase("")) {
                ChatMessage chatMessage = new ChatMessage(-2, message, CommonMethods.getServerTimeInFormate(this), true);
                edt_message.setText("");
                chatlist.add(chatMessage);
                chatAdapter.notifyDataSetChanged();
                msgRecyclerView.smoothScrollToPosition(chatlist.size() - 1);
                //if (NetworkUtil.getConnectivityStatusString(this)) {
                    APICaller.instance().sendMessage(this, this, message);
//                } else {
//                    String offlinemsg = Preferences.getString("offlinemessage", this);
//                    if (offlinemsg.isEmpty()) {
//                        Preferences.saveString("offlinemessage", message, this);
//                    } else {
//                        Preferences.saveString("offlinemessage", offlinemsg + "\n" + message, this);
//                    }
//                }
            }
        } else {
            Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                sendTextMessage();
        }
    }

    @Override
    public void onNetworkChanged() {
        if (NetworkUtil.getConnectivityStatusString(this))
            imgnetwork.setImageResource(R.drawable.network);
        else
            imgnetwork.setImageResource(R.drawable.no_network);
    }

    @Override
    public void onResult(int api_code, int result_code, String result_data) {
        try {
            if (result_code == 409) {
                authalert(this);
            } else if (result_code == 200) {
                JSONObject obj = new JSONObject(result_data);
                if (obj.getString("Result").equals("Success")) {
                    if (api_code == Constants.MarkMessageAsRead) {
                        int pos = chatlist.indexOf(chatMessage);
                        chatlist.get(pos).IsRead = true;
                        ChatMessage.setRead(chatMessage.MessageId);
                        chatAdapter.notifyDataSetChanged();
                    } else {
                        chatlist.get(chatlist.size() - 1).MessageId = -1;
                        chatAdapter.notifyDataSetChanged();
                    }
                }
            } else {
                raiseSnakbar("Error");
            }
            hideProgressDialog();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void messageClicked(ChatMessage chatMessage) {
        if (NetworkUtil.getConnectivityStatusString(ChatActivity.this)) {
            showProgressDialog("Loading...");
            this.chatMessage = chatMessage;
            APICaller.instance().MarkMessageAsRead(this, this, chatMessage.MessageId);
        } else {
            raiseInternetSnakbar();
        }
    }
}
