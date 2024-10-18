package com.team.e.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class HashHelper {

    private HashHelper(){
        //preventing initiation
    }

    public static String encode(String input) {
        try {
            // Create a MessageDigest instance for MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            // Hash the input string
            byte[] messageDigest = md.digest(input.getBytes());
            // Convert byte array to hexadecimal format
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 encoding not supported", e);
        }
    }

    public static boolean verify(String input, String hash) {
        String inputHash = encode(input);
        return inputHash.equals(hash);
    }
}
