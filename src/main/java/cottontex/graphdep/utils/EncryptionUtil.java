package cottontex.graphdep.utils;

import lombok.Setter;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class EncryptionUtil {
    private static final String ALGORITHM = "AES";
    private static final String SECRET_KEY = "YourSecretKey123"; // Replace with a secure key
    @Setter
    private static boolean encryptionEnabled = false; // Flag to enable/disable encryption

    public static String encrypt(String value) throws Exception {
        if (!encryptionEnabled) {
            return value; // Return the value as-is if encryption is disabled
        }
        SecretKey key = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = cipher.doFinal(value.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String decrypt(String encrypted) throws Exception {
        if (!encryptionEnabled) {
            return encrypted; // Return the value as-is if encryption is disabled
        }
        SecretKey key = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encrypted));
        return new String(decryptedBytes);
    }
}