package com.novigosolutions.certiscisco_pcsbr.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.novigosolutions.certiscisco_pcsbr.R;
import com.novigosolutions.certiscisco_pcsbr.interfaces.MessageClickListener;
import com.novigosolutions.certiscisco_pcsbr.interfaces.RecyclerViewClickListener;
import com.novigosolutions.certiscisco_pcsbr.models.ChatMessage;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {
    List<ChatMessage> chatMessageList;
    MessageClickListener itemListener;
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        LinearLayout layout, parent_layout;
        TextView msg;
        TextView time;
        ImageView img, img_ack;

        public MyViewHolder(View view) {
            super(view);
            layout = view.findViewById(R.id.bubble_layout);
            parent_layout = view.findViewById(R.id.bubble_layout_parent);
            msg = view.findViewById(R.id.txt_message);
            time = view.findViewById(R.id.txt_time);
            img = view.findViewById(R.id.img);
            img_ack = view.findViewById(R.id.img_ack);
            img_ack.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemListener.messageClicked(chatMessageList.get(this.getLayoutPosition()));
        }
    }

    public ChatAdapter(List<ChatMessage> chatMessageList, MessageClickListener itemListener) {
        this.chatMessageList = chatMessageList;
        this.itemListener = itemListener;
    }


    @Override
    public ChatAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chatbubble, parent, false);
        return new ChatAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ChatAdapter.MyViewHolder holder, int position) {
        ChatMessage message = chatMessageList.get(position);
        holder.msg.setText(message.Message);
        holder.time.setText(message.CreatedDt);

        if (message.IsFromDevice) {
            holder.layout.setBackgroundResource(R.drawable.chat_right);
            holder.parent_layout.setGravity(Gravity.RIGHT);
            holder.msg.setTextColor(Color.WHITE);
            holder.time.setTextColor(Color.WHITE);
            holder.img_ack.setVisibility(View.GONE);
            if(message.MessageId<-1) holder.img.setImageResource(R.drawable.icon_right);
            else holder.img.setImageResource(R.drawable.icon_right_fill);
        }
        // If not mine then align to left
        else {
            holder.parent_layout.setGravity(Gravity.LEFT);
            holder.msg.setTextColor(Color.BLACK);
            holder.time.setTextColor(Color.BLACK);
            if (message.IsRead) {
                holder.layout.setBackgroundResource(R.drawable.chat_left);
                holder.img.setImageResource(R.drawable.icon_right_fill);
                holder.img_ack.setVisibility(View.GONE);
            } else {
                holder.layout.setBackgroundResource(R.drawable.chat_left_ack);
                holder.img.setImageResource(R.drawable.icon_right);
                holder.img_ack.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return chatMessageList.size();
    }
}