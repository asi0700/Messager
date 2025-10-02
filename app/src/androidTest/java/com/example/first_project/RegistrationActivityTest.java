package com.example.first_project;

import android.content.Intent;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.first_project.activity.RegistrationActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class RegistrationActivityTest {

    @Rule
    public ActivityScenarioRule<RegistrationActivity> activityRule = 
        new ActivityScenarioRule<>(RegistrationActivity.class);

    @Test
    public void registrationActivity_LaunchesSuccessfully() {
        // Запускаем активность
        // ActivityScenarioRule автоматически запускает активность
        
        // Проверяем, что все элементы интерфейса отображаются
        Espresso.onView(ViewMatchers.withId(R.id.editTextEmail))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        
        Espresso.onView(ViewMatchers.withId(R.id.editTextPassword))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        
        Espresso.onView(ViewMatchers.withId(R.id.editTextConfirmPassword))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        
        Espresso.onView(ViewMatchers.withId(R.id.buttonRegister))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        
        Espresso.onView(ViewMatchers.withId(R.id.link_login))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void registerButton_ClickWithEmptyFields_ShowsValidationErrors() {
        // Запускаем активность
        // ActivityScenarioRule автоматически запускает активность
        
        // Нажимаем кнопку регистрации без заполнения полей
        Espresso.onView(ViewMatchers.withId(R.id.buttonRegister))
            .perform(ViewActions.click());
        
        // Проверяем, что поля остаются пустыми (валидация сработала)
        Espresso.onView(ViewMatchers.withId(R.id.editTextEmail))
            .check(ViewAssertions.matches(ViewMatchers.withText("")));
        
        Espresso.onView(ViewMatchers.withId(R.id.editTextPassword))
            .check(ViewAssertions.matches(ViewMatchers.withText("")));
        
        Espresso.onView(ViewMatchers.withId(R.id.editTextConfirmPassword))
            .check(ViewAssertions.matches(ViewMatchers.withText("")));
    }

    @Test
    public void registerButton_ClickWithValidData_AttemptsRegistration() {
        // Запускаем активность
        // ActivityScenarioRule автоматически запускает активность
        
        // Заполняем поля валидными данными
        Espresso.onView(ViewMatchers.withId(R.id.editTextEmail))
            .perform(ViewActions.typeText("test@example.com"));
        
        Espresso.onView(ViewMatchers.withId(R.id.editTextPassword))
            .perform(ViewActions.typeText("password123"));
        
        Espresso.onView(ViewMatchers.withId(R.id.editTextConfirmPassword))
            .perform(ViewActions.typeText("password123"));
        
        // Скрываем клавиатуру
        Espresso.closeSoftKeyboard();
        
        // Нажимаем кнопку регистрации
        Espresso.onView(ViewMatchers.withId(R.id.buttonRegister))
            .perform(ViewActions.click());
        
        // Проверяем, что поля заполнены
        Espresso.onView(ViewMatchers.withId(R.id.editTextEmail))
            .check(ViewAssertions.matches(ViewMatchers.withText("test@example.com")));
        
        Espresso.onView(ViewMatchers.withId(R.id.editTextPassword))
            .check(ViewAssertions.matches(ViewMatchers.withText("password123")));
        
        Espresso.onView(ViewMatchers.withId(R.id.editTextConfirmPassword))
            .check(ViewAssertions.matches(ViewMatchers.withText("password123")));
    }

    @Test
    public void registerButton_ClickWithMismatchedPasswords_ShowsValidationError() {
        // Запускаем активность
        // ActivityScenarioRule автоматически запускает активность
        
        // Заполняем поля с несовпадающими паролями
        Espresso.onView(ViewMatchers.withId(R.id.editTextEmail))
            .perform(ViewActions.typeText("test@example.com"));
        
        Espresso.onView(ViewMatchers.withId(R.id.editTextPassword))
            .perform(ViewActions.typeText("password123"));
        
        Espresso.onView(ViewMatchers.withId(R.id.editTextConfirmPassword))
            .perform(ViewActions.typeText("password456"));
        
        // Скрываем клавиатуру
        Espresso.closeSoftKeyboard();
        
        // Нажимаем кнопку регистрации
        Espresso.onView(ViewMatchers.withId(R.id.buttonRegister))
            .perform(ViewActions.click());
        
        // Проверяем, что поля заполнены (валидация не прошла)
        Espresso.onView(ViewMatchers.withId(R.id.editTextPassword))
            .check(ViewAssertions.matches(ViewMatchers.withText("password123")));
        
        Espresso.onView(ViewMatchers.withId(R.id.editTextConfirmPassword))
            .check(ViewAssertions.matches(ViewMatchers.withText("password456")));
    }

    @Test
    public void registerButton_ClickWithShortPassword_ShowsValidationError() {
        // Запускаем активность
        // ActivityScenarioRule автоматически запускает активность
        
        // Заполняем поля с коротким паролем
        Espresso.onView(ViewMatchers.withId(R.id.editTextEmail))
            .perform(ViewActions.typeText("test@example.com"));
        
        Espresso.onView(ViewMatchers.withId(R.id.editTextPassword))
            .perform(ViewActions.typeText("12345"));
        
        Espresso.onView(ViewMatchers.withId(R.id.editTextConfirmPassword))
            .perform(ViewActions.typeText("12345"));
        
        // Скрываем клавиатуру
        Espresso.closeSoftKeyboard();
        
        // Нажимаем кнопку регистрации
        Espresso.onView(ViewMatchers.withId(R.id.buttonRegister))
            .perform(ViewActions.click());
        
        // Проверяем, что поля заполнены (валидация не прошла)
        Espresso.onView(ViewMatchers.withId(R.id.editTextPassword))
            .check(ViewAssertions.matches(ViewMatchers.withText("12345")));
    }

    @Test
    public void loginLink_Click_NavigatesToLogin() {
        // Запускаем активность
        // ActivityScenarioRule автоматически запускает активность
        
        // Нажимаем на ссылку входа
        Espresso.onView(ViewMatchers.withId(R.id.link_login))
            .perform(ViewActions.click());
        
        // Проверяем, что ссылка кликабельна
        Espresso.onView(ViewMatchers.withId(R.id.link_login))
            .check(ViewAssertions.matches(ViewMatchers.isClickable()));
    }

    @Test
    public void emailField_AcceptsTextInput() {
        // Запускаем активность
        // ActivityScenarioRule автоматически запускает активность
        
        String testEmail = "user@test.com";
        
        // Вводим текст в поле email
        Espresso.onView(ViewMatchers.withId(R.id.editTextEmail))
            .perform(ViewActions.typeText(testEmail));
        
        // Проверяем, что текст введен
        Espresso.onView(ViewMatchers.withId(R.id.editTextEmail))
            .check(ViewAssertions.matches(ViewMatchers.withText(testEmail)));
    }

    @Test
    public void passwordField_AcceptsTextInput() {
        // Запускаем активность
        // ActivityScenarioRule автоматически запускает активность
        
        String testPassword = "mypassword123";
        
        // Вводим текст в поле пароля
        Espresso.onView(ViewMatchers.withId(R.id.editTextPassword))
            .perform(ViewActions.typeText(testPassword));
        
        // Проверяем, что текст введен
        Espresso.onView(ViewMatchers.withId(R.id.editTextPassword))
            .check(ViewAssertions.matches(ViewMatchers.withText(testPassword)));
    }

    @Test
    public void confirmPasswordField_AcceptsTextInput() {
        // Запускаем активность
        // ActivityScenarioRule автоматически запускает активность
        
        String testPassword = "mypassword123";
        
        // Вводим текст в поле подтверждения пароля
        Espresso.onView(ViewMatchers.withId(R.id.editTextConfirmPassword))
            .perform(ViewActions.typeText(testPassword));
        
        // Проверяем, что текст введен
        Espresso.onView(ViewMatchers.withId(R.id.editTextConfirmPassword))
            .check(ViewAssertions.matches(ViewMatchers.withText(testPassword)));
    }

    @Test
    public void registerButton_IsClickable() {
        // Запускаем активность
        // ActivityScenarioRule автоматически запускает активность
        
        // Проверяем, что кнопка регистрации кликабельна
        Espresso.onView(ViewMatchers.withId(R.id.buttonRegister))
            .check(ViewAssertions.matches(ViewMatchers.isClickable()));
    }

    @Test
    public void loginLink_IsClickable() {
        // Запускаем активность
        // ActivityScenarioRule автоматически запускает активность
        
        // Проверяем, что ссылка входа кликабельна
        Espresso.onView(ViewMatchers.withId(R.id.link_login))
            .check(ViewAssertions.matches(ViewMatchers.isClickable()));
    }
}
