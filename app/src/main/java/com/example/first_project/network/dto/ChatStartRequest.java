package com.example.first_project.network.dto;

public class ChatStartRequest {
    public String otherUserId;

    public ChatStartRequest(String otherUserId) {
        this.otherUserId = otherUserId;
    }
}

