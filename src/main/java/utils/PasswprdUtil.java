package utils;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.regex.Pattern;

public class PasswordUtil {

    // Complexity regex: at least 8 chars, 1 uppercase, 1 lowercase, 1 digit, 1 special
    private static final Pattern COMPLEXITY_PATTERN =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$");

    private static final int SALT_LENGTH = 16;

    public static String generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static String hashPassword(String password) throws Exception {
        String salt = generateSalt();
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(salt.getBytes());
        byte[] hashed = md.digest(password.getBytes());
        String hash = Base64.getEncoder().encodeToString(hashed);
        return salt + ":" + hash;
    }

    public static boolean verifyPassword(String password, String stored) throws Exception {
        String[] parts = stored.split(":");
        if (parts.length != 2) return false;
        String salt = parts[0];
        String hash = parts[1];

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(salt.getBytes());
        byte[] hashed = md.digest(password.getBytes());
        String computedHash = Base64.getEncoder().encodeToString(hashed);
        return computedHash.equals(hash);
    }

    public static boolean isComplex(String password) {
        return COMPLEXITY_PATTERN.matcher(password).matches();
    }
}
