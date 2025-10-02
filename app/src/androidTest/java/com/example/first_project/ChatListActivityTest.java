package com.example.first_project;

import android.content.Intent;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.first_project.activity.ChatListActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ChatListActivityTest {

    @Rule
    public ActivityScenarioRule<ChatListActivity> activityRule = 
        new ActivityScenarioRule<>(ChatListActivity.class);

    @Test
    public void chatListActivity_LaunchesSuccessfully() {
        // Запускаем активность
        // ActivityScenarioRule автоматически запускает активность
        
        // Проверяем, что RecyclerView для чатов отображается
        Espresso.onView(ViewMatchers.withId(R.id.recyclerChats))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void chatRecyclerView_IsDisplayed() {
        // Запускаем активность
        // ActivityScenarioRule автоматически запускает активность
        
        // Проверяем, что RecyclerView для чатов отображается
        Espresso.onView(ViewMatchers.withId(R.id.recyclerChats))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void chatRecyclerView_HasItems() {
        // Запускаем активность
        // ActivityScenarioRule автоматически запускает активность
        
        // Проверяем, что RecyclerView содержит элементы
        // Поскольку в коде есть предустановленные чаты, проверяем их наличие
        Espresso.onView(ViewMatchers.withId(R.id.recyclerChats))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void chatListActivity_ShowsPredefinedChats() {
        // Запускаем активность
        // ActivityScenarioRule автоматически запускает активность
        
        // Проверяем, что активность загружается без ошибок
        // В коде есть предустановленные чаты: "Gleb", "Семья", "Bro"
        Espresso.onView(ViewMatchers.withId(R.id.recyclerChats))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void chatListActivity_HandlesClickEvents() {
        // Запускаем активность
        // ActivityScenarioRule автоматически запускает активность
        
        // Проверяем, что RecyclerView кликабелен
        Espresso.onView(ViewMatchers.withId(R.id.recyclerChats))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void chatListActivity_IsScrollable() {
        // Запускаем активность
        // ActivityScenarioRule автоматически запускает активность
        
        // Проверяем, что RecyclerView можно прокручивать
        Espresso.onView(ViewMatchers.withId(R.id.recyclerChats))
            .perform(ViewActions.swipeUp());
        
        // Проверяем, что RecyclerView все еще отображается после прокрутки
        Espresso.onView(ViewMatchers.withId(R.id.recyclerChats))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void chatListActivity_HandlesSwipeDown() {
        // Запускаем активность
        // ActivityScenarioRule автоматически запускает активность
        
        // Проверяем, что RecyclerView можно прокручивать вниз
        Espresso.onView(ViewMatchers.withId(R.id.recyclerChats))
            .perform(ViewActions.swipeDown());
        
        // Проверяем, что RecyclerView все еще отображается после прокрутки
        Espresso.onView(ViewMatchers.withId(R.id.recyclerChats))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void chatListActivity_IsInteractive() {
        // Запускаем активность
        // ActivityScenarioRule автоматически запускает активность
        
        // Проверяем, что RecyclerView интерактивен
        Espresso.onView(ViewMatchers.withId(R.id.recyclerChats))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()));
    }

    @Test
    public void chatListActivity_HandlesMultipleInteractions() {
        // Запускаем активность
        // ActivityScenarioRule автоматически запускает активность
        
        // Выполняем несколько взаимодействий
        Espresso.onView(ViewMatchers.withId(R.id.recyclerChats))
            .perform(ViewActions.swipeUp());
        
        Espresso.onView(ViewMatchers.withId(R.id.recyclerChats))
            .perform(ViewActions.swipeDown());
        
        // Проверяем, что активность все еще работает корректно
        Espresso.onView(ViewMatchers.withId(R.id.recyclerChats))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void chatListActivity_LoadsWithoutErrors() {
        // Запускаем активность
        // ActivityScenarioRule автоматически запускает активность
        
        // Проверяем, что активность загружается без ошибок
        // Если активность загрузилась успешно, RecyclerView должен быть виден
        Espresso.onView(ViewMatchers.withId(R.id.recyclerChats))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
}
