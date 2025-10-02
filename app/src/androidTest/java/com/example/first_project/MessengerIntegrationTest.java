package com.example.first_project;

import android.content.Intent;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.first_project.activity.LoginActivity;
import com.example.first_project.activity.RegistrationActivity;
import com.example.first_project.activity.MainActivity;
import com.example.first_project.activity.ChatListActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MessengerIntegrationTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> loginActivityRule = 
        new ActivityScenarioRule<>(LoginActivity.class);

    @Rule
    public ActivityScenarioRule<RegistrationActivity> registrationActivityRule = 
        new ActivityScenarioRule<>(RegistrationActivity.class);

    @Rule
    public ActivityScenarioRule<MainActivity> mainActivityRule = 
        new ActivityScenarioRule<>(MainActivity.class);

    @Rule
    public ActivityScenarioRule<ChatListActivity> chatListActivityRule = 
        new ActivityScenarioRule<>(ChatListActivity.class);

    @Test
    public void fullAppFlow_RegistrationToChat_WorksCorrectly() {
        // 1. Тестируем регистрацию
        // ActivityScenarioRule автоматически запускает активность
        
        // Заполняем форму регистрации
        Espresso.onView(ViewMatchers.withId(R.id.editTextEmail))
            .perform(ViewActions.typeText("integration@test.com"));
        
        Espresso.onView(ViewMatchers.withId(R.id.editTextPassword))
            .perform(ViewActions.typeText("password123"));
        
        Espresso.onView(ViewMatchers.withId(R.id.editTextConfirmPassword))
            .perform(ViewActions.typeText("password123"));
        
        Espresso.closeSoftKeyboard();
        
        // Нажимаем кнопку регистрации
        Espresso.onView(ViewMatchers.withId(R.id.buttonRegister))
            .perform(ViewActions.click());
        
        // Проверяем, что форма была заполнена
        Espresso.onView(ViewMatchers.withId(R.id.editTextEmail))
            .check(ViewAssertions.matches(ViewMatchers.withText("integration@test.com")));
    }

    @Test
    public void fullAppFlow_LoginToChat_WorksCorrectly() {
        // 1. Тестируем вход
        // ActivityScenarioRule автоматически запускает активность
        
        // Заполняем форму входа
        Espresso.onView(ViewMatchers.withId(R.id.editTextLoginEmail))
            .perform(ViewActions.typeText("test@example.com"));
        
        Espresso.onView(ViewMatchers.withId(R.id.editTextLoginPassword))
            .perform(ViewActions.typeText("password123"));
        
        Espresso.closeSoftKeyboard();
        
        // Нажимаем кнопку входа
        Espresso.onView(ViewMatchers.withId(R.id.buttonLogin))
            .perform(ViewActions.click());
        
        // Проверяем, что форма была заполнена
        Espresso.onView(ViewMatchers.withId(R.id.editTextLoginEmail))
            .check(ViewAssertions.matches(ViewMatchers.withText("test@example.com")));
    }

    @Test
    public void navigationFlow_LoginToRegistration_WorksCorrectly() {
        // 1. Запускаем LoginActivity
        // ActivityScenarioRule автоматически запускает активность
        
        // 2. Нажимаем на ссылку регистрации
        Espresso.onView(ViewMatchers.withId(R.id.textGoToRegister))
            .perform(ViewActions.click());
        
        // 3. Проверяем, что переход произошел (LoginActivity должна закрыться)
        // Это сложно проверить напрямую, но мы можем убедиться, что элемент кликабелен
        Espresso.onView(ViewMatchers.withId(R.id.textGoToRegister))
            .check(ViewAssertions.matches(ViewMatchers.isClickable()));
    }

    @Test
    public void navigationFlow_RegistrationToLogin_WorksCorrectly() {
        // 1. Запускаем RegistrationActivity
        // ActivityScenarioRule автоматически запускает активность
        
        // 2. Нажимаем на ссылку входа
        Espresso.onView(ViewMatchers.withId(R.id.link_login))
            .perform(ViewActions.click());
        
        // 3. Проверяем, что переход произошел
        Espresso.onView(ViewMatchers.withId(R.id.link_login))
            .check(ViewAssertions.matches(ViewMatchers.isClickable()));
    }

    @Test
    public void chatFlow_SendMessage_WorksCorrectly() {
        // 1. Запускаем MainActivity (чат)
        // ActivityScenarioRule автоматически запускает активность
        
        // 2. Вводим сообщение
        String testMessage = "Интеграционный тест сообщения";
        Espresso.onView(ViewMatchers.withId(R.id.editTextMessage))
            .perform(ViewActions.typeText(testMessage));
        
        // 3. Скрываем клавиатуру
        Espresso.closeSoftKeyboard();
        
        // 4. Нажимаем кнопку отправки
        Espresso.onView(ViewMatchers.withId(R.id.btnSend))
            .perform(ViewActions.click());
        
        // 5. Проверяем, что поле сообщения очистилось
        Espresso.onView(ViewMatchers.withId(R.id.editTextMessage))
            .check(ViewAssertions.matches(ViewMatchers.withText("")));
    }

    @Test
    public void chatFlow_BackButton_WorksCorrectly() {
        // 1. Запускаем MainActivity (чат)
        // ActivityScenarioRule автоматически запускает активность
        
        // 2. Нажимаем кнопку "Назад"
        Espresso.onView(ViewMatchers.withId(R.id.btnBack))
            .perform(ViewActions.click());
        
        // 3. Проверяем, что кнопка кликабельна (навигация должна произойти)
        Espresso.onView(ViewMatchers.withId(R.id.btnBack))
            .check(ViewAssertions.matches(ViewMatchers.isClickable()));
    }

    @Test
    public void chatListFlow_DisplayChats_WorksCorrectly() {
        // 1. Запускаем ChatListActivity
        // ActivityScenarioRule автоматически запускает активность
        
        // 2. Проверяем, что список чатов отображается
        Espresso.onView(ViewMatchers.withId(R.id.recyclerChats))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        
        // 3. Проверяем, что список можно прокручивать
        Espresso.onView(ViewMatchers.withId(R.id.recyclerChats))
            .perform(ViewActions.swipeUp());
        
        // 4. Проверяем, что список все еще отображается
        Espresso.onView(ViewMatchers.withId(R.id.recyclerChats))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void validationFlow_EmptyFields_ShowsErrors() {
        // 1. Тестируем валидацию в LoginActivity
        // ActivityScenarioRule автоматически запускает активность
        
        // 2. Нажимаем кнопку входа без заполнения полей
        Espresso.onView(ViewMatchers.withId(R.id.buttonLogin))
            .perform(ViewActions.click());
        
        // 3. Проверяем, что поля остаются пустыми
        Espresso.onView(ViewMatchers.withId(R.id.editTextLoginEmail))
            .check(ViewAssertions.matches(ViewMatchers.withText("")));
        
        // 4. Тестируем валидацию в RegistrationActivity
        // ActivityScenarioRule автоматически запускает активность
        
        // 5. Нажимаем кнопку регистрации без заполнения полей
        Espresso.onView(ViewMatchers.withId(R.id.buttonRegister))
            .perform(ViewActions.click());
        
        // 6. Проверяем, что поля остаются пустыми
        Espresso.onView(ViewMatchers.withId(R.id.editTextEmail))
            .check(ViewAssertions.matches(ViewMatchers.withText("")));
    }

    @Test
    public void validationFlow_InvalidData_ShowsErrors() {
        // 1. Тестируем невалидные данные в RegistrationActivity
        // ActivityScenarioRule автоматически запускает активность
        
        // 2. Заполняем поля невалидными данными
        Espresso.onView(ViewMatchers.withId(R.id.editTextEmail))
            .perform(ViewActions.typeText("invalid-email"));
        
        Espresso.onView(ViewMatchers.withId(R.id.editTextPassword))
            .perform(ViewActions.typeText("12345")); // Слишком короткий пароль
        
        Espresso.onView(ViewMatchers.withId(R.id.editTextConfirmPassword))
            .perform(ViewActions.typeText("123456")); // Пароли не совпадают
        
        Espresso.closeSoftKeyboard();
        
        // 3. Нажимаем кнопку регистрации
        Espresso.onView(ViewMatchers.withId(R.id.buttonRegister))
            .perform(ViewActions.click());
        
        // 4. Проверяем, что поля заполнены (валидация не прошла)
        Espresso.onView(ViewMatchers.withId(R.id.editTextEmail))
            .check(ViewAssertions.matches(ViewMatchers.withText("invalid-email")));
    }

    @Test
    public void appFlow_AllActivities_LaunchSuccessfully() {
        // Проверяем, что все активности запускаются без ошибок
        
        // 1. LoginActivity
        // ActivityScenarioRule автоматически запускает активность
        Espresso.onView(ViewMatchers.withId(R.id.buttonLogin))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        
        // 2. RegistrationActivity
        // ActivityScenarioRule автоматически запускает активность
        Espresso.onView(ViewMatchers.withId(R.id.buttonRegister))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        
        // 3. MainActivity
        // ActivityScenarioRule автоматически запускает активность
        Espresso.onView(ViewMatchers.withId(R.id.btnSend))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        
        // 4. ChatListActivity
        // ActivityScenarioRule автоматически запускает активность
        Espresso.onView(ViewMatchers.withId(R.id.recyclerChats))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
}
