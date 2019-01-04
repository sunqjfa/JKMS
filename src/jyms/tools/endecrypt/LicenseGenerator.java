/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jyms.tools.endecrypt;

import java.io.File;
import jyms.data.TxtLogger;
import jyms.tools.FileUtil;


public class LicenseGenerator {
    
    private static final String FILE_NAME = "LicenseGenerator.java";
    
    
    private static final String PUBLICKEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCBgYuypXW+/3+62HyZKJK7Q6njB+qIZiTpv1pj\n"
                                          + "CtgQI4GKMjfUJrXy9uy9BsGjlpu6Sw11DERO9L834QF2dsDRWncGwnPLPyCc/lBWUbY26GlnBDvr\n"
                                          + "9mwdc6tUjD6fKSCFAn01Qv76KznsoRP5/sGm5zbl76pJKTwaRqyWEoKcQwIDAQAB";
    
    /**
     * RSA算法
     * 公钥和私钥是一对，此处只用私钥加密
     */
    public static final String PRIVATEKEY = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAIGBi7Kldb7/f7rYfJkokrtDqeMH\n"
                                          + "6ohmJOm/WmMK2BAjgYoyN9QmtfL27L0GwaOWm7pLDXUMRE70vzfhAXZ2wNFadwbCc8s/IJz+UFZR\n" 
                                          + "tjboaWcEO+v2bB1zq1SMPp8pIIUCfTVC/vorOeyhE/n+wabnNuXvqkkpPBpGrJYSgpxDAgMBAAEC\n"
                                          + "gYA/BuQNCneWf9QTA/8HrvZSSujoQ9cBkOyQEf+UShfDKvSFZFFFX52XZFLs39fnnImnjZU8DL9F\n"
                                          + "ydkyAcBrffnydMitocK3Bsx5/8tK7R4d3vFkJk8JBYDnJrBXTmZwQ7CZZk1UVOkEY7dExfs+6o2b\n"
                                          + "sHJGTuTQVkSaBfnKk9uJ+QJBAN0NGLQqM/7EoYbFTPVV1wlSVWurzPjuLWRs4pKouu26HrAeavV9\n" 
                                          + "hZ4tKwAkzGHKJh1BRZ4q1k0rdUyzSAg6310CQQCV+zgktdPi4Wv4TqoqSrIJXNtjc4M18yYrGXGf\n" 
                                          + "tWnkwb0acedldBbKGdPMW0szKNCgXgbae3tXwmbAbS9FHtAfAkEArHpLjEhhyagjYMUSB1uzLdLQ\n" 
                                          + "U/L5PEKbqIBNSaHeOR/AUitpyaLnX0RfCP2te5//nVQQvhkunYgLPo2k5scJAQJAf4Wh3/zGExOO\n" 
                                          + "BQuFvf3S0Qkl9LJjrvx1yJGSvbO3POzJx/FvSSDiu6YoIorBLteWQ7SO6Tey0RSW3yg21/NyCwJB\n" 
                                          + "ALQOAx6BQVK348osgwbw1q6JzdkLXOmAeM3eV5mPztfqGvKZfRILc38RENcqc9f87uYAZjfdJSSf\n" 
                                          + "joLyX25l+lg=";
    
    
    /**
        * 函数:      generateLicense
        * 函数描述:  产生注册码
        * @param MachineCode   机器码
        * @return String 注册码，失败返回""
    */
    public static String generateLicense(String MachineCode){
        try{
            byte[] data = MachineCode.getBytes();
            byte[] encodedData = RSAUtils.encryptByPrivateKey(data, PRIVATEKEY);
            String License = byte2hex(encodedData);
            if (License.length() > 256) License = License.substring(0, 256);
            return License;
        }catch (Exception e){
            TxtLogger.append("", "generateLicense()","系统在产生注册码过程中，出现错误"
                         + "\r\n                       Exception:" + e.toString());
        }
        return "";
    }
    
    /** 
        * byte转哈希 
        * @param b 
        * @return 
    */  
    public static  String byte2hex(byte[] b) {  
        String hs = "";  
        String stmp = "";  
        for (int n = 0; n < b.length; n++) {  
            stmp = Integer.toHexString(b[n] & 0xFF);  
            if (stmp.length() == 1)  
                hs += ("0" + stmp);  
            else  
                hs += stmp;  
        }  
        return hs.toUpperCase();  
    } 
    /** 
        * 哈希转byte 
        * @param b 
        * @return 
    */  
    public  byte[] hex2byte(byte[] b) {  
        if ((b.length % 2) != 0)  
            throw new IllegalArgumentException("长度不是偶数");  

        byte[] b2 = new byte[b.length / 2];  

        for (int n = 0; n < b.length; n += 2) {  
            String item = new String(b, n, 2);  
            b2[n / 2] = (byte) Integer.parseInt(item, 16);  
        }  
        return b2;  
    } 
    
    
    public static void generator() throws Exception {
        /**
        * serial：由客户提供
        * timeEnd：过期时间
        */
         String licensestatic = "serial=568b8fa5cdfd8a2623bda1d8ab7b7b34;" +
                                          "timeEnd=1404057600000";
        System.err.println("私钥加密——公钥解密");
        licensestatic = "A81BDAFC5C069BE9B90667F22BF2197B";
        System.out.println("原文字：\r\n" + licensestatic);
        byte[] data = licensestatic.getBytes();
        byte[] encodedData = RSAUtils.encryptByPrivateKey(data, PRIVATEKEY);
        System.out.println("加密后：\r\n" + new String(encodedData)); //加密后乱码是正常的
        System.out.println("加密后：\r\n" + byte2hex(encodedData)); //加密后乱码是正常的
        
        Base64Utils.byteArrayToFile(encodedData, FileUtil.getBasePath()+File.separator+"license.dat");
        System.out.println("license.dat：\r\n" + FileUtil.getBasePath()+File.separator+"license.dat");
        
        //解密
        byte[] decodedData = RSAUtils.decryptByPublicKey(encodedData, PUBLICKEY);
        String target = new String(decodedData);
        System.out.println("解密后: \r\n" + target);
    }
    public static void main(String[] args) throws Exception {
        generator();
    }
}
