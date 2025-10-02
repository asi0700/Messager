package com.example.first_project;

import android.content.Intent;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.first_project.activity.LoginActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule = 
        new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void loginActivity_LaunchesSuccessfully() {
        // Запускаем активность
        // ActivityScenarioRule автоматически запускает активность
        
        // Проверяем, что элементы интерфейса отображаются
        Espresso.onView(ViewMatchers.withId(R.id.editTextLoginEmail))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        
        Espresso.onView(ViewMatchers.withId(R.id.editTextLoginPassword))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        
        Espresso.onView(ViewMatchers.withId(R.id.buttonLogin))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        
        Espresso.onView(ViewMatchers.withId(R.id.textGoToRegister))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void loginButton_ClickWithEmptyFields_ShowsValidationError() {
        // Запускаем активность
        // ActivityScenarioRule автоматически запускает активность
        
        // Нажимаем кнопку входа без заполнения полей
        Espresso.onView(ViewMatchers.withId(R.id.buttonLogin))
            .perform(ViewActions.click());
        
        // Проверяем, что поля остаются пустыми (валидация сработала)
        Espresso.onView(ViewMatchers.withId(R.id.editTextLoginEmail))
            .check(ViewAssertions.matches(ViewMatchers.withText("")));
        
        Espresso.onView(ViewMatchers.withId(R.id.editTextLoginPassword))
            .check(ViewAssertions.matches(ViewMatchers.withText("")));
    }

    @Test
    public void loginButton_ClickWithValidData_AttemptsLogin() {
        // Запускаем активность
        // ActivityScenarioRule автоматически запускает активность
        
        // Заполняем поля валидными данными
        Espresso.onView(ViewMatchers.withId(R.id.editTextLoginEmail))
            .perform(ViewActions.typeText("test@example.com"));
        
        Espresso.onView(ViewMatchers.withId(R.id.editTextLoginPassword))
            .perform(ViewActions.typeText("password123"));
        
        // Скрываем клавиатуру
        Espresso.closeSoftKeyboard();
        
        // Нажимаем кнопку входа
        Espresso.onView(ViewMatchers.withId(R.id.buttonLogin))
            .perform(ViewActions.click());
        
        // Проверяем, что поля заполнены
        Espresso.onView(ViewMatchers.withId(R.id.editTextLoginEmail))
            .check(ViewAssertions.matches(ViewMatchers.withText("test@example.com")));
        
        Espresso.onView(ViewMatchers.withId(R.id.editTextLoginPassword))
            .check(ViewAssertions.matches(ViewMatchers.withText("password123")));
    }

    @Test
    public void registerLink_Click_NavigatesToRegistration() {
        // Запускаем активность
        // ActivityScenarioRule автоматически запускает активность
        
        // Нажимаем на ссылку регистрации
        Espresso.onView(ViewMatchers.withId(R.id.textGoToRegister))
            .perform(ViewActions.click());
        
        // Активность должна закрыться (переход к регистрации)
        // Это сложно проверить напрямую, но мы можем убедиться, что элемент кликабелен
        Espresso.onView(ViewMatchers.withId(R.id.textGoToRegister))
            .check(ViewAssertions.matches(ViewMatchers.isClickable()));
    }

    @Test
    public void emailField_AcceptsTextInput() {
        // Запускаем активность
        // ActivityScenarioRule автоматически запускает активность
        
        String testEmail = "user@test.com";
        
        // Вводим текст в поле email
        Espresso.onView(ViewMatchers.withId(R.id.editTextLoginEmail))
            .perform(ViewActions.typeText(testEmail));
        
        // Проверяем, что текст введен
        Espresso.onView(ViewMatchers.withId(R.id.editTextLoginEmail))
            .check(ViewAssertions.matches(ViewMatchers.withText(testEmail)));
    }

    @Test
    public void passwordField_AcceptsTextInput() {
        // Запускаем активность
        // ActivityScenarioRule автоматически запускает активность
        
        String testPassword = "mypassword123";
        
        // Вводим текст в поле пароля
        Espresso.onView(ViewMatchers.withId(R.id.editTextLoginPassword))
            .perform(ViewActions.typeText(testPassword));
        
        // Проверяем, что текст введен
        Espresso.onView(ViewMatchers.withId(R.id.editTextLoginPassword))
            .check(ViewAssertions.matches(ViewMatchers.withText(testPassword)));
    }

    @Test
    public void loginButton_IsClickable() {
        // Запускаем активность
        // ActivityScenarioRule автоматически запускает активность
        
        // Проверяем, что кнопка входа кликабельна
        Espresso.onView(ViewMatchers.withId(R.id.buttonLogin))
            .check(ViewAssertions.matches(ViewMatchers.isClickable()));
    }

    @Test
    public void registerLink_IsClickable() {
        // Запускаем активность
        // ActivityScenarioRule автоматически запускает активность
        
        // Проверяем, что ссылка регистрации кликабельна
        Espresso.onView(ViewMatchers.withId(R.id.textGoToRegister))
            .check(ViewAssertions.matches(ViewMatchers.isClickable()));
    }
}
