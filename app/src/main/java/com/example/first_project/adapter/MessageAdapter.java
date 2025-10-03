package com.example.first_project.adapter;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.first_project.R;
import com.example.first_project.model.Message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> messages;
    private String currentUserId;

    public MessageAdapter(List<Message> messages, String currentUserId) {
        this.messages = messages;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return  new MessageViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position){

        Message message = messages.get(position);

        holder.textMessage.setText(message.getText());

        if (message.getTimestamp() > 0) {
            Date date = new Date(message.getTimestamp());
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String timeString = formatter.format(date);
            holder.textTime.setText(timeString);
        }


        if (message.getSenderId().equals(currentUserId)) {

            setupMyMessage(holder, message);
        } else {

            setupOtherMessage(holder, message);
        }
    }

    private void setupMyMessage(MessageViewHolder holder, Message message) {
        ((FrameLayout.LayoutParams) holder.messageBubbleContainer.getLayoutParams()).gravity = Gravity.END;


        holder.textMessage.setBackgroundResource(R.drawable.message_bubble);

        holder.textMessage.setGravity(Gravity.END);
        holder.textTime.setGravity(Gravity.END);

        holder.textSender.setVisibility(View.GONE);

        holder.textMessage.setPadding(12, 8, 12, 8);
    }

    private void setupOtherMessage(MessageViewHolder holder, Message message) {

        ((FrameLayout.LayoutParams) holder.messageBubbleContainer.getLayoutParams()).gravity = Gravity.START;

        holder.textMessage.setBackgroundResource(R.drawable.message_bubble_friend);

        holder.textMessage.setGravity(Gravity.START);
        holder.textTime.setGravity(Gravity.START);

        holder.textSender.setVisibility(View.VISIBLE);
        holder.textSender.setText(message.getSenderName());

        holder.textMessage.setPadding(12, 8, 12, 8);
    }

    public void  addMessage(Message message) {
        messages.add(message);
        notifyItemInserted(messages.size() -1);
    }


    @Override
    public int getItemCount() {
        return messages.size();
    }


    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView textSender;
        public TextView textMessage;
        public LinearLayout messageBubbleContainer;
        public TextView textTime;


        public MessageViewHolder(View itemView) {
            super(itemView);
            textSender = itemView.findViewById(R.id.textSender);
            textMessage = itemView.findViewById(R.id.textMessage);
            textTime = itemView.findViewById(R.id.textTime);
            messageBubbleContainer = itemView.findViewById(R.id.messageBubbleContainer);
        }

    }

}
