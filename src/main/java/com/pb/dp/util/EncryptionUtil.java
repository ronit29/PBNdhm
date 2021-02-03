package com.pb.dp.util;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pb.dp.exception.CipherException;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This utility class is to support encoding and encryption operations
 * 
 * @author kanupriya
 *
 */
public class EncryptionUtil {

	private static final Logger logger = LoggerFactory.getLogger(EncryptionUtil.class);

	public static String decryptAES128InController(String val) throws CipherException {
		return AESCipher.decrypt(val);
	}

	public static String decryptAES256(String val) throws CipherException {
		return AESCipher.decrypt(val);
	}

	public static String decryptAES128Filter(String val) throws CipherException {
		try {
			return AESCipher.decrypt(URLDecoder.decode(val, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			logger.error("error in decrypt :: " + e.getMessage());
		}
		return null;
	}

	public static String decodeBase64String(String val) {
		byte[] decodedString = Base64.decodeBase64(val.getBytes());
		return new String(decodedString);
	}

	public static String encodeStringToBase64(String val) {
		return new String(Base64.encodeBase64(val.getBytes()));
	}

	public static void main(String[] args) throws CipherException {
		System.out.println(decryptSHA256Filter("amsbnfdoad|cId=117;clientKey=assc34234d;mapping=[{docCategory=ID Proof;docCategoryId=1;docType=Passport;docTypeId=2;idx=0;};{docCategory=ID Proof;docCategoryId=1;docType=aadhar ID;docTypeId=6;idx=1;}];refId=12344546768567;src=abc;|adssndf8302hd34ipdfn0834hf7934odiw3yd893"));
		System.out.println(decryptAES128InController("wsK3n7uIrucb8%2BD7fBMLjA%3D%3D"));
	}

	public static String decryptSHA256Filter(String val) throws CipherException {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(val.getBytes(StandardCharsets.UTF_8));
			StringBuffer hexString = new StringBuffer();

			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if (hex.length() == 1)
					hexString.append('0');
				hexString.append(hex);
			}

			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			logger.error("error in decrypt :: " + e.getMessage());
		}
		return null;
	}
}
