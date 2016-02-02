package com.mindbox.pe.client.applet.template.guideline;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.UIFactory;

public class TemplateValidationErrorReportDialog extends JPanel {
	private static final String SAVE_REPORT_ACTION = "SAVE_REPORT";
	private static final String VIEW_REPORT_ACTION = "VIEW_REPORT";
	
	private String errorReport;
	private int gridCount;
	private JFileChooser fileChooser;
	private JButton saveReportButton, viewReportButton;
	private boolean reportSaved;

    private TemplateValidationErrorReportDialog(String errorReport, int gridCount) {
    	this.errorReport = errorReport;
    	this.gridCount = gridCount;
    	this.reportSaved = false;
    	
    	initComponents();
    	layoutComponents();
    }

    public static boolean showErrorReport(String errorReport, int gridCount) {
        TemplateValidationErrorReportDialog dialog = new TemplateValidationErrorReportDialog(errorReport, gridCount);
        dialog.saveReportButton.requestFocus();

        int userChoice = JOptionPane.OK_OPTION;
        
        while (userChoice == JOptionPane.OK_OPTION && !dialog.reportSaved) {
        	userChoice = JOptionPane.showOptionDialog(ClientUtil.getApplet(),
                dialog,
                ClientUtil.getInstance().getLabel("d.title.save.templatewithgriderrors"),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, new String[]{"Continue", "Cancel"},
        		"Cancel");
        }
        	
        return userChoice == JOptionPane.OK_OPTION;
    }

	private void initComponents() {
    	saveReportButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.save.report"), 
    			null, new SaveReportListener(), "button.save.report");
        saveReportButton.setActionCommand(SAVE_REPORT_ACTION);

        viewReportButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.view.report"), 
    			null, new ViewReportListener(), "button.view.report");
        viewReportButton.setActionCommand(VIEW_REPORT_ACTION);
        
        fileChooser = new JFileChooser();
		fileChooser.addChoosableFileFilter(new FileFilter() {
			public boolean accept(File f) {
				return f.isDirectory() || f.getName().toUpperCase().endsWith("HTML");
			}
			public String getDescription() {
				return "html";
			}
		});
	}

    private void layoutComponents() {
        setLayout(new GridLayout(2, 1));
        
        add(new JLabel("<html><body>Saving this template will result in " + gridCount + " guideline" + (gridCount > 1 ? "s " : " ") + "with validation errors."
        		+ "<p>A detailed error report has been generated which you <em>must</em> save before continuing with your template save request."));
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveReportButton);
        viewReportButton.setEnabled(false);
        buttonPanel.add(viewReportButton);
        add(buttonPanel);
    }
    
    private class SaveReportListener implements ActionListener {
    	public void actionPerformed(ActionEvent event) {
    		if (SAVE_REPORT_ACTION.equals(event.getActionCommand())) {
    			saveReport();
			}
    	}
    }

	private void saveReport() {
		int returnVal = fileChooser.showDialog(this, ClientUtil.getInstance().getLabel("button.save"));
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	    	File f = fileChooser.getSelectedFile();
    		try {
    			
		    	if (!f.exists()) {
		    		String fName = f.getCanonicalPath();
		    		if (!fName.toUpperCase().endsWith("HTML")) {
		    			fName = fName + ".html";
		    			fileChooser.setSelectedFile(new File(fName));
		    			f = fileChooser.getSelectedFile();
		    		}
		    		f.createNewFile();
		    	}
				PrintWriter writer = new PrintWriter(new FileOutputStream(f));
				writer.print(errorReport);
				writer.flush();
				writer.close();
				
				String styleSheet = "pe_report_style.css";
				String targetFile = f.getParent() + File.separatorChar + styleSheet;
				ClientUtil.getInstance().downloadResourceFileFromServer(styleSheet, targetFile);
				
				ClientUtil.getInstance().showInformation("msg.info.success.file.save");
				
				reportSaved = true;
				viewReportButton.setEnabled(true);
				
			} catch (Exception e) {
				ClientUtil.handleRuntimeException(e);
			}
	    }
	}
    
    private class ViewReportListener implements ActionListener {
    	public void actionPerformed(ActionEvent event) {
    		if (VIEW_REPORT_ACTION.equals(event.getActionCommand())) {
    			viewReport();
			}
    	}
    }

	private void viewReport() {
		try {
			ClientUtil.executeAsScript(fileChooser.getSelectedFile().toURI().toURL().toString());
		} catch (Exception e) {
			ClientUtil.handleRuntimeException(e);
			viewReportButton.setEnabled(false);
		}
	}
}
