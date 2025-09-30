package com.example.first_project.model;

public class ChatItem {
    private final String name;
    private final String subtitle;

    public ChatItem(String name, String subtitle) {
        this.name = name;
        this.subtitle = subtitle;
    }


    public String getName() {
        return name;
    }

    public String getSubtitle() {
        return subtitle;
    }
}
