package com.mindbox.pe.server.repository;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 */
public interface RepositoryManager {

	/**
	 * Indicates a repository manager has not been initialized.
	 * @author Geneho Kim
	 * @author MindBox
	 * @since PowerEditor 2.3.0
	 */
	public static class NotInitializedException extends RuntimeException {
		private static final long serialVersionUID = 1L;
	}
	
	/**
	 * Call to initialize this repository manager.
	 * This must be called prior to invoking other methods in this class.
	 * @param initParam optional initialization parameter
	 */
	void initialize(Object initParam) throws RepositoryException;
	
	void deinitialize();
}

