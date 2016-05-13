package com.mindbox.pe.server.tag;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

public class WriteFileContentTag extends TagSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = -686557627308558748L;

	private String filename;

	public final String getFilename() {
		return filename;
	}

	public final void setFilename(String filename) {
		this.filename = filename;
	}

	@Override
	public int doStartTag() throws JspException {
		try {
			writeAll(new BufferedReader(new FileReader(String.class.cast(filename))));
		}
		catch (IOException e) {
			throw new JspException(e);
		}
		return SKIP_BODY;
	}

	private void writeAll(BufferedReader reader) throws IOException {
		JspWriter jspWriter = pageContext.getOut();
		for (String line = reader.readLine(); line != null; line = reader.readLine()) {
			jspWriter.println(line.replaceAll("<", "&lt;").replaceAll(">", "&gt;"));
		}
		jspWriter.flush();
	}
}
