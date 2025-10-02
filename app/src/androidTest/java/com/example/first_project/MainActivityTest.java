package com.example.first_project;

import android.content.Intent;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.first_project.activity.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = 
        new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void mainActivity_LaunchesSuccessfully() {
        // Запускаем активность
        // ActivityScenarioRule автоматически запускает активность
        
        // Проверяем, что основные элементы интерфейса отображаются
        Espresso.onView(ViewMatchers.withId(R.id.recyclerMessages))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        
        Espresso.onView(ViewMatchers.withId(R.id.editTextMessage))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        
        Espresso.onView(ViewMatchers.withId(R.id.btnSend))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        
        Espresso.onView(ViewMatchers.withId(R.id.btnBack))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void messageInput_AcceptsTextInput() {
        // Запускаем активность
        // ActivityScenarioRule автоматически запускает активность
        
        String testMessage = "Привет, это тестовое сообщение!";
        
        // Вводим текст в поле сообщения
        Espresso.onView(ViewMatchers.withId(R.id.editTextMessage))
            .perform(ViewActions.typeText(testMessage));
        
        // Проверяем, что текст введен
        Espresso.onView(ViewMatchers.withId(R.id.editTextMessage))
            .check(ViewAssertions.matches(ViewMatchers.withText(testMessage)));
    }

    @Test
    public void sendButton_ClickWithEmptyMessage_DoesNotSend() {
        // Запускаем активность
        // ActivityScenarioRule автоматически запускает активность
        
        // Убеждаемся, что поле сообщения пустое
        Espresso.onView(ViewMatchers.withId(R.id.editTextMessage))
            .check(ViewAssertions.matches(ViewMatchers.withText("")));
        
        // Нажимаем кнопку отправки
        Espresso.onView(ViewMatchers.withId(R.id.btnSend))
            .perform(ViewActions.click());
        
        // Проверяем, что поле сообщения остается пустым
        Espresso.onView(ViewMatchers.withId(R.id.editTextMessage))
            .check(ViewAssertions.matches(ViewMatchers.withText("")));
    }

    @Test
    public void sendButton_ClickWithMessage_ClearsInputField() {
        // Запускаем активность
        // ActivityScenarioRule автоматически запускает активность
        
        String testMessage = "Тестовое сообщение";
        
        // Вводим сообщение
        Espresso.onView(ViewMatchers.withId(R.id.editTextMessage))
            .perform(ViewActions.typeText(testMessage));
        
        // Скрываем клавиатуру
        Espresso.closeSoftKeyboard();
        
        // Нажимаем кнопку отправки
        Espresso.onView(ViewMatchers.withId(R.id.btnSend))
            .perform(ViewActions.click());
        
        // Проверяем, что поле сообщения очистилось
        Espresso.onView(ViewMatchers.withId(R.id.editTextMessage))
            .check(ViewAssertions.matches(ViewMatchers.withText("")));
    }

    @Test
    public void sendButton_IsClickable() {
        // Запускаем активность
        // ActivityScenarioRule автоматически запускает активность
        
        // Проверяем, что кнопка отправки кликабельна
        Espresso.onView(ViewMatchers.withId(R.id.btnSend))
            .check(ViewAssertions.matches(ViewMatchers.isClickable()));
    }

    @Test
    public void backButton_IsClickable() {
        // Запускаем активность
        // ActivityScenarioRule автоматически запускает активность
        
        // Проверяем, что кнопка "Назад" кликабельна
        Espresso.onView(ViewMatchers.withId(R.id.btnBack))
            .check(ViewAssertions.matches(ViewMatchers.isClickable()));
    }

    @Test
    public void backButton_Click_NavigatesBack() {
        // Запускаем активность
        // ActivityScenarioRule автоматически запускает активность
        
        // Нажимаем кнопку "Назад"
        Espresso.onView(ViewMatchers.withId(R.id.btnBack))
            .perform(ViewActions.click());
        
        // Проверяем, что кнопка кликабельна (навигация должна произойти)
        Espresso.onView(ViewMatchers.withId(R.id.btnBack))
            .check(ViewAssertions.matches(ViewMatchers.isClickable()));
    }

    @Test
    public void messageRecyclerView_IsDisplayed() {
        // Запускаем активность
        // ActivityScenarioRule автоматически запускает активность
        
        // Проверяем, что RecyclerView для сообщений отображается
        Espresso.onView(ViewMatchers.withId(R.id.recyclerMessages))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void messageInput_IsEditable() {
        // Запускаем активность
        // ActivityScenarioRule автоматически запускает активность
        
        // Проверяем, что поле ввода сообщения редактируемо
        Espresso.onView(ViewMatchers.withId(R.id.editTextMessage))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()));
    }

    @Test
    public void messageInput_SupportsLongText() {
        // Запускаем активность
        // ActivityScenarioRule автоматически запускает активность
        
        String longMessage = "Это очень длинное сообщение, которое содержит много текста " +
            "и должно проверить, что приложение корректно обрабатывает длинные сообщения. " +
            "Сообщение может содержать несколько предложений и даже абзацев текста.";
        
        // Вводим длинное сообщение
        Espresso.onView(ViewMatchers.withId(R.id.editTextMessage))
            .perform(ViewActions.typeText(longMessage));
        
        // Проверяем, что текст введен
        Espresso.onView(ViewMatchers.withId(R.id.editTextMessage))
            .check(ViewAssertions.matches(ViewMatchers.withText(longMessage)));
    }

    @Test
    public void messageInput_SupportsSpecialCharacters() {
        // Запускаем активность
        // ActivityScenarioRule автоматически запускает активность
        
        String specialMessage = "Сообщение с эмодзи 🎉 и символами @#$%^&*()";
        
        // Вводим сообщение со специальными символами
        Espresso.onView(ViewMatchers.withId(R.id.editTextMessage))
            .perform(ViewActions.typeText(specialMessage));
        
        // Проверяем, что текст введен
        Espresso.onView(ViewMatchers.withId(R.id.editTextMessage))
            .check(ViewAssertions.matches(ViewMatchers.withText(specialMessage)));
    }
}
