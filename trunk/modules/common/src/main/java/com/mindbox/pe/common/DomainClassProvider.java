/**
 * 
 */
package com.mindbox.pe.common;

import com.mindbox.pe.model.domain.DomainClass;

public interface DomainClassProvider {

	DomainClass getDomainClass(String className);
}