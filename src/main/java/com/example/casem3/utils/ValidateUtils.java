package com.example.casem3.utils;

import java.util.regex.Pattern;

public class ValidateUtils {
    public static final String ADDRESS_REGEX = "^[A-Za-z0-9][A-Za-z0-9\\s]{4,14}$";

    public static final String NAME_REGEX = "^[A-Za-z0-9][A-Za-z0-9\\s]{4,9}$";
    public static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=_])(?=\\S+$).{6,}$";
    public static final String USERNAME_PATTERN = "^\\S{6,}$";
    public static final String EMAIL_PATTERN = "^[A-Za-z0-9.]*[A-Za-z0-9]+@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)$";
    public static final String PHONE_PATTERN = "^[0][1-9][0-9]{8}$";
    public static final String QUANTITY_PATTERN = "^[1-100000]$";
    public static boolean isQuantityValid(String quantity){
        return Pattern.matches(QUANTITY_PATTERN,quantity);
    }
    public static boolean isPhoneValid(String phone){
        return Pattern.matches(PHONE_PATTERN,phone);
    }
    public static boolean isEmailValid(String email){
        return Pattern.matches(EMAIL_PATTERN,email);
    }
    public static boolean isPasswordValid(String password){
        return Pattern.matches(PASSWORD_PATTERN,password);
    }
    public static boolean isUsernameValid(String username){
        return Pattern.matches(USERNAME_PATTERN,username);
    }

    public static boolean isNameValid(String name) {
        return Pattern.matches(NAME_REGEX, name);
    }
}
