package com.example.miraihellp.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class AESDecryptor {

    /**
     * 解密
     * @param ciphertext
     * @param key
     * @return
     * @throws Exception
     */
    public static String decryptAES(String ciphertext, String key) throws Exception {
        // 将所有空格替换为加号
        ciphertext = ciphertext.replace(" ", "+");
        // 将 Base64 编码的密文解码为字节数组
        byte[] ciphertextBytes = Base64.getDecoder().decode(ciphertext);
        // 将密钥转换为字节数组
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        // 创建 AES 密钥规范
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
        // 创建 AES 密码器并设置为解密模式
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        // 解密密文
        byte[] decryptedBytes = cipher.doFinal(ciphertextBytes);
        // 将解密后的字节数组转换为字符串
        String plaintext = new String(decryptedBytes, StandardCharsets.UTF_8);
        return plaintext;
    }


    /**
     * 加密
     * @param plaintext
     * @param key
     * @return
     * @throws Exception
     */
    public static String encryptAES(String plaintext, String key) throws Exception {
        // 将密钥转换为字节数组
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        // 创建 AES 密钥规范
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
        // 创建 AES 密码器并设置为加密模式
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        // 加密明文
        byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
        // 将加密后的字节数组进行 Base64 编码
        String ciphertext = Base64.getEncoder().encodeToString(encryptedBytes);
        return ciphertext;
    }

    public static void main(String[] args) throws UnsupportedEncodingException {


        String ciphertext="fYpgFUlLx4DtefJQsor8xXEP6qeTHPL1OvhJcjrCmp1PQYvi3UpBLPZjlrTNF+1jC7emu/uy43wHS9WLtGVkjIM6sloiWLN8Lbmiqrv0vPrihculFgKzXuzShIqanx00PtnyCPJlzx8625fasQLcaujoY8oR9/Ry6VRChMvcpLTk2Ont5gX0qrl+JO7K06Zh5ZJUlw0ESUSf3PeuZIyFKdNRSnV+tKJA7eyP7dDcX2f4nZ4RhV0WGFaDOlgvQZWus1UJ4iE3ufniyRoIUvvfXxwi2QNVHfaU4wUMdTrRPuq5ufpST6HWEldlGAswuvH0MQLFw3BdEdsmhEXqIMTDdK72En1bkpXuI4iji0TeC8M=";

        ciphertext = URLDecoder.decode(ciphertext, "UTF-8");

        String key = "Hfnuk#Client2022";

        try {
            String plaintext = decryptAES(ciphertext, key);
            System.out.println("Decrypted plaintext: " + plaintext);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
