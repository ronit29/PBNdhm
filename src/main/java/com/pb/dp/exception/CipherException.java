package com.pb.dp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Unable to do Encryption/Decryption due to bad input.")
public class CipherException extends Exception {

	private static final long serialVersionUID = 7498345931100306058L;

	public CipherException() {
		super("Unable to do Encryption/Decryption");
	}

	public CipherException(String input) {
		super("Unable to do Encryption/Decryption for bad input string [ " + input + " ]");
	}

}
