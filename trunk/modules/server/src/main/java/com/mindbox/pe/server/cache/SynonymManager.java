package com.mindbox.pe.server.cache;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.mindbox.pe.model.Synonym;

public class SynonymManager extends AbstractCacheManager {

	public void startLoading() {
		mSynonymHash.clear();
	}

	private SynonymManager() {
		mSynonymHash = new Hashtable<String, List<Synonym>>();
	}

	public String toString() {
		String s = "";
		s += "SynonymManager with " + mSynonymHash.size() + " Synonyms!";
		s += mSynonymHash.toString();
		return s;
	}

	public static void main(String args[]) {
		try {
			getInstance();
		}
		catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public boolean addSynonym(String s, String s1, String s2) {
		Synonym synonym = new Synonym(s, s1, s2);
		List<Synonym> obj = mSynonymHash.get(s2);
		if (obj == null) obj = new LinkedList<Synonym>();
		obj.add(synonym);
		mSynonymHash.put(s2, obj);
		return true;
	}

	public void finishLoading() {
	}

	public Set<String> fetchSynonymTypes() {
		return mSynonymHash.keySet();
	}

	public List<Synonym> fetchSynonyms(String s) {
		List<Synonym> list = mSynonymHash.get(s);
		return list;
	}

	public static synchronized SynonymManager getInstance() {
		if (mSingleton == null) mSingleton = new SynonymManager();
		return mSingleton;
	}

	private static SynonymManager mSingleton = null;
	private Hashtable<String, List<Synonym>> mSynonymHash;

}