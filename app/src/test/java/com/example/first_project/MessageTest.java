package com.example.first_project;

import org.junit.Test;
import static org.junit.Assert.*;

import com.example.first_project.model.Message;

public class MessageTest {

    @Test
    public void constructor_WithSenderAndText_SetsCorrectValues() {
        // Создаем сообщение с отправителем и текстом
        String sender = "TestUser";
        String text = "Test message";
        
        Message message = new Message(sender, text);
        
        // Проверяем, что все поля установлены корректно
        assertEquals(sender, message.getSender());
        assertEquals(text, message.getText());
        assertTrue(message.getTimestamp() > 0);
    }

    @Test
    public void constructor_DefaultConstructor_SetsNullValues() {
        // Создаем сообщение через конструктор по умолчанию
        Message message = new Message();
        
        // Проверяем, что все поля имеют значения по умолчанию
        assertNull(message.getSender());
        assertNull(message.getText());
        assertEquals(0, message.getTimestamp());
    }

    @Test
    public void setSender_ValidSender_SetsCorrectValue() {
        // Создаем сообщение и устанавливаем отправителя
        Message message = new Message();
        String sender = "NewSender";
        
        message.setSender(sender);
        
        // Проверяем, что отправитель установлен корректно
        assertEquals(sender, message.getSender());
    }

    @Test
    public void setText_ValidText_SetsCorrectValue() {
        // Создаем сообщение и устанавливаем текст
        Message message = new Message();
        String text = "New message text";
        
        message.setText(text);
        
        // Проверяем, что текст установлен корректно
        assertEquals(text, message.getText());
    }

    @Test
    public void setTimestamp_ValidTimestamp_SetsCorrectValue() {
        // Создаем сообщение и устанавливаем временную метку
        Message message = new Message();
        long timestamp = 1234567890L;
        
        message.setTimestamp(timestamp);
        
        // Проверяем, что временная метка установлена корректно
        assertEquals(timestamp, message.getTimestamp());
    }

    @Test
    public void timestamp_ConstructorWithSenderAndText_SetsCurrentTime() {
        // Проверяем, что временная метка устанавливается автоматически
        long beforeCreation = System.currentTimeMillis();
        
        Message message = new Message("TestUser", "Test message");
        long afterCreation = System.currentTimeMillis();
        
        // Проверяем, что временная метка находится в правильном диапазоне
        assertTrue(message.getTimestamp() >= beforeCreation);
        assertTrue(message.getTimestamp() <= afterCreation);
    }

    @Test
    public void message_WithNullValues_HandlesCorrectly() {
        // Создаем сообщение с null значениями
        Message message = new Message(null, null);
        
        // Проверяем, что null значения обрабатываются корректно
        assertNull(message.getSender());
        assertNull(message.getText());
        assertTrue(message.getTimestamp() > 0);
    }

    @Test
    public void message_WithEmptyStrings_HandlesCorrectly() {
        // Создаем сообщение с пустыми строками
        Message message = new Message("", "");
        
        // Проверяем, что пустые строки обрабатываются корректно
        assertEquals("", message.getSender());
        assertEquals("", message.getText());
        assertTrue(message.getTimestamp() > 0);
    }
}
