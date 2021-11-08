package com.mirrors.mirrorsbackend.marketplaceuser.utils;

import org.springframework.stereotype.Component;

import java.util.function.Predicate;
import java.util.regex.Pattern;

@Component
public class PasswordValidator implements Predicate<String> {

    private static final Predicate<String> VALID_PASSWORD =
            Pattern.compile("^" +
                            "(?=.*[0-9])" + // Atleast one number
                            "(?=.*[a-z])" + // Atleast one lowercase letter
                            "(?=.*[A-Z])" + // Atleast one uppercase letter
                            "(?=\\S+$)" + // No whitespaces allowed
                            ".{8,32}$") // Must be between 8 and 32 characters
                    .asPredicate();

    @Override
    public boolean test(String password) {
        return VALID_PASSWORD.test(password);
    }
}
