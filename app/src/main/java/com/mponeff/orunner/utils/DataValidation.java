package com.mponeff.orunner.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataValidation {

    private static final Pattern VALID_EMAIL_ADDRESS_REGEX  =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private static final Pattern PASSWORD_FORBIDDEN_SYMBOLS =
            Pattern.compile("[_+-.,!@#$%^&?=:*();\\/|<>\"']");

    public static boolean isValidEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    public static boolean isValidPassword (String passwordStr) {
        Matcher matcher = PASSWORD_FORBIDDEN_SYMBOLS.matcher(passwordStr);
        return !matcher.find();
    }

    /*public static boolean isValidTime(String time) {
        Pattern regex = Pattern.compile("^(?:[0-9][0-9]|[01]?[0-9]):[0-5][0-9]:[0-5][0-9]$");
        Matcher matcher = regex.matcher(time);

        return matcher.matches();
    }*/
}
