package com.mindbox.pe.server.config;

import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * A password as stored in any PE server configuration.
 * 
 * Instances are immutable.
 */
public final class Password {
	private final String clearText;
	private final String encrypted;

	/** Factory method for creating a <code>Password</code> from clear text. */
	public static Password fromClearText(String pwd) {
		return new Password(pwd, false);
	}

	/** Package-private factory method for creating a <code>Password</code> from an encrypted string (as stored in a config file, for example). */
	static Password fromEncryptedString(String pwd) {
		return new Password(pwd, true);
	}
	
	/** @param encrypted Specifies whether the <code>pwd</code> argument is encrypted. */
	private Password(String pwd, boolean encrypted) {
		if (pwd == null) {
			throw new NullPointerException("Null password.");
		}
		
		this.encrypted = encrypted ? pwd : encrypt(pwd);
		this.clearText = encrypted ? decrypt(pwd) : pwd;
	}
	
	String getClearText() {
		return clearText;
	}
	
	public String getEncrypted() {
		return encrypted;
	}
	
	public String toString() {
		return encrypted;
	}
	
	/*
	 * Below is the cryptography for server passwords.
	 * 
	 * N.B. The requirement driving the use of encryption merely states that PE must not store clear text passwords in config files.
	 * We understand that including in source files all the necessary information for decrypting such passwords is not highly secure.
	 * But it does fully meet the requirement and has been approved by the business analyst.    
	 */
	private static final String ALGORITHM = "DES";
	private static final String FEEDBACK_MODE = "ECB";
	private static final String PADDING_SCHEME = "PKCS5Padding";
	private static final String CIPHER_TRANSFORMATION = ALGORITHM + '/' + FEEDBACK_MODE + '/' + PADDING_SCHEME;
	private static final Cipher CIPHER; 
	private static final String UTF8 = "UTF8";
	private static final Base64 PRINTABLE_CHAR_TRANSFORMER = new Base64();
	private static final String KEY_STRING = "987sd\\]['; 	k;lAERTagj;9879q345dfasdfwer234gvaf;lasdwearfeiodsfajlk;rf";
	private static final SecretKey KEY;
	
	static {
		try {
			byte[] keyBytes = KEY_STRING.getBytes(UTF8);
			KeySpec keySpec = new DESKeySpec(keyBytes);
			KEY = SecretKeyFactory.getInstance(ALGORITHM).generateSecret(keySpec);
			
			CIPHER = Cipher.getInstance(CIPHER_TRANSFORMATION);
		} catch (Exception e) {
			throw new ExceptionInInitializerError();
		}
	}

	private synchronized String encrypt(String clearText) {
		try {
			Cipher encrypter = getCipher(Cipher.ENCRYPT_MODE);
			byte[] clearBytes = clearText.getBytes(UTF8);
			byte[] encryptedBytes = encrypter.doFinal(clearBytes);
			return bytesToPrintableString(encryptedBytes);
		} catch (Exception e) {
			throw new RuntimeException("Error encrypting password: " + e.getMessage()); // don't give stack trace, it includes info about algorithm
		}
    }
	private synchronized String decrypt(String encrypted) {
		try {
			Cipher decrypter = getCipher(Cipher.DECRYPT_MODE);
			byte[] encryptedBytes = bytesFromPrintableString(encrypted);
			byte[] decryptedBytes = decrypter.doFinal(encryptedBytes);
			return new String(decryptedBytes, UTF8);
		} catch (Exception e) {
			throw new RuntimeException("Error decrypting password. Use Power Editor PasswordTool to encrypt passwords stored in configuration files.");
		}
	}

	private synchronized Cipher getCipher(int mode) throws Exception {
		CIPHER.init(mode, KEY);
		return CIPHER;
	}
	
	private String bytesToPrintableString(byte[] bs) {
		return new String(PRINTABLE_CHAR_TRANSFORMER.encode(bs));
	}

	private byte[] bytesFromPrintableString(String s) {
		return PRINTABLE_CHAR_TRANSFORMER.decode(s.getBytes());
	}
}
