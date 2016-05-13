package com.mindbox.pe.server.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.mindbox.pe.model.PeDataType;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.GuidelineContext;
import com.mindbox.pe.model.grid.ProductGrid;
import com.mindbox.pe.server.model.LockException;
import com.mindbox.pe.server.model.LockInfo;
import com.mindbox.pe.server.model.User;

/**
 * Manages Locks on various entities.
 * 
 * @since PowerEditor 1.0
 */
public class LockManager extends AbstractCacheManager {

	private static LockManager sSingleton = null;

	public static synchronized LockManager getInstance() {
		if (sSingleton == null) sSingleton = new LockManager();
		return sSingleton;
	}

	private interface Lock {

		int hashCode();

		boolean equals(Object obj);
	}

	/**
	 * Immutable product grid lock object.
	 * 
	 * @since PowerEditor 1.0
	 */
	private final class ProductGridLock implements Lock {

		private final int templateID;
		private final int hashCode;
		private final int gridID;

		public int hashCode() {
			return hashCode;
		}

		public boolean equals(Object obj) {
			if (obj instanceof ProductGridLock) {
				ProductGridLock productgridlock = (ProductGridLock) obj;
				return this.templateID == productgridlock.templateID && this.gridID == productgridlock.gridID;
			}
			else {
				return false;
			}
		}

		private ProductGridLock(int templateID, int gridID) {
			this.templateID = templateID;
			this.gridID = gridID;
			this.hashCode = (templateID + ":" + gridID).hashCode();
		}

		private ProductGridLock(ProductGrid productgrid) {
			this(productgrid.getTemplateID(), productgrid.getID());
		}
	}

	private final Map<ProductGridLock, LockInfo> productGridLockMap;
	private final Map<Integer, LockInfo> paramGridLockMap;
	private final Map<GenericEntityType, Map<Integer, LockInfo>> geneicEntityLockMap;
	private final Map<PeDataType, Map<Integer, LockInfo>> entityLockMap;
	private final Map<PeDataType, Map<String, LockInfo>> namedEntityLockMap;

	private LockManager() {
		geneicEntityLockMap = Collections.synchronizedMap(new HashMap<GenericEntityType, Map<Integer, LockInfo>>());
		entityLockMap = Collections.synchronizedMap(new HashMap<PeDataType, Map<Integer, LockInfo>>());
		namedEntityLockMap = Collections.synchronizedMap(new HashMap<PeDataType, Map<String, LockInfo>>());
		productGridLockMap = Collections.synchronizedMap(new HashMap<ProductGridLock, LockInfo>());
		paramGridLockMap = Collections.synchronizedMap(new HashMap<Integer, LockInfo>());
	}

	public LockInfo lock(ProductGrid productgrid, User user) throws LockException {
		LockInfo lockinfo = getExistingLock(productgrid, user);
		if (lockinfo == null) {
			ProductGridLock productgridlock = new ProductGridLock(productgrid);
			lockinfo = new LockInfo(user, new Date());
			productGridLockMap.put(productgridlock, lockinfo);
		}
		return lockinfo;
	}

	public LockInfo lockProductGrid(int templateID, GuidelineContext[] context, User user) throws LockException {
		if (templateID < 0) throw new IllegalArgumentException("Invalid template id: " + templateID);
		List<ProductGrid> gridList = GridManager.getInstance().getProductGrids(templateID, context);
		LockInfo lockInfo = null;
		List<ProductGrid> lockedList = new ArrayList<ProductGrid>();
		try {
			for (Iterator<ProductGrid> iter = gridList.iterator(); iter.hasNext();) {
				ProductGrid element = iter.next();
				lockInfo = lock(element, user);
				lockedList.add(element);
			}
			return lockInfo;
		}
		catch (LockException ex) {
			for (Iterator<ProductGrid> iter = lockedList.iterator(); iter.hasNext();) {
				ProductGrid element = iter.next();
				unlock(element, user);
			}
			throw ex;
		}
	}

	public LockInfo lockParameterGrid(int paramGridID, User user) throws LockException {
		if (paramGridID < 0) throw new IllegalArgumentException("Invalid paramter grid id: " + paramGridID);
		if (user == null) throw new NullPointerException("User cannot be null");

		LockInfo lockinfo = getExistingParameterGridLock(paramGridID, user);
		if (lockinfo == null) {
			lockinfo = new LockInfo(user, new Date());
			paramGridLockMap.put(new Integer(paramGridID), lockinfo);
		}
		return lockinfo;
	}

