package com.dennis.Auth;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import com.dennis.db.DB;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Base64;

public class Auth {

    //From: https://www.programmersought.com/article/36295393794/
    
    private static final SecureRandom RAND = new SecureRandom();
  //Dennis: this can vary
    //The higher the number of iterations the more 
    // expensive computing the hash is for us and
    // also for an attacker. ist can also make you more vulnerable to DoS attacks
    private static final int ITERATIONS = 65536; 
    private static final int KEY_LENGTH = 512;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA512";
    private static final int saltSize=512;
    
    
    public static String generateSaltAndHashPassword (String password) {
    	String salt = generateSalt(saltSize);
    	return salt+"$"+hashPassword(password,salt);
    }
    
public static boolean verifyPassword (String password, String saltAndHash) {
    	
    	String salt = saltAndHash.substring(0,saltAndHash.indexOf('$'));
    	String hash=hashPassword(password,salt);
    	if (hash.isEmpty()) {
        	return false;
        };
    	return saltAndHash.equals(salt+"$"+hash);
    }


    public static String hashPassword (String password, String salt) {

        char[] chars = password.toCharArray();// password has to be converted to char[] to make the hash
        byte[] bytes = salt.getBytes();//salt has to be converted to byte[] to make use of it

        PBEKeySpec spec = new PBEKeySpec(chars, bytes, ITERATIONS, KEY_LENGTH);

        Arrays.fill(chars, Character.MIN_VALUE);

        try {
          SecretKeyFactory fac = SecretKeyFactory.getInstance(ALGORITHM);
          byte[] securePassword = fac.generateSecret(spec).getEncoded();
          return Base64.getEncoder().encodeToString(securePassword);

        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
          System.err.println("Exception encountered in hashPassword()");
          return "";

        } finally {
        	// Dennis: this holds the original password so we clean it up.
        	//Because garbage collection occurs unpredictably.
          spec.clearPassword();
        }
      }

    public static String generateSalt (final int length) {

      if (length < 1) {
        System.err.println("error in generateSalt: length must be > 0");
        return "";
      }

      byte[] salt = new byte[length];
      RAND.nextBytes(salt);

      return Base64.getEncoder().encodeToString(salt);
    }
    
    
    
}