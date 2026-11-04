
package dazzejpro.habitual.api.security;

import org.springframework.stereotype.Component;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

@Component
public class PasswordHasher {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA512";
    private static final String PREFIX = "pbkdf2_sha512";
    private static final int ITERATIONS = 210_000;   // OWASP recommendation for PBKDF2-HMAC-SHA512
    private static final int SALT_BYTES = 16;        // 128-bit salt
    private static final int HASH_BITS = 512;        // 512-bit derived key (full SHA-512 output)

    private final SecureRandom random = new SecureRandom();

    /** Returns a salted hash of the raw password, ready to store. */
    public String hash(String rawPassword) {
        if (rawPassword == null) {
            throw new IllegalArgumentException("password must not be null");
        }
        byte[] salt = new byte[SALT_BYTES];
        random.nextBytes(salt);

        byte[] hash = pbkdf2(rawPassword.toCharArray(), salt, ITERATIONS, HASH_BITS);

        Base64.Encoder b64 = Base64.getEncoder();
        return PREFIX + "$" + ITERATIONS + "$" + b64.encodeToString(salt) + "$" + b64.encodeToString(hash);
    }

    /** Returns true if the raw password matches the stored hash. */
    public boolean matches(String rawPassword, String storedHash) {
        if (rawPassword == null || storedHash == null) {
            return false;
        }
        String[] parts = storedHash.split("\\$");
        if (parts.length != 4 || !PREFIX.equals(parts[0])) {
            return false;
        }
        try {
            int iterations = Integer.parseInt(parts[1]);
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] expected = Base64.getDecoder().decode(parts[3]);

            byte[] actual = pbkdf2(rawPassword.toCharArray(), salt, iterations, expected.length * 8);

            // Constant-time comparison so timing doesn't leak how many bytes matched
            return MessageDigest.isEqual(expected, actual);
        } catch (IllegalArgumentException e) {
            // Malformed number or Base64 in the stored value
            return false;
        }
    }

    private byte[] pbkdf2(char[] password, byte[] salt, int iterations, int keyLengthBits) {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLengthBits);
        try {
            return SecretKeyFactory.getInstance(ALGORITHM).generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("PBKDF2 algorithm unavailable", e);
        } finally {
            spec.clearPassword();
            Arrays.fill(password, '\0');
        }
    }
}