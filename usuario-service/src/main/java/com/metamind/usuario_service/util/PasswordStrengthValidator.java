package com.metamind.usuario_service.util;

public class PasswordStrengthValidator {

    public static boolean isStrong(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        boolean hasUpperCase = !password.equals(password.toLowerCase());
        boolean hasLowerCase = !password.equals(password.toUpperCase());
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecialChar = !password.matches("[A-Za-z0-9]*");

        return hasUpperCase && hasLowerCase && hasDigit && hasSpecialChar;
    }
}