/**
 * 
 */
package com.mindbox.pe.common;

import com.mindbox.pe.model.DomainClass;

public interface DomainClassProvider {

	DomainClass getDomainClass(String className);
}