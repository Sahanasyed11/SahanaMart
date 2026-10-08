package com.sahana.sahanamart.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Password utility using jBCrypt.
 * Complies with Section 9: Passwords bcrypt-hashed and never stored plaintext or logged.
 */
public class PasswordUtil {
    private static final int WORK_FACTOR = 10;

    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(WORK_FACTOR));
    }

    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null || hashedPassword.isEmpty()) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (Exception e) {
            return false;
        }
    }
}
