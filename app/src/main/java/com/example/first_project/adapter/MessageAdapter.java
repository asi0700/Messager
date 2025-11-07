package com.example.first_project.adapter;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
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

        // Определяем тип сообщения и отображаем соответствующий контент
        boolean isImageMessage = "image".equals(message.getType()) && message.getImageUrl() != null && !message.getImageUrl().isEmpty();
        
        if (isImageMessage) {
            // Отображаем изображение
            holder.textMessage.setVisibility(View.GONE);
            holder.imageMessage.setVisibility(View.VISIBLE);
            
            Glide.with(holder.itemView.getContext())
                    .load(message.getImageUrl())
                    .placeholder(R.drawable.message_bubble)
                    .error(R.drawable.message_bubble)
                    .into(holder.imageMessage);
            
            // Если есть текст вместе с изображением, можно показать его под изображением
            if (message.getText() != null && !message.getText().trim().isEmpty()) {
                holder.textMessage.setVisibility(View.VISIBLE);
                holder.textMessage.setText(message.getText());
                holder.textMessage.setBackgroundResource(android.R.color.transparent);
                holder.textMessage.setPadding(8, 4, 8, 4);
            }
        } else {
            // Отображаем текст
            holder.imageMessage.setVisibility(View.GONE);
            holder.textMessage.setVisibility(View.VISIBLE);
            holder.textMessage.setText(message.getText());
        }

        if (message.getTimestamp() > 0) {
            Date date = new Date(message.getTimestamp());
            SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
            
            // Check if message is from today or older
            Date today = new Date();
            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
            
            String timeString;
            if (dateFormatter.format(date).equals(dateFormatter.format(today))) {
                // Message from today, show only time
                timeString = timeFormatter.format(date);
            } else {
                // Message from another day, show date and time
                timeString = dateFormatter.format(date) + " " + timeFormatter.format(date);
            }
            
            holder.textTime.setText(timeString);
            holder.textTime.setVisibility(View.VISIBLE);
        } else {
            holder.textTime.setVisibility(View.GONE);
        }


        if (message.getSenderId().equals(currentUserId)) {

            setupMyMessage(holder, message, isImageMessage);
        } else {

            setupOtherMessage(holder, message, isImageMessage);
        }
    }

    private void setupMyMessage(MessageViewHolder holder, Message message, boolean isImageMessage) {
        ((FrameLayout.LayoutParams) holder.messageBubbleContainer.getLayoutParams()).gravity = Gravity.END;

        if (isImageMessage) {
            holder.imageMessage.setBackgroundResource(R.drawable.message_bubble);
        } else {
            holder.textMessage.setBackgroundResource(R.drawable.message_bubble);
            holder.textMessage.setPadding(12, 8, 12, 8);
        }

        holder.textMessage.setGravity(Gravity.END);
        holder.textTime.setGravity(Gravity.END);

        holder.textSender.setVisibility(View.GONE);
    }

    private void setupOtherMessage(MessageViewHolder holder, Message message, boolean isImageMessage) {

        ((FrameLayout.LayoutParams) holder.messageBubbleContainer.getLayoutParams()).gravity = Gravity.START;

        if (isImageMessage) {
            holder.imageMessage.setBackgroundResource(R.drawable.message_bubble_friend);
        } else {
            holder.textMessage.setBackgroundResource(R.drawable.message_bubble_friend);
            holder.textMessage.setPadding(12, 8, 12, 8);
        }

        holder.textMessage.setGravity(Gravity.START);
        holder.textTime.setGravity(Gravity.START);

        holder.textSender.setVisibility(View.VISIBLE);
        holder.textSender.setText(message.getSenderName());
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
        public ImageView imageMessage;
        public LinearLayout messageBubbleContainer;
        public TextView textTime;


        public MessageViewHolder(View itemView) {
            super(itemView);
            textSender = itemView.findViewById(R.id.textSender);
            textMessage = itemView.findViewById(R.id.textMessage);
            imageMessage = itemView.findViewById(R.id.imageMessage);
            textTime = itemView.findViewById(R.id.textTime);
            messageBubbleContainer = itemView.findViewById(R.id.messageBubbleContainer);
        }

    }

}
