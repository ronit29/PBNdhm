package com.pb.dp.util;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.BaseEncoding;
import com.pb.dp.exception.CipherException;


public class AESCipher {

	private static final Logger log = LoggerFactory.getLogger(AESCipher.class);

	private static String keyString = EncryptionUtil.decodeBase64String("PSVJQRk9QTEpNVU1DWUZCRVFGV1VVT0ZOV1RRU1NaWQ=");
	private static String ivString = EncryptionUtil.decodeBase64String("WVdsRkxWRVpaVUZOYVdsaA==");
	

	private static final String ALGORITHM_AES256 = "AES/CBC/PKCS5Padding";
	private static SecretKeySpec secretKeySpec;
	private static Cipher cipher;
	private static IvParameterSpec iv;

	private static void setIvPair(byte[] key, byte[] iv) throws CipherException {
		try {
			AESCipher.secretKeySpec = new SecretKeySpec(key, "AES");
			AESCipher.iv = new IvParameterSpec(iv);
			AESCipher.cipher = Cipher.getInstance(ALGORITHM_AES256);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			log.debug("Unable to set key and iv pair :: " + e.getMessage());
			throw new CipherException();
		}
	}

	private static Cipher getCipher(int encryptMode) throws InvalidKeyException, InvalidAlgorithmParameterException {
		cipher.init(encryptMode, getSecretKeySpec(), iv);
		return cipher;
	}

	private static SecretKeySpec getSecretKeySpec() {
		return secretKeySpec;
	}

	public static String encrypt(String message) throws CipherException {
		try {
			setIvPair(keyString.getBytes("UTF-8"), ivString.getBytes("UTF-8"));
			Cipher cipher = getCipher(Cipher.ENCRYPT_MODE);
			byte[] encryptedTextBytes = cipher.doFinal(message.getBytes("UTF-8"));
			return BaseEncoding.base64().encode(encryptedTextBytes);
		} catch (IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException | InvalidKeyException | InvalidAlgorithmParameterException e) {
			log.debug("Unable to encrypt message :: " + e.getMessage());
			throw new CipherException(message);
		}
	}

	public static String decrypt(String message) throws CipherException {
		try {
			if (message != null && message.matches("^[A-Za-z0-9-\\+]*\\s[A-Za-z0-9-\\+]*==$")) {
				message = message.replaceAll(" ", "+");
				throw new CipherException("'" + message + "' must be URL encoded.");
			}
			setIvPair(keyString.getBytes("UTF-8"), ivString.getBytes("UTF-8"));
			Cipher cipher = getCipher(Cipher.DECRYPT_MODE);
			byte[] encryptedTextBytes = BaseEncoding.base64().decode(message);
			byte[] decryptedTextBytes = cipher.doFinal(encryptedTextBytes);
			return new String(decryptedTextBytes);
		} catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | UnsupportedEncodingException | IllegalArgumentException e) {
			log.debug("Unable to decrypt message :: " + e.getMessage());
			throw new CipherException(message);
		}
	}

	public static void main(String[] args) throws UnsupportedEncodingException, CipherException {
		String messageToEncrypt = "2669150";
		String encryptedMessage = encrypt(messageToEncrypt);
		String decryptedMessage = decrypt(encryptedMessage);
		System.out.println("Original Message: {" + messageToEncrypt + "}, Encrypted Message: {" + encryptedMessage + "}, Decrypted Message: {"
				+ decryptedMessage + "}");
		System.out.println("URL encoded:- "+URLEncoder.encode(encryptedMessage, "UTF-8"));
	}
}
