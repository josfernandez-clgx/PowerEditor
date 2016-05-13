/*
 * Created on Jan 26, 2006
 *
 */
package com.mindbox.pe.model.report;


/**
 * Custom report spec.
 * @author Geneho Kim
 * @since PowerEditor 4.4.0
 */
public class CustomReportSpec extends AbstractReportSpec {
	
	private static final long serialVersionUID = 20060126800001L;
	
	public CustomReportSpec(String filename) {
		super();
		super.setLocalFilename(filename);
	}
	
	/**
	 * Equivalent to <code>getLocalFilename()</code>.
	 * @return report file name
	 */
	public String getReportFilename() {
		return super.getLocalFilename();
	}
}