	public LockInfo lock(GenericEntityType entityType, int entityID, User user) throws LockException {
		if (entityID < 0) throw new IllegalArgumentException("Invalid entity id: " + entityID);
		if (user == null) throw new NullPointerException("User cannot be null");

		LockInfo lockinfo = getExistingLock(entityType, entityID, user);
		if (lockinfo == null) {
			lockinfo = new LockInfo(user, new Date());
			getGenericEntityLockMap(entityType).put(new Integer(entityID), lockinfo);
		}
		return lockinfo;
	}

	public LockInfo lock(PeDataType entityType, int entityID, User user) throws LockException {
		if (entityID < 0) throw new IllegalArgumentException("Invalid entity id: " + entityID);
		if (user == null) throw new NullPointerException("User cannot be null");

		LockInfo lockinfo = getExistingLock(entityType, entityID, user);
		if (lockinfo == null) {
			lockinfo = new LockInfo(user, new Date());
			getEntityLockMap(entityType).put(new Integer(entityID), lockinfo);
		}
		return lockinfo;
	}

	public LockInfo lock(PeDataType entityType, String name, User user) throws LockException {
		if (user == null) throw new NullPointerException("name cannot be null");
		LockInfo lockinfo = getExistingLock(entityType, name, user);
		if (lockinfo == null) {
			lockinfo = new LockInfo(user, new Date());
			getNamedEntityLockMap(entityType).put(name, lockinfo);
		}
		return lockinfo;
	}

	// ///////////////////////// Lock Check Methods ////////////////////////////

	public LockInfo getExistingLock(GenericEntityType entityType, int id, User user) throws LockException {
		if (id < 0) throw new IllegalArgumentException("Invalid entity id: " + id);
		return handleExistingLock((LockInfo) getGenericEntityLockMap(entityType).get(new Integer(id)), user);
	}

	public LockInfo getExistingLock(PeDataType entityType, int id, User user) throws LockException {
		if (id < 0) throw new IllegalArgumentException("Invalid entity id: " + id);
		return handleExistingLock((LockInfo) getEntityLockMap(entityType).get(new Integer(id)), user);
	}

	public LockInfo getExistingLock(PeDataType entityType, String name, User user) throws LockException {
		return handleExistingLock(getNamedEntityLockMap(entityType).get(name), user);
	}

	public LockInfo getExistingLock(ProductGrid productgrid, User user) throws LockException {
		ProductGridLock productgridlock = new ProductGridLock(productgrid);
		return handleExistingLock(productGridLockMap.get(productgridlock), user);
	}

	public LockInfo getExistingParameterGridLock(int gridID, User user) throws LockException {
		return handleExistingLock(paramGridLockMap.get(new Integer(gridID)), user);
	}

	public LockInfo getExistingProductGridLock(int templateID, GuidelineContext[] context, User user) throws LockException {
		logger.debug(">>> getExistingProductGridLock: " + templateID + " for " + user);
		List<ProductGrid> gridList = GridManager.getInstance().getProductGrids(templateID, context);
		LockInfo lockInfo = null;
		for (ProductGrid element : gridList) {
			lockInfo = getExistingLock(element, user);
		}
		return lockInfo;
	}

	// //////////////////////////// UnLock Methods /////////////////////////////

	public boolean unlock(GenericEntityType entityType, int entityID, User user) throws LockException {
		if (getExistingLock(entityType, entityID, user) != null) {
			return getGenericEntityLockMap(entityType).remove(new Integer(entityID)) != null;
		}
		else {
			return true;
		}
	}

	public boolean unlock(PeDataType entityType, int entityID, User user) throws LockException {
		LockInfo lockInfo = getExistingLock(entityType, entityID, user);
		if (lockInfo != null) {
			return getEntityLockMap(entityType).remove(new Integer(entityID)) != null;
		}
		else {
			return true;
		}
	}

	public boolean unlockParameterGrid(int gridID, User user) throws LockException {
		if (getExistingParameterGridLock(gridID, user) != null) {
			return paramGridLockMap.remove(new Integer(gridID)) != null;
		}
		else {
			return true;
		}
	}

