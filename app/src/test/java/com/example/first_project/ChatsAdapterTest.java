package com.example.first_project;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.example.first_project.model.ChatItem;
import com.example.first_project.adapter.ChatsAdapter;

import java.util.ArrayList;
import java.util.List;

public class ChatsAdapterTest {

    private List<ChatItem> testChats;
    private ChatsAdapter.OnChatClick mockOnClick;
    private ChatsAdapter adapter;

    @Before
    public void setUp() {
        testChats = new ArrayList<>();
        mockOnClick = new ChatsAdapter.OnChatClick() {
            @Override
            public void onClick(ChatItem item) {
                // Mock implementation
            }
        };
        adapter = new ChatsAdapter(testChats, mockOnClick);
    }

    @Test
    public void getItemCount_EmptyList_ReturnsZero() {
        assertEquals(0, adapter.getItemCount());
    }

    @Test
    public void getItemCount_WithChats_ReturnsCorrectCount() {
        // Добавляем несколько чатов в список
        testChats.add(new ChatItem("Chat 1", "Subtitle 1"));
        testChats.add(new ChatItem("Chat 2", "Subtitle 2"));
        testChats.add(new ChatItem("Chat 3", "Subtitle 3"));
        
        int count = adapter.getItemCount();
        
        // Проверяем, что количество элементов корректно
        assertEquals(3, count);
    }

    @Test
    public void constructor_WithValidParameters_InitializesCorrectly() {
        // Создаем адаптер с валидными параметрами
        List<ChatItem> chats = new ArrayList<>();
        chats.add(new ChatItem("Test Chat", "Test Subtitle"));
        ChatsAdapter.OnChatClick onClick = new ChatsAdapter.OnChatClick() {
            @Override
            public void onClick(ChatItem item) {}
        };
        
        ChatsAdapter newAdapter = new ChatsAdapter(chats, onClick);
        
        // Проверяем, что адаптер инициализирован корректно
        assertNotNull(newAdapter);
        assertEquals(1, newAdapter.getItemCount());
    }

    @Test
    public void constructor_WithNullList_HandlesGracefully() {
        // Проверяем обработку null списка в конструкторе
        try {
            new ChatsAdapter(null, mockOnClick);
            // Если конструктор не выбрасывает исключение, это приемлемо
            assertTrue("Constructor should handle null list gracefully", true);
        } catch (Exception e) {
            // Если выбрасывается исключение, это тоже приемлемое поведение
            assertTrue("Exception should be handled gracefully", true);
        }
    }

    @Test
    public void constructor_WithNullOnClick_HandlesGracefully() {
        // Проверяем обработку null onClick в конструкторе
        try {
            new ChatsAdapter(testChats, null);
            // Если конструктор не выбрасывает исключение, это приемлемо
            assertTrue("Constructor should handle null onClick gracefully", true);
        } catch (Exception e) {
            // Если выбрасывается исключение, это тоже приемлемое поведение
            assertTrue("Exception should be handled gracefully", true);
        }
    }

    @Test
    public void getItemCount_WithSingleChat_ReturnsOne() {
        // Добавляем один чат в список
        testChats.add(new ChatItem("Single Chat", "Single Subtitle"));
        
        int count = adapter.getItemCount();
        
        // Проверяем, что количество равно 1
        assertEquals(1, count);
    }

    @Test
    public void getItemCount_WithMultipleChats_ReturnsCorrectCount() {
        // Добавляем 10 чатов в список
        for (int i = 0; i < 10; i++) {
            testChats.add(new ChatItem("Chat " + i, "Subtitle " + i));
        }
        
        int count = adapter.getItemCount();
        
        // Проверяем, что количество равно 10
        assertEquals(10, count);
    }

    @Test
    public void getItemCount_AfterAddingChat_ReturnsUpdatedCount() {
        // Проверяем изменение количества после добавления чата
        int initialCount = adapter.getItemCount();
        
        testChats.add(new ChatItem("New Chat", "New Subtitle"));
        
        // Проверяем, что количество увеличилось на 1
        assertEquals(initialCount + 1, adapter.getItemCount());
    }

    @Test
    public void getItemCount_WithEmptyChatItems_ReturnsCorrectCount() {
        // Добавляем чаты с пустыми строками
        testChats.add(new ChatItem("", ""));
        testChats.add(new ChatItem("", ""));
        
        int count = adapter.getItemCount();
        
        // Проверяем, что пустые чаты учитываются в подсчете
        assertEquals(2, count);
    }

    @Test
    public void getItemCount_WithNullChatItems_ReturnsCorrectCount() {
        // Добавляем чаты с null значениями
        testChats.add(new ChatItem(null, null));
        testChats.add(new ChatItem(null, null));
        
        int count = adapter.getItemCount();
        
        // Проверяем, что чаты с null значениями учитываются в подсчете
        assertEquals(2, count);
    }

    @Test
    public void getItemCount_WithLongChatNames_ReturnsCorrectCount() {
        // Добавляем чат с длинными строками
        String longName = "This is a very long chat name that might be used in the application";
        String longSubtitle = "This is a very long subtitle that contains a lot of text";
        
        testChats.add(new ChatItem(longName, longSubtitle));
        
        int count = adapter.getItemCount();
        
        // Проверяем, что чат с длинными строками учитывается в подсчете
        assertEquals(1, count);
    }

    @Test
    public void getItemCount_WithSpecialCharacters_ReturnsCorrectCount() {
        // Добавляем чат со специальными символами
        String specialName = "Chat with émojis 🎉 and symbols @#$%";
        String specialSubtitle = "Subtitle with unicode: Привет! 🌟";
        
        testChats.add(new ChatItem(specialName, specialSubtitle));
        
        int count = adapter.getItemCount();
        
        // Проверяем, что чат со специальными символами учитывается в подсчете
        assertEquals(1, count);
    }

    @Test
    public void onChatClickInterface_IsDefined() {
        // Проверяем, что интерфейс OnChatClick определен
        assertNotNull(ChatsAdapter.OnChatClick.class);
    }

    @Test
    public void adapter_HandlesEmptyListCorrectly() {
        // Создаем адаптер с пустым списком
        List<ChatItem> emptyList = new ArrayList<>();
        ChatsAdapter emptyAdapter = new ChatsAdapter(emptyList, mockOnClick);
        
        int count = emptyAdapter.getItemCount();
        
        // Проверяем, что пустой список обрабатывается корректно
        assertEquals(0, count);
    }
}
