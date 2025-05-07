package org.example.dataBase;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Hasher {
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-384");
            byte[] hashedBytes = md.digest(password.getBytes());

            // Преобразуем в base64 для удобства хранения
            return Base64.getEncoder().encodeToString(hashedBytes);
        }

        catch (NoSuchAlgorithmException e) {
            System.err.println("Не получилось захэшировать...");
            return password;
        }
    }

    public static boolean checkPassword(String password, String hashPassword) {
        password = hashPassword(hashPassword);
        return password.equals(hashPassword);
    }
}