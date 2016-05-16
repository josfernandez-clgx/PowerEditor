package com.mindbox.pe.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Util class for converting password into one way hash.
 * Currently only MD5 hash is supported. Future versions would be able to take algorithm values from
 * config file.
 * @author vineet khosla
 * @since PowerEditor 5.1
 */
public final class PasswordOneWayHashUtil {

	/**
	 * MD5 Hash algorithhm.
	 */
	public static final String HASH_ALGORITHM_MD5 = "MD5";

	private static String convertToMD5Hash(String data) throws NoSuchAlgorithmException {
		if (data == null || data.equals("")) return null;

		byte[] defaultBytes = data.getBytes();
		try {
			MessageDigest algorithm = MessageDigest.getInstance("MD5");
			algorithm.reset();
			algorithm.update(defaultBytes);
			byte messageDigest[] = algorithm.digest();

			StringBuilder hexString = new StringBuilder();
			for (int i = 0; i < messageDigest.length; i++) {
				hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
			}
			data = hexString + "";
		}
		catch (NoSuchAlgorithmException nsae) {
		}
		return data;
	}

	/**
	 * Public method called to convert a string to one way hash. Currently only MD5 hash is supported.
	 * Use {@link #HASH_ALGORITHM_MD5}.
	 * @since PowerEditor 5.1
	 * @param clearTextPassword clearTextPassword
	 * @param algorithm algorithm
	 * @return one way hash string if <code>clearTextPassword</code> is not <code>null</code> and is not empty; <code>null</code>, otherwise
	 * @throws UnsupportedOperationException is thrown if algorithm requested is anything other than {@link #HASH_ALGORITHM_MD5}
	 * @throws NullPointerException if <code>algorithm</code> is <code>null</code>
	 */
	public static String convertToOneWayHash(String clearTextPassword, String algorithm) {
		if (algorithm.equalsIgnoreCase(HASH_ALGORITHM_MD5)) {
			try {
				return convertToMD5Hash(clearTextPassword);
			}
			catch (NoSuchAlgorithmException e) {
				throw new UnsupportedOperationException(e.getMessage());
			}
		}
		else {
			throw new UnsupportedOperationException("THIS ALGORITHM IS NOT CURRENTLY SUPPORTED");
		}
	}

	private PasswordOneWayHashUtil() {
	}
}
