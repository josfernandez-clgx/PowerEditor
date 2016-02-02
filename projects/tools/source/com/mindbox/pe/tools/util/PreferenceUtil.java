package com.mindbox.pe.tools.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import com.mindbox.pe.tools.PowerEditorTool;
import com.mindbox.pe.tools.db.DBConnInfo;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 */
public class PreferenceUtil {

	private static final String PREF_SAVED_SESSIONS = "sessions";

	private static Set<DBConnInfo> savedConnInfoList = new HashSet<DBConnInfo>();

	public static DBConnInfo[] getSavedDBInfo() {
		//Arrays.sort(info);
		return getSavedDBInfo_internal();
	}

	private static DBConnInfo[] getSavedDBInfo_internal() {
		DBConnInfo[] info = (DBConnInfo[]) savedConnInfoList.toArray(new DBConnInfo[0]);
		return info;
	}

	public static void readSavedDBInfo() {
		byte[] savedBytes = getPreferences().getByteArray(PREF_SAVED_SESSIONS, null);
		if (savedBytes != null && savedBytes.length > 0) {
			InputStream in = new ByteArrayInputStream(savedBytes);
			try {
				ObjectInputStream ois = new ObjectInputStream(in);
				DBConnInfo[] info = (DBConnInfo[]) ois.readObject();
				for (int i = 0; i < info.length; i++) {
					savedConnInfoList.add(info[i]);
				}
			}
			catch (Exception e) {
				e.printStackTrace(System.err);
				SwingUtil.showError("Failed to read saved connections:\n  " + e.getMessage());
			}
		}
	}

	public static boolean addToPreference(DBConnInfo info) {
		if (info != null) { return savedConnInfoList.add(info); }
		return false;
	}

	public static void initialize() {
		getPreferences();
	}
	
	private static final Preferences getPreferences() {
		return Preferences.systemNodeForPackage(PowerEditorTool.class);
	}

	public static void savePreferences() {
		try {
			// save previous sessions
			saveSavedSessions();
			getPreferences().flush();
		}
		catch (Exception ex) {
		}
	}

	private static void saveSavedSessions() {
		try {
			DBConnInfo[] info = getSavedDBInfo_internal();
			System.out.println("saving " + info.length + " saved sessions");

			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bo);
			out.writeObject(info);
			out.flush();

			getPreferences().putByteArray(PREF_SAVED_SESSIONS, bo.toByteArray());
		}
		catch (Exception ex) {
			Logger.getLogger("PEDataRepairor").log(Level.WARNING, "Failed to persist saved sessions: " + ex.getMessage(), ex);
			ex.printStackTrace(System.err);
		}
	}

	private PreferenceUtil() {
	}

}