	public boolean unlock(PeDataType entityType, String name, User user) throws LockException {
		if (getExistingLock(entityType, name, user) != null) {
			return getNamedEntityLockMap(entityType).remove(name) != null;
		}
		else {
			return true;
		}
	}

	public boolean unlock(ProductGrid productgrid, User user) throws LockException {
		if (getExistingLock(productgrid, user) != null) {
			// use lockKey to remove
			return productGridLockMap.remove(new ProductGridLock(productgrid)) != null;
		}
		else {
			return true;
		}
	}

	public boolean unlockProductGrid(int templateID, GuidelineContext[] context, User user) throws LockException {
		List<ProductGrid> gridList = GridManager.getInstance().getProductGrids(templateID, context);
		for (ProductGrid element : gridList) {
			if (!unlock(element, user)) {
				return false;
			}
		}
		return true;
	}

	private void removeAllLocks(Map<? extends Object, LockInfo> lockMap, User user) {
		List<Object> keyToRemoveList = new ArrayList<Object>();
		for (Iterator<?> iter = lockMap.keySet().iterator(); iter.hasNext();) {
			Object key = iter.next();
			LockInfo lockInfo = lockMap.get(key);
			if (user.equals(lockInfo.getLockedBy())) {
				keyToRemoveList.add(key);
			}
		}
		for (Iterator<Object> iter = keyToRemoveList.iterator(); iter.hasNext();) {
			Object key = iter.next();
			lockMap.remove(key);
		}
	}

	public void unlockAll(User user) {
		logger.debug(">>> unlockAll: " + user);
		if (user == null) {
			throw new NullPointerException("User cannot be null");
		}

		// remove all grid locks
		removeAllLocks(productGridLockMap, user);

		logger.info("unlockAll: guideline grid locks removed...");

		// remove all param grid locks
		removeAllLocks(paramGridLockMap, user);
		logger.info("unlockAll: parameter grid locks removed...");

		// remove all entity locks
		for (PeDataType entityType : entityLockMap.keySet()) {
			removeAllLocks(entityLockMap.get(entityType), user);
		}
		for (PeDataType entityType : namedEntityLockMap.keySet()) {
			removeAllLocks(namedEntityLockMap.get(entityType), user);
		}

		// remove all generic entity locks
		for (Iterator<GenericEntityType> iter = geneicEntityLockMap.keySet().iterator(); iter.hasNext();) {
			Object key = iter.next();
			logger.debug("unlockAll: removing entity locks for " + key);
			removeAllLocks(geneicEntityLockMap.get(key), user);
		}
		logger.debug("<< unlockAll");
	}

	// /////////////////////////// Other Methods ///////////////////////////////

	private Map<Integer, LockInfo> getGenericEntityLockMap(GenericEntityType entityType) {
		Map<Integer, LockInfo> map = geneicEntityLockMap.get(entityType);
		if (map == null) {
			map = Collections.synchronizedMap(new HashMap<Integer, LockInfo>());
			geneicEntityLockMap.put(entityType, map);
		}
		return map;
	}

	private Map<Integer, LockInfo> getEntityLockMap(PeDataType entityType) {
		Map<Integer, LockInfo> map = entityLockMap.get(entityType);
		if (map == null) {
			map = Collections.synchronizedMap(new HashMap<Integer, LockInfo>());
			entityLockMap.put(entityType, map);
		}
		return map;
	}

	private Map<String, LockInfo> getNamedEntityLockMap(PeDataType entityType) {
		Map<String, LockInfo> map = namedEntityLockMap.get(entityType);
		if (map == null) {
			map = Collections.synchronizedMap(new HashMap<String, LockInfo>());
			namedEntityLockMap.put(entityType, map);
		}
		return map;
	}

	private LockInfo handleExistingLock(LockInfo lockinfo, User user) throws LockException {
		if (lockinfo == null) return null;
		logger.debug(">>> handleExistingLock: " + lockinfo + " for " + user);
		if (lockinfo.getLockedBy().equals(user)) {
			logger.debug("<<< handleExistingLock with " + lockinfo);
			return lockinfo;
		}
		else {
			throw new LockException("Already Locked", lockinfo.getLockedBy().getName());
		}
	}

	public String toString() {
		return "LockManager[entityLockTypes=" + geneicEntityLockMap.size() + ",prodGridLocks=" + productGridLockMap.size() + "]";
	}

}