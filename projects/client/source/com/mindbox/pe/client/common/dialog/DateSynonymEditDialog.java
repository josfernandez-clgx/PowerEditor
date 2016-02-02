/*
 * Created on 2004. 6. 29.
 */
package com.mindbox.pe.client.common.dialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.client.InputValidationException;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.common.PrivilegeConstants;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.ParameterGrid;
import com.mindbox.pe.model.ParameterTemplate;
import com.mindbox.pe.model.filter.AllNamedDateSynonymFilter;
import com.mindbox.pe.model.filter.GuidelineReportFilter;

/**
 * Date synonym edit dialog.
 *
 * @author kim
 * @since PowerEditor  4.2.0
 */
public class DateSynonymEditDialog extends JPanel {
    
    public static DateSynonym editDateSynonym(Frame owner, DateSynonym dateSynonym) {
        return editDateSynonym(owner, dateSynonym, false);
    }

    public static DateSynonym copyDateSynonym(Frame owner, DateSynonym dateSynonym) {
        return editDateSynonym(owner, dateSynonym, true);
    }

	private static DateSynonym editDateSynonym(Frame owner, DateSynonym dateSynonym, boolean forCopy) {
		JDialog dialog = new JDialog(owner, true);
		dialog.setTitle(forCopy ? ClientUtil.getInstance().getLabel("d.title.copy.dateSynonym") : ClientUtil.getInstance().getLabel("d.title.edit.dateSynonym"));
		DateSynonymEditDialog instance = new DateSynonymEditDialog(dialog, dateSynonym);
		UIFactory.addToDialog(dialog, instance);

		dialog.setVisible(true);

		return instance.dateSynonym;
	}
	
	public static DateSynonym mergeDateSynonyms(Frame owner, DateSynonym[] dateSynonyms) {
        if (hasProductionRestrictions(dateSynonyms)) {
            ClientUtil.getInstance().showWarning("msg.warning.merge.datesynonym.production", 
                    new Object[] { ClientUtil.getHighestStatusDisplayLabel()});
            return null;
        }
		JDialog dialog = new JDialog(owner, true);
		dialog.setTitle(ClientUtil.getInstance().getLabel("d.title.merge.dateSynonym"));
		String instructions= (ClientUtil.getInstance().getMessage("msg.inst.merge.datesynonyms")+System.getProperty("line.separator")+namesToCsv(dateSynonyms));

		DateSynonymEditDialog instance 
				= new DateSynonymEditDialog(
						dialog, 
						new DateSynonym(-1, dateSynonyms[0].getName(), dateSynonyms[0].getDescription(), dateSynonyms[0].getDate()), 
						instructions, 
						false);
		UIFactory.addToDialog(dialog, instance);

		dialog.setVisible(true);

		return instance.dateSynonym;
	}
	
