package com.example.first_project;

import org.junit.Test;
import static org.junit.Assert.*;

import com.example.first_project.utils.ValidationUtils;

public class ValidationTest {

    // Тесты для валидации email
    @Test
    public void isValidEmail_ValidEmail_ReturnsTrue() {
        // Проверяем валидные email адреса
        assertTrue(ValidationUtils.isValidEmail("test@example.com"));
        assertTrue(ValidationUtils.isValidEmail("user.name@domain.co.uk"));
        assertTrue(ValidationUtils.isValidEmail("test123@test.org"));
    }

    @Test
    public void isValidEmail_InvalidEmail_ReturnsFalse() {
        // Проверяем невалидные email адреса
        assertFalse(ValidationUtils.isValidEmail("invalid-email"));
        assertFalse(ValidationUtils.isValidEmail("@example.com"));
        assertFalse(ValidationUtils.isValidEmail("test@"));
        assertFalse(ValidationUtils.isValidEmail(""));
        assertFalse(ValidationUtils.isValidEmail(null));
    }

    // Тесты для валидации пароля
    @Test
    public void isValidPassword_ValidPassword_ReturnsTrue() {
        // Проверяем валидные пароли (длина >= 6 символов)
        assertTrue(ValidationUtils.isValidPassword("123456"));
        assertTrue(ValidationUtils.isValidPassword("password123"));
        assertTrue(ValidationUtils.isValidPassword("verylongpassword"));
    }

    @Test
    public void isValidPassword_InvalidPassword_ReturnsFalse() {
        // Проверяем невалидные пароли (длина < 6 символов)
        assertFalse(ValidationUtils.isValidPassword("12345"));
        assertFalse(ValidationUtils.isValidPassword(""));
        assertFalse(ValidationUtils.isValidPassword(null));
    }

    // Тесты для проверки совпадения паролей
    @Test
    public void passwordsMatch_MatchingPasswords_ReturnsTrue() {
        // Проверяем совпадающие пароли
        assertTrue(ValidationUtils.passwordsMatch("password123", "password123"));
        assertTrue(ValidationUtils.passwordsMatch("", ""));
    }

    @Test
    public void passwordsMatch_NonMatchingPasswords_ReturnsFalse() {
        // Проверяем несовпадающие пароли
        assertFalse(ValidationUtils.passwordsMatch("password123", "password456"));
        assertFalse(ValidationUtils.passwordsMatch("password", ""));
        assertFalse(ValidationUtils.passwordsMatch(null, "password"));
        assertFalse(ValidationUtils.passwordsMatch("password", null));
    }

    // Тесты для проверки непустых строк
    @Test
    public void isNotEmpty_NonEmptyString_ReturnsTrue() {
        // Проверяем непустые строки
        assertTrue(ValidationUtils.isNotEmpty("text"));
        assertTrue(ValidationUtils.isNotEmpty("  text  "));
    }

    @Test
    public void isNotEmpty_EmptyString_ReturnsFalse() {
        // Проверяем пустые строки и null
        assertFalse(ValidationUtils.isNotEmpty(""));
        assertFalse(ValidationUtils.isNotEmpty("   "));
        assertFalse(ValidationUtils.isNotEmpty(null));
    }

    // Тесты для полной валидации регистрации
    @Test
    public void validateRegistration_ValidData_ReturnsSuccess() {
        // Проверяем успешную валидацию регистрации
        ValidationUtils.ValidationResult result = ValidationUtils.validateRegistration(
            "test@example.com", "password123", "password123");
        
        assertTrue(result.isValid());
        assertEquals("Валидация прошла успешно", result.getMessage());
    }

    @Test
    public void validateRegistration_EmptyEmail_ReturnsError() {
        // Проверяем ошибку при пустом email
        ValidationUtils.ValidationResult result = ValidationUtils.validateRegistration(
            "", "password123", "password123");
        
        assertFalse(result.isValid());
        assertEquals("Введите email", result.getMessage());
    }

    @Test
    public void validateRegistration_InvalidEmail_ReturnsError() {
        // Проверяем ошибку при невалидном email
        ValidationUtils.ValidationResult result = ValidationUtils.validateRegistration(
            "invalid-email", "password123", "password123");
        
        assertFalse(result.isValid());
        assertEquals("Введите корректный email", result.getMessage());
    }

    @Test
    public void validateRegistration_ShortPassword_ReturnsError() {
        // Проверяем ошибку при коротком пароле
        ValidationUtils.ValidationResult result = ValidationUtils.validateRegistration(
            "test@example.com", "12345", "12345");
        
        assertFalse(result.isValid());
        assertEquals("Пароль должен быть не короче 6 символов", result.getMessage());
    }

    @Test
    public void validateRegistration_PasswordsDoNotMatch_ReturnsError() {
        // Проверяем ошибку при несовпадающих паролях
        ValidationUtils.ValidationResult result = ValidationUtils.validateRegistration(
            "test@example.com", "password123", "password456");
        
        assertFalse(result.isValid());
        assertEquals("Пароли не совпадают", result.getMessage());
    }

    // Тесты для полной валидации входа
    @Test
    public void validateLogin_ValidData_ReturnsSuccess() {
        // Проверяем успешную валидацию входа
        ValidationUtils.ValidationResult result = ValidationUtils.validateLogin(
            "test@example.com", "password123");
        
        assertTrue(result.isValid());
        assertEquals("Валидация прошла успешно", result.getMessage());
    }

    @Test
    public void validateLogin_EmptyEmail_ReturnsError() {
        // Проверяем ошибку при пустом email в форме входа
        ValidationUtils.ValidationResult result = ValidationUtils.validateLogin("", "password123");
        
        assertFalse(result.isValid());
        assertEquals("Введите email", result.getMessage());
    }

    @Test
    public void validateLogin_EmptyPassword_ReturnsError() {
        // Проверяем ошибку при пустом пароле в форме входа
        ValidationUtils.ValidationResult result = ValidationUtils.validateLogin("test@example.com", "");
        
        assertFalse(result.isValid());
        assertEquals("Введите пароль", result.getMessage());
    }
}
