package com.mindbox.pe.server.imexport.digest;

import net.sf.oval.constraint.MatchPattern;
import net.sf.oval.constraint.Min;
import net.sf.oval.constraint.NotNull;

import com.mindbox.pe.server.db.DBIdGenerator;

/**
 * Next ID Seed objects for updates.
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.5.0
 */
public class NextIDSeed {

	@Min(value=1)
	private int seed;
	
	@Min(value=1)
	private int cache;
	
	@NotNull
	@MatchPattern(pattern=DBIdGenerator.REGEX_VALID_ID_TYPES)
	private String type;

	public NextIDSeed() {
	}

	public NextIDSeed(String type, int seed, int cache) {
		this.type = type;
		this.seed = seed;
		this.cache = cache;
	}

	public int getCache() {
		return cache;
	}

	public int getSeed() {
		return seed;
	}

	public String getType() {
		return type;
	}

	public void setCache(int i) {
		cache = i;
	}

	public void setSeed(int i) {
		seed = i;
	}

	public void setType(String string) {
		type = string;
	}

	public String toString() {
		return "NextID[" + type + ",seed=" + seed + ",cache=" + cache + "]";
	}
}
