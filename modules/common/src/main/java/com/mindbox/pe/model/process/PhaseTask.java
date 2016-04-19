/*
 * Created on 2004. 6. 24.
 *
 */
package com.mindbox.pe.model.process;

import java.io.Serializable;


/**
 * Marks a class that can be used as a phase task.
 * @author kim
 * @since PowerEditor 3.3.0
 */
public interface PhaseTask extends Serializable {
	String getName();
	String getStorageName();
}
