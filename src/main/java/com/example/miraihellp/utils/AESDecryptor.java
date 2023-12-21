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


        String ciphertext="IJqqUNKO/m0DQGo8DfyAcRylSwxe/B69ZKpTEiWxkG0vJ//mfvaHU0ampNoTddq12RApGKKhw4MZHE1iQOwKOfOn1UWkY7bCg6SFE0/elugcmnt1vedGATH9PVB4FSMIxWsKAq6VQFvPxnmLH9xa4Q==";

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
