package com.mindbox.pe.model;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.mindbox.pe.unittest.AbstractTestBase;

public class DeployTypeTest extends AbstractTestBase {

	@Test
	public void testNotValidValues() throws Exception {
		Set<DeployType> notValidValues = new HashSet<DeployType>(Arrays.asList(new DeployType[] { DeployType.CODE, DeployType.RELATIONSHIP }));
		Set<DeployType> intersection = new HashSet<DeployType>(Arrays.asList(DeployType.VALID_VALUES));
		intersection.retainAll(notValidValues);
		assertEquals("Invalid DeployTypes in VALID_VALUES: " + intersection, 0, intersection.size());
	}
}
