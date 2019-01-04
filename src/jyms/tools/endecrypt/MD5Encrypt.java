/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jyms.tools.endecrypt;

/**
 *
 * @author John
 */

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MD5Encrypt {

	public static String getMD5Str(String str) {
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update(str.getBytes("UTF-8"));

		} catch (NoSuchAlgorithmException e) {
			System.out.println("NoSuchAlgorithmException caught!");
			System.exit(-1);
		} catch (UnsupportedEncodingException ex) { 
                    Logger.getLogger(MD5Encrypt.class.getName()).log(Level.SEVERE, null, ex);
                }
                
		byte[] byteArray = messageDigest.digest();
//                return new String(byteArray );
		StringBuilder md5StrBuff = new StringBuilder();
		for (int i = 0; i < byteArray.length; i++) {
			if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
				md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
			else
				md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
		}
		return md5StrBuff.toString().toUpperCase();
	}

	public static void main(String[] args) {
                String ss = MD5Encrypt.getMD5Str("BFEBFBFF000406E3.5CL2L82.CN486436C10456.");
                System.out.println(ss);
                System.out.println(ss.length());
//		System.out.println(MD5Encrypt.getMD5Str("123456"));
//                System.out.println(MD5Encrypt.getMD5Str("123456").length());
	}
}
