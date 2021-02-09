package com.pb.dp.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pb.dp.exception.CipherException;

public class AES256Cipher {

	private static final Logger log = LoggerFactory.getLogger(AES256Cipher.class);
	private static final String ALGORITHM_AES256 = "AES/CBC/PKCS5Padding";
	private final byte[] key;
	private final byte[] ivByte;
	private final SecretKey secretKey;

	public AES256Cipher(String key, String iv) {
		this.key = key.getBytes();
		this.ivByte = iv.getBytes();
		secretKey = new SecretKeySpec(key.getBytes(), 0, key.length(), "AES");
	}

	public static void main(String[] args) throws UnsupportedEncodingException, CipherException {
		String messageToEncrypt = "GVHIyY1Po/TlFixTccgzEw==";
		AES256Cipher aes256Cipher = new AES256Cipher("LZO7CnfjzXD6g0mKVyV14MP1Ci2pBQXL", "FQSxN27aqtGNSdxY");
		//String encryptedMessage = aes256Cipher.encrypt(messageToEncrypt);
		String decryptedMessage = aes256Cipher.decrypt(messageToEncrypt);
//		System.out.println("Original Message: {" + messageToEncrypt + "}, Encrypted Message: {" + encryptedMessage
//				+ "}, Decrypted Message: {" + decryptedMessage + "}");
//		System.out.println("URL encoded:- " + URLEncoder.encode(encryptedMessage, "UTF-8"));
		System.out.println("decoded " + decryptedMessage);
		/*
		 * AES256Cipher myCipher = new AES256Cipher("18lk1sDd3XLVost4BIaPjptCvX2iCglR",
		 * "2bwAHNw889fG5qN8"); String mobileNo = myCipher.encrypt("9972429140");
		 * System.out.print("My encryption: "+mobileNo);
		 */
	}

	public String encrypt(String message) {
		byte[] cipherText = null;
		try {
			cipherText = encrypt(message.getBytes(), secretKey, ivByte);
		} catch (Exception e) {
			log.error("Unable to set key and iv pair for message :{}, error :{}:: ", message, e.getMessage());
		}
		return Base64.getEncoder().encodeToString(cipherText);
	}

	public String decrypt(String message) {
		String decryptedText = null;
		try {
			byte[] cipherText = Base64.getDecoder().decode(message);
			decryptedText = decrypt(cipherText, secretKey, ivByte);
		} catch (Exception e) {
			log.error("Unable to set key and iv pair for message :{}, error :{}:: ", message, e.getMessage());
		}
		return decryptedText;
	}

	private byte[] encrypt(byte[] plaintext, SecretKey key, byte[] IV) throws Exception {
		// Get Cipher Instance
		Cipher cipher = Cipher.getInstance(ALGORITHM_AES256);
		SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), "AES");
		IvParameterSpec ivSpec = new IvParameterSpec(IV);
		cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
		byte[] cipherText = cipher.doFinal(plaintext);
		return cipherText;
	}

	private String decrypt(byte[] cipherText, SecretKey key, byte[] IV) throws Exception {
		Cipher cipher = Cipher.getInstance(ALGORITHM_AES256);
		SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), "AES");
		IvParameterSpec ivSpec = new IvParameterSpec(IV);
		cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
		byte[] decryptedText = cipher.doFinal(cipherText);
		return new String(decryptedText);
	}
}
