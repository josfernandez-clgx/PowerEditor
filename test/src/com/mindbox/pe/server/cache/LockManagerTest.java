package com.mindbox.pe.server.cache;

import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithTestConfig;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.model.EntityType;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.model.ProductGrid;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.server.model.LockException;
import com.mindbox.pe.server.model.LockInfo;
import com.mindbox.pe.server.model.User;

/**
 * Lock Manager test cases.
 * 
 * @author Geneho Kim
 * @since PowerEditor 4.3.7
 */
public class LockManagerTest extends AbstractTestWithTestConfig {

	public static TestSuite suite() {
		TestSuite suite = new TestSuite(LockManagerTest.class);
		suite.setName("Lock Manager Tests");
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	private User user = null;

	private LockManager lockManager;

	/**
	 * @param name
	 */
	public LockManagerTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		config.initServer();
		lockManager = LockManager.getInstance();
		user = new User("testuser", "Test User", "pwd", false, 0, null);
	}

	protected void tearDown() throws Exception {
		lockManager.unlockAll(user);
		config.resetConfiguration();
		user = null;
		lockManager = null;
		super.tearDown();
	}

	public void testUnlockAllUnlocksGenericEntityIDLocks() throws Exception {
		int entityID = ObjectMother.createInt();
		lockManager.lock(GenericEntityType.getAllGenericEntityTypes()[0], entityID, user);
		// sanity check
		assertNotNull(lockManager.getExistingLock(GenericEntityType.getAllGenericEntityTypes()[0], entityID, user));

		lockManager.unlockAll(user);
		assertNull(lockManager.getExistingLock(GenericEntityType.getAllGenericEntityTypes()[0], entityID, user));
	}

	public void testUnlockAllUnlocksEntityIDLocks() throws Exception {
		int templateID = ObjectMother.createInt();
		lockManager.lock(EntityType.TEMPLATE, templateID, user);
		// sanity check
		assertNotNull(lockManager.getExistingLock(EntityType.TEMPLATE, templateID, user));

		lockManager.unlockAll(user);
		assertNull(lockManager.getExistingLock(EntityType.TEMPLATE, templateID, user));
	}

	public void testUnlockAllUnlocksEntityNameLocks() throws Exception {
		String name = ObjectMother.createString();
		lockManager.lock(EntityType.TEMPLATE, name, user);
		// sanity check
		assertNotNull(lockManager.getExistingLock(EntityType.TEMPLATE, name, user));

		lockManager.unlockAll(user);
		assertNull(lockManager.getExistingLock(EntityType.TEMPLATE, name, user));
	}

	public void testUnlockAllUnlocksGuidelineGridLocks() throws Exception {
		ProductGrid grid = ObjectMother.createGuidelineGrid(ObjectMother.createGridTemplate(TemplateUsageType.getAllInstances()[0]));
		lockManager.lock(grid, user);
		// sanity check
		assertNotNull(lockManager.getExistingLock(grid, user));

		lockManager.unlockAll(user);
		assertNull(lockManager.getExistingLock(grid, user));
	}

	public void testUnlockAllUnlocksParameterGridLocks() throws Exception {
		int gridID = ObjectMother.createInt();
		lockManager.lockParameterGrid(gridID, user);
		// sanity check
		assertNotNull(lockManager.getExistingParameterGridLock(gridID, user));

		lockManager.unlockAll(user);
		assertNull(lockManager.getExistingParameterGridLock(gridID, user));
	}

	public void testGetExistingLockForGridWithNullGridThrowsNullPointerException() throws Exception {
		try {
			lockManager.getExistingLock(null, user);
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException e) {
		}
	}

	public void testGetExistingLockForGridWithUnlockedEmptyGridReturnsNull() throws Exception {
		ProductGrid grid = new ProductGrid(1000, new GridTemplate(100, "test", TemplateUsageType.getAllInstances()[0]), null, null);
		assertNull(lockManager.getExistingLock(grid, user));
	}

	public void testGetExistingLockForGridWithLockedEmptyGridReturnsValidLock() throws Exception {
		ProductGrid grid = new ProductGrid(1000, new GridTemplate(100, "test", TemplateUsageType.getAllInstances()[0]), null, null);
		lockManager.lock(grid, user);

		LockInfo lockInfo = lockManager.getExistingLock(grid, user);
		assertNotNull(lockInfo);
		assertEquals(user, lockInfo.getLockedBy());
	}

	public void testGetExistingLockForGridWithLockedEmptyGridByAnotherUserThrowsLockException() throws Exception {
		ProductGrid grid = new ProductGrid(1000, new GridTemplate(100, "test", TemplateUsageType.getAllInstances()[0]), null, null);
		lockManager.lock(grid, user);
		try {
			lockManager.getExistingLock(grid, new User("user2", "user2", "pwd", false, 0, null));
			fail("Expected LockException not thrown");
		}
		catch (LockException e) {
		}
	}

	public void testGetExistingLockForGridWithUnlockedNonEmptyGridReturnsNull() throws Exception {
		ProductGrid grid = new ProductGrid(1000, new GridTemplate(100, "test", TemplateUsageType.getAllInstances()[0]), null, null);
		grid.setGenericEntityIDs(GenericEntityType.forName("channel"), new int[] { 3, 4, 5, 6, 7 });
		grid.setGenericCategoryIDs(GenericEntityType.forName("product"), new int[] { 11 });
		assertNull(lockManager.getExistingLock(grid, user));
	}

