package com.example.first_project.model;

public class Message {
    private String messageId;
    private String senderId;
    private String senderName;
    private String text;
    private long timestamp;
    private String chatId;
    private String type;

    public Message(){}

    public Message(String senderId, String senderName, String text,  String chatId) {
        this.senderId = senderId;
        this.senderName = senderName;
        this.text = text;
        this.timestamp = System.currentTimeMillis();
        this.chatId = chatId;
        this.type = "text";
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
