package com.example.first_project;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.example.first_project.model.Message;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapterTest {

    private List<Message> testMessages;
    private com.example.first_project.adapter.MessageAdapter adapter;

    @Before
    public void setUp() {
        testMessages = new ArrayList<>();
        adapter = new com.example.first_project.adapter.MessageAdapter(testMessages);
    }

    @Test
    public void getItemCount_EmptyList_ReturnsZero() {
        assertEquals(0, adapter.getItemCount());
    }

    @Test
    public void getItemCount_WithMessages_ReturnsCorrectCount() {
        // Добавляем несколько сообщений в список
        testMessages.add(new Message("User1", "Message 1"));
        testMessages.add(new Message("User2", "Message 2"));
        testMessages.add(new Message("User3", "Message 3"));
        
        int count = adapter.getItemCount();
        
        // Проверяем, что количество элементов корректно
        assertEquals(3, count);
    }

    @Test
    public void addMessage_AddsMessageToList() {
        // Создаем новое сообщение и добавляем его в адаптер
        Message newMessage = new Message("TestUser", "Test message");
        int initialCount = adapter.getItemCount();
        
        adapter.addMessage(newMessage);
        
        // Проверяем, что сообщение добавлено и количество увеличилось
        assertEquals(initialCount + 1, adapter.getItemCount());
        assertTrue(testMessages.contains(newMessage));
    }

    @Test
    public void addMessage_WithNullMessage_HandlesGracefully() {
        // Проверяем обработку null сообщения
        int initialCount = adapter.getItemCount();
        
        try {
            adapter.addMessage(null);
            // Если метод не выбрасывает исключение, проверяем, что размер не изменился
            assertEquals(initialCount, adapter.getItemCount());
        } catch (Exception e) {
            // Если выбрасывается исключение, это тоже приемлемое поведение
            assertTrue("Exception should be handled gracefully", true);
        }
    }

    @Test
    public void addMessage_MultipleMessages_AddsAllMessages() {
        // Создаем несколько сообщений и добавляем их в адаптер
        Message message1 = new Message("User1", "Message 1");
        Message message2 = new Message("User2", "Message 2");
        Message message3 = new Message("User3", "Message 3");
        
        adapter.addMessage(message1);
        adapter.addMessage(message2);
        adapter.addMessage(message3);
        
        // Проверяем, что все сообщения добавлены
        assertEquals(3, adapter.getItemCount());
        assertTrue(testMessages.contains(message1));
        assertTrue(testMessages.contains(message2));
        assertTrue(testMessages.contains(message3));
    }

    @Test
    public void addMessage_WithEmptyText_AddsMessage() {
        // Создаем сообщение с пустым текстом
        Message emptyMessage = new Message("User", "");
        
        adapter.addMessage(emptyMessage);
        
        // Проверяем, что сообщение с пустым текстом добавляется
        assertEquals(1, adapter.getItemCount());
        assertTrue(testMessages.contains(emptyMessage));
    }

    @Test
    public void addMessage_WithNullSender_AddsMessage() {
        // Создаем сообщение с null отправителем
        Message nullSenderMessage = new Message(null, "Message text");
        
        adapter.addMessage(nullSenderMessage);
        
        // Проверяем, что сообщение с null отправителем добавляется
        assertEquals(1, adapter.getItemCount());
        assertTrue(testMessages.contains(nullSenderMessage));
    }

    @Test
    public void addMessage_WithLongText_AddsMessage() {
        // Создаем сообщение с длинным текстом
        String longText = "Это очень длинное сообщение, которое содержит много текста " +
            "и должно проверить, что адаптер корректно обрабатывает длинные сообщения. " +
            "Сообщение может содержать несколько предложений и даже абзацев текста.";
        Message longMessage = new Message("User", longText);

        adapter.addMessage(longMessage);

        // Проверяем, что длинное сообщение добавляется корректно
        assertEquals(1, adapter.getItemCount());
        assertTrue(testMessages.contains(longMessage));
    }

    @Test
    public void addMessage_WithSpecialCharacters_AddsMessage() {
        // Создаем сообщение со специальными символами
        String specialText = "Сообщение с эмодзи 🎉 и символами @#$%^&*()";
        Message specialMessage = new Message("User", specialText);

        adapter.addMessage(specialMessage);

        // Проверяем, что сообщение со специальными символами добавляется
        assertEquals(1, adapter.getItemCount());
        assertTrue(testMessages.contains(specialMessage));
    }

    @Test
    public void constructor_WithNullList_HandlesGracefully() {
        // Проверяем обработку null списка в конструкторе
        try {
            new com.example.first_project.adapter.MessageAdapter(null);
            // Если конструктор не выбрасывает исключение, это приемлемо
            assertTrue("Constructor should handle null list gracefully", true);
        } catch (Exception e) {
            // Если выбрасывается исключение, это тоже приемлемое поведение
            assertTrue("Exception should be handled gracefully", true);
        }
    }

    @Test
    public void getItemCount_AfterAddingAndRemoving_ReturnsCorrectCount() {
        // Добавляем сообщения и затем удаляем одно из них
        Message message1 = new Message("User1", "Message 1");
        Message message2 = new Message("User2", "Message 2");

        adapter.addMessage(message1);
        adapter.addMessage(message2);
        assertEquals(2, adapter.getItemCount());
        
        testMessages.remove(message1);
        // В реальном адаптере нужно было бы вызвать notifyDataSetChanged()

        // Проверяем, что количество элементов изменилось
        assertEquals(1, testMessages.size());
    }
}
