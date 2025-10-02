package com.example.first_project;

import org.junit.Test;
import static org.junit.Assert.*;

import com.example.first_project.model.ChatItem;

public class ChatItemTest {

    @Test
    public void constructor_WithNameAndSubtitle_SetsCorrectValues() {
        // Создаем элемент чата с именем и подзаголовком
        String name = "Test Chat";
        String subtitle = "Last message";
        
        ChatItem chatItem = new ChatItem(name, subtitle);
        
        // Проверяем, что все поля установлены корректно
        assertEquals(name, chatItem.getName());
        assertEquals(subtitle, chatItem.getSubtitle());
    }

    @Test
    public void getName_ReturnsCorrectName() {
        // Создаем элемент чата и проверяем получение имени
        String expectedName = "Family Chat";
        ChatItem chatItem = new ChatItem(expectedName, "Online");
        
        String actualName = chatItem.getName();
        
        // Проверяем, что имя возвращается корректно
        assertEquals(expectedName, actualName);
    }

    @Test
    public void getSubtitle_ReturnsCorrectSubtitle() {
        // Создаем элемент чата и проверяем получение подзаголовка
        String expectedSubtitle = "Active now";
        ChatItem chatItem = new ChatItem("Test User", expectedSubtitle);
        
        String actualSubtitle = chatItem.getSubtitle();
        
        // Проверяем, что подзаголовок возвращается корректно
        assertEquals(expectedSubtitle, actualSubtitle);
    }

    @Test
    public void chatItem_WithNullValues_HandlesCorrectly() {
        // Создаем элемент чата с null значениями
        ChatItem chatItem = new ChatItem(null, null);
        
        // Проверяем, что null значения обрабатываются корректно
        assertNull(chatItem.getName());
        assertNull(chatItem.getSubtitle());
    }

    @Test
    public void chatItem_WithEmptyStrings_HandlesCorrectly() {
        // Создаем элемент чата с пустыми строками
        ChatItem chatItem = new ChatItem("", "");
        
        // Проверяем, что пустые строки обрабатываются корректно
        assertEquals("", chatItem.getName());
        assertEquals("", chatItem.getSubtitle());
    }

    @Test
    public void chatItem_WithLongStrings_HandlesCorrectly() {
        // Создаем элемент чата с длинными строками
        String longName = "This is a very long chat name that might be used in the application";
        String longSubtitle = "This is a very long subtitle that contains a lot of text and might be used to show the last message";
        
        ChatItem chatItem = new ChatItem(longName, longSubtitle);
        
        // Проверяем, что длинные строки обрабатываются корректно
        assertEquals(longName, chatItem.getName());
        assertEquals(longSubtitle, chatItem.getSubtitle());
    }

    @Test
    public void chatItem_WithSpecialCharacters_HandlesCorrectly() {
        // Создаем элемент чата со специальными символами
        String nameWithSpecialChars = "Chat with émojis 🎉 and symbols @#$%";
        String subtitleWithSpecialChars = "Message with unicode: Привет! 🌟";
        
        ChatItem chatItem = new ChatItem(nameWithSpecialChars, subtitleWithSpecialChars);
        
        // Проверяем, что специальные символы обрабатываются корректно
        assertEquals(nameWithSpecialChars, chatItem.getName());
        assertEquals(subtitleWithSpecialChars, chatItem.getSubtitle());
    }
}
