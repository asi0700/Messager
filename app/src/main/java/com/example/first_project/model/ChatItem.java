package com.example.first_project.model;

public class ChatItem {
    private String chatId;
    private final String name;
    private final String subtitle;
    private String imageUrl;
    private long timestamp;

    public ChatItem(String name, String subtitle) {
        this.name = name;
        this.subtitle = subtitle;
        this.timestamp = System.currentTimeMillis();
    }

    public ChatItem(String chatId, String name, String subtitle) {
        this.chatId = chatId;
        this.name = name;
        this.subtitle = subtitle;
        this.timestamp = System.currentTimeMillis();
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getName() {
        return name;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
