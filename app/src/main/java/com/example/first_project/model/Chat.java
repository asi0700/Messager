package com.example.first_project.model;

import java.util.List;
import java.util.Map;

public class Chat {

    private String chatId;
    private List<String> participants; // эт список userId участ.
    private Map<String, String> participantNames;
    private String lastMessage;
    private String lastMessageSender;
    private long lastMessagetime;
    private long createdTime;
    private String chatType;

    public Chat() {}

    public Chat(String chatId, List<String> participants, Map<String, String> participantNames) {
        this.chatId = chatId;
        this.participants = participants;
        this.participantNames = participantNames;
        this.lastMessage = "Чат создан";
        this.lastMessagetime = System.currentTimeMillis();
        this.createdTime = System.currentTimeMillis();
        this.chatType = "private";
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    public Map<String, String> getParticipantNames() {
        return participantNames;
    }

    public void setParticipantNames(Map<String, String> participantNames) {
        this.participantNames = participantNames;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastMessageSender() {
        return lastMessageSender;
    }

    public void setLastMessageSender(String lastMessageSender) {
        this.lastMessageSender = lastMessageSender;
    }

    public long getLastMessagetime() {
        return lastMessagetime;
    }

    public void setLastMessagetime(long lastMessagetime) {
        this.lastMessagetime = lastMessagetime;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public String getChatType() {
        return chatType;
    }

    public void setChatType(String chatType) {
        this.chatType = chatType;
    }
}
