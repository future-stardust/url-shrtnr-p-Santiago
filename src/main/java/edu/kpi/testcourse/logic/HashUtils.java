package edu.kpi.testcourse.logic;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Utility class for password hashing and validation.
 */
public class HashUtils {
  private final SecretKeyFactory skf;
  private final SecureRandom sr;

  /**
   * Hash utils might crash during creation if there is not PBKDF2 or SHA1 algorithms in the system.
   */
  public HashUtils() {
    try {
      skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("System doesn't provide PBKDF2 algorithm");
    }

    try {
      sr = SecureRandom.getInstance("SHA1PRNG");
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("System doesn't provide SHA1 algorithm");
    }
  }

  /**
   * Generate a hash for a password.
   *
   * @param password a password
   * @return a password hash
   */
  public String generateHash(String password) {
    int iterations = 1000;
    char[] chars = password.toCharArray();
    byte[] salt = getSalt();

    PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
    try {
      byte[] hash = skf.generateSecret(spec).getEncoded();
      return iterations + ":" + toHex(salt) + ":" + toHex(hash);
    } catch (InvalidKeySpecException e) {
      throw new RuntimeException("Error during password hash generation", e);
    }
  }

  private byte[] getSalt() {
    byte[] salt = new byte[16];
    sr.nextBytes(salt);
    return salt;
  }

  private String toHex(byte[] array) {
    BigInteger bi = new BigInteger(1, array);
    String hex = bi.toString(16);
    int paddingLength = (array.length * 2) - hex.length();
    if (paddingLength > 0) {
      return String.format("%0" + paddingLength + "d", 0) + hex;
    } else {
      return hex;
    }
  }

  /**
   * Validate hashed password.
   *
   * @param originalPassword an original password
   * @param storedPassword a hash of the original password
   * @return if password is valid
   */
  public boolean validatePassword(String originalPassword, String storedPassword) {
    String[] parts = storedPassword.split(":");
    int iterations = Integer.parseInt(parts[0]);
    byte[] salt = fromHex(parts[1]);
    byte[] hash = fromHex(parts[2]);

    PBEKeySpec spec = new PBEKeySpec(
        originalPassword.toCharArray(),
        salt,
        iterations,
        hash.length * 8
    );
    try {
      byte[] testHash = skf.generateSecret(spec).getEncoded();
      int diff = hash.length ^ testHash.length;
      for (int i = 0; i < hash.length && i < testHash.length; i++) {
        diff |= hash[i] ^ testHash[i];
      }
      return diff == 0;
    } catch (InvalidKeySpecException e) {
      throw new RuntimeException("Error during password validation", e);
    }
  }

  private byte[] fromHex(String hex) {
    byte[] bytes = new byte[hex.length() / 2];
    for (int i = 0; i < bytes.length; i++) {
      bytes[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
    }
    return bytes;
  }
}
