package com.mindbox.pe.common;

public class VersionUtil {

	public static boolean isNewer(final String version1, final String version2) {
		if (version1.equals(version2)) {
			return false;
		}
		else {
			// assume version has two dots
			final String[] strs1 = version1.split("\\.");
			final String[] strs2 = version2.split("\\.");
			final int major1 = Integer.valueOf(strs1[0]);
			final int major2 = Integer.valueOf(strs2[0]);
			if (major1 > major2) {
				return true;
			}
			if (major1 < major2) {
				return false;
			}
			else {
				final int minor1 = Integer.valueOf(strs1[1]);
				final int minor2 = Integer.valueOf(strs2[1]);
				if (minor1 > minor2) {
					return true;
				}
				else if (minor1 < minor2) {
					return false;
				}
				else {
					return strs1[2].compareTo(strs2[2]) > 0;
				}
			}
		}
	}

	private VersionUtil() {
	}
}
