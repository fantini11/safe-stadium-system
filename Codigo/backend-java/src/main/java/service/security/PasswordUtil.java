package service.security;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utilitário central para hash e validação de senhas.
 */
public final class PasswordUtil {
    private static final int BCRYPT_ROUNDS = 12;

    private PasswordUtil() {}

    public static String hashPassword(String plainText) {
        if (plainText == null || plainText.isBlank()) {
            return plainText;
        }
        if (isHashed(plainText)) {
            return plainText;
        }
        return BCrypt.hashpw(plainText, BCrypt.gensalt(BCRYPT_ROUNDS));
    }

    public static boolean isHashed(String value) {
        return value != null && value.startsWith("$2");
    }

    public static boolean matches(String plainText, String stored) {
        if (plainText == null || stored == null) {
            return false;
        }
        if (isHashed(stored)) {
            try {
                return BCrypt.checkpw(plainText, stored);
            } catch (Exception e) {
                return false;
            }
        }
        return plainText.equals(stored);
    }
}