	public void testGetExistingLockForGridWithLockedNonEmptyGridByAnotherUserThrowsLockException() throws Exception {
		ProductGrid grid = new ProductGrid(1000, new GridTemplate(100, "test", TemplateUsageType.getAllInstances()[0]), null, null);
		grid.setGenericEntityIDs(GenericEntityType.forName("channel"), new int[] { 3, 4, 5, 6, 7 });
		grid.setGenericCategoryIDs(GenericEntityType.forName("product"), new int[] { 11 });
		lockManager.lock(grid, user);

		LockInfo lockInfo = lockManager.getExistingLock(grid, user);
		assertNotNull(lockInfo);
		assertEquals(user, lockInfo.getLockedBy());
	}

	public void testGetExistingLockForGridWithLockedNonEmptyGridReturnsValidLock() throws Exception {
		ProductGrid grid = new ProductGrid(1000, new GridTemplate(100, "test", TemplateUsageType.getAllInstances()[0]), null, null);
		grid.setGenericEntityIDs(GenericEntityType.forName("channel"), new int[] { 3, 4, 5, 6, 7 });
		grid.setGenericCategoryIDs(GenericEntityType.forName("product"), new int[] { 11 });
		lockManager.lock(grid, user);
		try {
			lockManager.getExistingLock(grid, new User("user2", "user2", "pwd", false, 0, null));
			fail("Expected LockException not thrown");
		}
		catch (LockException e) {
		}
	}

	public void testGetExistingLockForGridWithDiffGridIDLockedReturnsNull() throws Exception {
		ProductGrid grid = new ProductGrid(1000, new GridTemplate(100, "test", TemplateUsageType.getAllInstances()[0]), null, null);
		grid.setGenericEntityIDs(GenericEntityType.forName("channel"), new int[] { 3, 4, 5, 6, 7 });
		lockManager.lock(grid, user);

		ProductGrid grid2 = new ProductGrid(1001, new GridTemplate(100, "test", TemplateUsageType.getAllInstances()[0]), null, null);
		grid2.setGenericEntityIDs(GenericEntityType.forName("channel"), new int[] { 3, 4, 5, 6, 7 });

		assertNull(lockManager.getExistingLock(grid2, user));
	}

	public void testGetExistingLockForGridWithDiffContextSameIDLockedReturnsNull() throws Exception {
		ProductGrid grid = new ProductGrid(1000, new GridTemplate(100, "test", TemplateUsageType.getAllInstances()[0]), null, null);
		grid.setGenericEntityIDs(GenericEntityType.forName("channel"), new int[] { 3, 4, 5, 6, 7 });
		lockManager.lock(grid, user);

		ProductGrid grid2 = new ProductGrid(1000, new GridTemplate(100, "test", TemplateUsageType.getAllInstances()[0]), null, null);

		LockInfo lockInfo = lockManager.getExistingLock(grid2, user);
		assertNotNull(lockInfo);
		assertEquals(user, lockInfo.getLockedBy());
	}

	public void testProductGridLockUnlockCheck() throws Exception {
		ProductGrid grid = new ProductGrid(1000, new GridTemplate(100, "test", TemplateUsageType.getAllInstances()[0]), null, null);
		lockManager.lock(grid, user);
		assertNotNull("Grid not locked", lockManager.getExistingLock(grid, user));

		lockManager.unlock(grid, user);
		assertNull("Grid not unlocked", lockManager.getExistingLock(grid, user));
	}

	public void testEntityIDLockUnlockCheck() throws Exception {
		lockManager.lock(EntityType.TEMPLATE, 1000, user);
		LockInfo lockInfo = lockManager.getExistingLock(EntityType.TEMPLATE, 1000, user);
		assertNotNull("Entity ID is not locked", lockInfo);

		lockManager.unlock(EntityType.TEMPLATE, 1000, user);

		lockInfo = lockManager.getExistingLock(EntityType.TEMPLATE, 1000, user);
		assertNull("Entity ID lock is not unlocked: " + lockInfo, lockInfo);
		
		// calling unlock again should return true
		assertTrue(lockManager.unlock(EntityType.TEMPLATE, 1000, user));
	}

	public void testEntityNameLockUnlockCheck() throws Exception {
		String nameToLock = "nameToLock";
		lockManager.lock(EntityType.USER_DATA, nameToLock, user);
		LockInfo lockInfo = lockManager.getExistingLock(EntityType.USER_DATA, nameToLock, user);
		assertNotNull("Entity name is not locked", lockInfo);

		lockManager.unlock(EntityType.USER_DATA, nameToLock, user);

		lockInfo = lockManager.getExistingLock(EntityType.USER_DATA, nameToLock, user);
		assertNull("Entity name lock is not unlocked: " + lockInfo, lockInfo);
	}

	public void testGenericEntityIDLockUnlockCheck() throws Exception {
		logBegin();

		GenericEntityType geType = GenericEntityType.forName("program");
		lockManager.lock(geType, 2300, user);
		LockInfo lockInfo = lockManager.getExistingLock(geType, 2300, user);
		assertNotNull("Generic Entity ID is not locked", lockInfo);

		lockManager.unlock(geType, 2300, user);

		lockInfo = lockManager.getExistingLock(geType, 2300, user);
		assertNull("Generic Entity ID lock is not unlocked: " + lockInfo, lockInfo);

		logEnd();
	}

	public void testParameterGridUnlockCheck() throws Exception {
		lockManager.lockParameterGrid(99999, user);
		LockInfo lockInfo = lockManager.getExistingParameterGridLock(99999, user);
		assertNotNull("Parameter grid is not locked", lockInfo);

		lockManager.unlockParameterGrid(99999, user);

		lockInfo = lockManager.getExistingParameterGridLock(99999, user);
		assertNull("Parameter grid lock is not unlocked: " + lockInfo, lockInfo);
	}
}
