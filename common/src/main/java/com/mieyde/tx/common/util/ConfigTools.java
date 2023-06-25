package com.mieyde.tx.common.util;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * 密钥工具
 *
 * @author 我吃稀饭面
 * @date 2023/6/25 22:06
 */
public class ConfigTools {

    /**
     * 生成 key pair
     */
    public static KeyPair getKeyPair(){
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.genKeyPair();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    /**
     * 根据keyPair获取公钥
     */
    public static String getPublicKey(KeyPair keyPair){
        PublicKey publicKey = keyPair.getPublic();
        byte[] bytes = publicKey.getEncoded();
        return byteToBase64(bytes);
    }

    /**
     * 根据keyPair获取私钥
     * @param keyPair
     * @return
     */
    public static String getPrivateKey(KeyPair keyPair){
        PrivateKey privateKey = keyPair.getPrivate();
        byte[] bytes = privateKey.getEncoded();
        return byteToBase64(bytes);
    }

    /**
     * 给定公钥字符串生成公钥对象
     */
    public static PublicKey stringToPublicKey(String publicKeyStr){
        try {
            byte[] keyBytes = base64ToByte(publicKeyStr);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpec);
        }catch (Exception e){
            return null;
        }
    }

    /**
     * 给定私钥字符串生成私钥对象
     */
    public static PrivateKey stringToPrivateKey(String privateKeyStr){
        try {
            byte[] keyBytes = base64ToByte(privateKeyStr);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);
        }catch (Exception e){
            return null;
        }
    }

    /**
     * 公钥加密内容
     */
    public static String publicEncrypt(String content,String publicKeyStr){
        try {
            PublicKey publicKey = stringToPublicKey(publicKeyStr);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE,publicKey);
            byte[] bytes = cipher.doFinal(content.getBytes());
            return byteToBase64(bytes);
        }catch (Exception e){
            return null;
        }
    }

    /**
     * 公钥解密内容
     */
    public static String publicDecrypt(String content,String publicKeyStr){
        try {
            PublicKey publicKey = stringToPublicKey(publicKeyStr);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE,publicKey);
            byte[] bytes = cipher.doFinal(base64ToByte(content));
            return new String(bytes,StandardCharsets.UTF_8);
        }catch (Exception e){
            return null;
        }
    }

    /**
     * 秘钥加密
     */
    public static String privateEncrypt(String content, String priStr) {
        try {
            PrivateKey privateKey = stringToPrivateKey(priStr);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            byte[] bytes = cipher.doFinal(content.getBytes());
            return byteToBase64(bytes);
        }catch (Exception e){
            return null;
        }
    }

    /**
     * 秘钥解密
     */
    public static String privateDecrypt(String content, String priStr) {
        try {
            PrivateKey privateKey = stringToPrivateKey(priStr);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] bytes = cipher.doFinal(base64ToByte(content));
            return new String(bytes, StandardCharsets.UTF_8);
        }catch (Exception e){
            return null;
        }
    }

    /**
     * base64编码
     */
    public static String byteToBase64(byte[] bytes) {
        return new String(Base64.getEncoder().encode(bytes), StandardCharsets.UTF_8);
    }

    /**
     * base64解码
     */
    public static byte[] base64ToByte(String base64Key){
        return Base64.getDecoder().decode(base64Key);
    }

    public static void main(String[] args) {
        KeyPair keyPair = ConfigTools.getKeyPair();
        String publicKey = ConfigTools.getPublicKey(keyPair);
        String privateKey = ConfigTools.getPrivateKey(keyPair);
        System.out.println("publicKey:"+publicKey);
        System.out.println("privateKey:"+privateKey);

        String data = "abadfasdfagasdfsadf";
        String byte2Base64Private1 = ConfigTools.privateEncrypt(data, privateKey);
        String byte2Base64Public2 = ConfigTools.publicEncrypt(data, publicKey);
        System.out.println(byte2Base64Private1);
        System.out.println(byte2Base64Public2);
    }
}