	private static String namesToCsv(DateSynonym[] syns) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < syns.length; i++) {
			sb.append(syns[i].getName());
			if (i < syns.length-1) {
				sb.append(", ");
			}
		}
		return sb.toString();
	}

	public static DateSynonym newDateSynonym(Frame owner) {
		JDialog dialog = new JDialog(owner, true);
		dialog.setTitle(ClientUtil.getInstance().getLabel("d.title.new.dateSynonym"));
		
		DateSynonymEditDialog instance = new DateSynonymEditDialog(dialog, new DateSynonym(-1, "", "", createNewDate()));
		UIFactory.addToDialog(dialog, instance);

		dialog.setVisible(true);

		return instance.dateSynonym;
	}

	private static Date createNewDate() {
		Calendar newDate = Calendar.getInstance();

		String defaultHour = ClientUtil.getUserSession().getDefaultHour();
		String defaultMin = ClientUtil.getUserSession().getDefaultMinute();
		
		int hour = UtilBase.isEmpty(defaultHour) ? newDate.get(Calendar.HOUR_OF_DAY) : Integer.parseInt(defaultHour);
		int min = UtilBase.isEmpty(defaultMin) ? newDate.get(Calendar.MINUTE) : Integer.parseInt(defaultMin);
		
		newDate.set(Calendar.MILLISECOND, 0);
		newDate.set(Calendar.SECOND, 0);
		newDate.set(Calendar.MINUTE, min);
		newDate.set(Calendar.HOUR_OF_DAY, hour);

		return newDate.getTime();
	}

	public static DateSynonym copyDateSynonym(DateSynonym source) {
		DateSynonym dateSynonym = new DateSynonym(-1, source.getName(), source.getDescription(), source.getDate());
		return copyDateSynonym(JOptionPane.getFrameForComponent(ClientUtil.getApplet()), dateSynonym);
	}

	private class AcceptL implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (updateFromGUI()) {
				try {
					dateSynonym.copyFrom(detailPanel.getDateSynonym());
					dialog.dispose();
				}
				catch (InputValidationException ex) {
					ClientUtil.getInstance().showErrorMessage(ex.getMessage());
				}
			}
		}
	}

	private class CancelL implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			detailPanel.setDateSynonym(null);
			dateSynonym = null;
			dialog.dispose();
		}
	}

	private final JDialog dialog;
	private final DateSynonymDetailPanel detailPanel;
	private DateSynonym dateSynonym;
	private String instructions;
	private final boolean validateNoDuplicates;
    private final boolean hasProductionRestrictions;

	private DateSynonymEditDialog(JDialog dialog, DateSynonym dateSynonym) {
		this(dialog, dateSynonym, null, true);
	}
	
	private DateSynonymEditDialog(JDialog dialog, DateSynonym dateSynonym, String instructions, boolean validateNoDuplicates) {
		this.dialog = dialog;
		this.dateSynonym = dateSynonym;
		this.detailPanel = new DateSynonymDetailPanel();
		this.instructions = instructions;
		this.validateNoDuplicates = validateNoDuplicates;
        this.hasProductionRestrictions = hasProductionRestrictions(dateSynonym); 
		
		setSize(400,250);
        DateSynonym tempDS = new DateSynonym(dateSynonym.getId(),dateSynonym.getName(), 
                dateSynonym.getDescription(), dateSynonym.getDate());
        detailPanel.setDateSynonym(tempDS);
        
		if (hasProductionRestrictions) {
		    if (dateSynonym.getDate().before(new Date())) {
		        detailPanel.setDateFieldEnabled(false);
                this.instructions = ClientUtil.getInstance().getMessage(
                        "msg.warning.production.datesynonym.past", 
                        new Object[] { ClientUtil.getHighestStatusDisplayLabel() });
		    } else {
                this.instructions = ClientUtil.getInstance().getMessage(
                        "msg.warning.production.datesynonym.future", 
                        new Object[] { ClientUtil.getHighestStatusDisplayLabel() });
            }
		}
        initPanel();		
	}

	private void initPanel() {
		JButton createButton = new JButton("Accept");
		createButton.addActionListener(new AcceptL());
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new CancelL());
		
		JPanel buttonPanel = UIFactory.createJPanel(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(createButton);
		buttonPanel.add(cancelButton);
		
		setLayout(new BorderLayout(4,4));
		if (!UtilBase.isEmpty(instructions)) {
			JTextArea txtArea = new JTextArea(instructions,3,40);
			txtArea.setLineWrap(true);
			txtArea.setWrapStyleWord(true);
			txtArea.setEditable(false);
			JScrollPane scrollPane= new JScrollPane(txtArea,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			add(scrollPane, BorderLayout.NORTH);
		}
		add(detailPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
	}
	
	private boolean updateFromGUI() {
		DateSynonym dateSynonymFromPanel;
		try {
			dateSynonymFromPanel = detailPanel.getDateSynonym();
		}
		catch (InputValidationException ex) {
			ClientUtil.getInstance().showErrorMessage(ex.getMessage());
			return false;
		}

		if (dateSynonymFromPanel.getName() == null || dateSynonymFromPanel.getName().length() == 0) {
			ClientUtil.getInstance().showWarning("msg.warning.empty.field", new Object[] {
				ClientUtil.getInstance().getLabel("label.name") });
			return false;
		}
		if (dateSynonymFromPanel.getDate() == null) {
			ClientUtil.getInstance().showWarning("msg.warning.empty.field", new Object[] {
				ClientUtil.getInstance().getLabel("label.date") });
			return false;
		}

		if (validateNoDuplicates) {
			try {
	            List<DateSynonym> allDateSynonyms = ClientUtil.getCommunicator().search(new AllNamedDateSynonymFilter());            
	            for (Iterator<DateSynonym> i = allDateSynonyms.iterator(); i.hasNext();) {
	            	DateSynonym dateSynonym = i.next();
	            	if (!dateSynonym.equals(dateSynonymFromPanel)) {
	            		if (dateSynonym.getName().equals(dateSynonymFromPanel.getName())) {
	            			ClientUtil.getInstance().showWarning("msg.warning.duplicate.entity.name", 
	            					new Object[] {"Date Synonym", dateSynonymFromPanel.getName()});
	            			return false;
	            		} else if (dateSynonym.getDate().equals(dateSynonymFromPanel.getDate())) {
	            			ClientUtil.getInstance().showWarning("msg.warning.duplicate.entity.date", 
	            					new Object[] {"Date Synonym", dateSynonym.getName()});

	            			return false;
	            		}
	            	} 
				}
			} catch (Exception x) {
				ClientUtil.handleRuntimeException(x);
				return false;
			}
		}
        
        if (hasProductionRestrictions(dateSynonym) && 
                !dateSynonym.getDate().equals(dateSynonymFromPanel.getDate()) 
                && dateSynonymFromPanel.getDate().before(new Date())) {
            ClientUtil.getInstance().showWarning("msg.warning.production.datesynonym.future", 
                    new Object[] { ClientUtil.getHighestStatusDisplayLabel() });
            return false;
            
        }
		
		return true;

	}
    
    private static boolean hasProductionRestrictions(DateSynonym[] dateSynonyms) {
        if (ClientUtil.checkPermissionByPrivilegeName(PrivilegeConstants.PRIV_EDIT_PRODUCTION_DATA)) {
            return false;
        }
        for (int i = 0; i < dateSynonyms.length; i++) {
            if (hasProductionRestrictions(dateSynonyms[i])) {
                return true;
            }
        }
        
        return false;
    }
    private static boolean hasProductionRestrictions(DateSynonym dateSynonym) {
        try {
            if (dateSynonym.getId() == -1 ||
                    ClientUtil.checkPermissionByPrivilegeName(PrivilegeConstants.PRIV_EDIT_PRODUCTION_DATA)) {
                return false;
            }
            GuidelineReportFilter filter = new GuidelineReportFilter();
            filter.setIncludeEmptyContexts(false);
            filter.setChangesOnDate(dateSynonym.getDate());
            filter.addStatus(ClientUtil.getHighestStatus());

            if (ClientUtil.getCommunicator().search(filter).size() > 0) {
                return true;
            }
      
            // check parameter references
            List<ParameterTemplate> parameterTemplates = EntityModelCacheFactory.getInstance().getAllParameterTemplates();
            for (Iterator<ParameterTemplate> i = parameterTemplates.iterator(); i.hasNext();) {
                ParameterTemplate template = i.next();
                List<ParameterGrid> paramGridList = ClientUtil.getCommunicator().fetchParameters(template.getID());
                for (Iterator<ParameterGrid> it = paramGridList.iterator(); it.hasNext();) {
                    ParameterGrid grid = it.next();
                    if (ClientUtil.isHighestStatus(grid.getStatus()) &&
                            (UtilBase.isSame(grid.getEffectiveDate(), dateSynonym) || 
                                    UtilBase.isSame(grid.getExpirationDate(), dateSynonym))) { 
                        return true;
                    }
                }
            }
        } catch (ServerException e) {
            ClientUtil.handleRuntimeException(e);
        }
        return false;
    }
}
