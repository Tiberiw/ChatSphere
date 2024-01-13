package org.map.socialnetwork.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordManager {

    private static MessageDigest md = null;

    public static String getEncryptedPassword(String password) {
        if(md == null) {
            try {
                md = MessageDigest.getInstance("SHA-256");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }

        md.update(password.getBytes());

        byte[] digest = md.digest();

        StringBuilder hexString = new StringBuilder();

        for(byte b : digest) {
            hexString.append(Integer.toHexString(0xFF & b));
        }

        return hexString.toString();
    }
}
