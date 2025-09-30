package com.example.first_project.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.first_project.R;
import com.example.first_project.model.ChatItem;

import java.util.List;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ChatVH> {
    public interface OnChatClick{
        void onClick(ChatItem item);
    }
    private final List<ChatItem> items;
    private final OnChatClick onClick;

    public ChatsAdapter(List<ChatItem> items, OnChatClick onClick) {
        this.items = items;
        this.onClick = onClick;
    }

    @NonNull
    @Override
    public ChatVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ChatVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatVH h, int position) {
        ChatItem it = items.get(position);
        h.textName.setText(it.getName());
        h.textSubtitle.setText(it.getSubtitle());
        h.itemView.setOnClickListener(v -> onClick.onClick(it));

    }

    @Override
    public int getItemCount(){ return  items.size();}
    static class ChatVH extends RecyclerView.ViewHolder{
        final ImageView imageAvatar;
        final TextView textName;
        final TextView textSubtitle;
        ChatVH(@NonNull View itemView) {
            super(itemView);
            imageAvatar = itemView.findViewById(R.id.imageAvatar);
            textName = itemView.findViewById(R.id.textName);
            textSubtitle = itemView.findViewById(R.id.textSubtitle);
        }
    }


}
