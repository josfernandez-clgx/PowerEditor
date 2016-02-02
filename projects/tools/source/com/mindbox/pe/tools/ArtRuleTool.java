/*
 * Created on 2005. 6. 17.
 *
 */
package com.mindbox.pe.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author Geneho Kim
 * @since PowerEditor 
 */
public class ArtRuleTool {

	private static final String BEGIN_RULE_PREFIX = "(define-rule";
	private static final String RULE_NAME_REGEX = "^\\(define-rule ([a-zA-Z0-9_-]+)";
	private static final String RULE_END_REGEX = "^\\)$";

	public static void main(String[] args) throws Exception {
		if (args.length < 2) {
			System.out.println("Usage: java " + ArtRuleTool.class.getName() + " (count|rule-size) (<filename>|<dir-name>)");
			System.exit(1);
		}

		System.out.println("");
		boolean countRuleSize = args[0].equals("rule-size");

		File file = new File(args[1]);
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isFile()) {
					ArtRuleTool tool = new ArtRuleTool(files[i]);
					if (countRuleSize) {
						tool.countRuleSize();
					}
					else {
						tool.countRules();
					}
				}
			}
		}
		else {
			ArtRuleTool tool = new ArtRuleTool(args[1]);
			if (countRuleSize) {
				tool.countRuleSize();
			}
			else {
				tool.countRules();
			}
		}
		System.out.println("");
		System.exit(0);
	}

	private final File file;

	private ArtRuleTool(File file) {
		this.file = file;
	}

	private ArtRuleTool(String filename) {
		this.file = new File(filename);
	}

	private void countRules() throws Exception {
		System.out.print("Counting rules in " + file.getAbsolutePath());

		int lineCount = 0;
		int count = 0;
		BufferedReader in = new BufferedReader(new FileReader(file));
		System.out.print(".");
		for (String line = in.readLine(); line != null; line = in.readLine()) {
			if (line.indexOf(BEGIN_RULE_PREFIX) >= 0) {
				++count;
			}
			++lineCount;
		}
		System.out.print(".");
		in.close();
		System.out.print(".");
		System.out.println(": total rules = " + count + " (lines processed: " + lineCount + ')');
	}

	private void countRuleSize() throws Exception {
		System.out.println("Counting rule sizes in " + file.getAbsolutePath());

		Pattern pattern = Pattern.compile(RULE_NAME_REGEX);
		Matcher matcher = pattern.matcher("");

		int size = 0;
		int lineCount = 0;
		int count = 0;
		BufferedReader in = new BufferedReader(new FileReader(file));
		boolean inRule = false;
		for (String line = in.readLine(); line != null; line = in.readLine()) {
			++lineCount;
			if (inRule) {
				if (line.matches(RULE_END_REGEX)) {
					System.out.println("lines = " + size);
					inRule = false;
				}
				else if (line.trim().length() > 1) {
					++size;
				}
			}
			else {
				if (line.indexOf(BEGIN_RULE_PREFIX) >= 0) {
					// get rule name using regex
					matcher.reset(line.trim());
					if (matcher.find()) {
						String rulename = matcher.group(1);
						System.out.print("Rule: " + rulename);
					}
					else {
						System.out.print("Rule: " + line.trim());
					}
					System.out.print(": ");
					size = 0;
					inRule = true;
					++count;
				}
			}
		}
		in.close();
		System.out.println("(total rules = " + count + "; lines processed: " + lineCount + ')');
		System.out.println("");
	}
}