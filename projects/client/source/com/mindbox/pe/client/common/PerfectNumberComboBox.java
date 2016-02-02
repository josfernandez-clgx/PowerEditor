/*
 * Created on Oct 27, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.mindbox.pe.client.common;

import java.awt.Component;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.plaf.basic.BasicComboBoxEditor;

import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.CBRAttribute;
import com.mindbox.pe.model.Constants;

/**
 * @author deklerk
 * @since PowerEditor 4.1.0
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PerfectNumberComboBox extends JComboBox {
	
	static Integer blankChoice = new Integer(Constants.CBR_NULL_DATA_EQUIVALENT_VALUE);
	static Integer perfectChoice = new Integer(CBRAttribute.PERFECT_VALUE);
	static Integer[] choices = {blankChoice, perfectChoice};
	
	class PerfectNumberComboBoxEditor extends BasicComboBoxEditor {
		private Object oldValue;
		public void setItem(Object item) {
			if (item != null) {
				if (item instanceof Integer) {
					if (item.equals(blankChoice)) this.editor.setText("");
					else if (item.equals(perfectChoice)) this.editor.setText("Perfect");
					else this.editor.setText(item.toString());
				} 
				else this.editor.setText(item.toString());
			} 
			else this.editor.setText("");
			oldValue = item;
		}
		public Object getItem(){
			String input = editor.getText();
			if (input.toLowerCase().indexOf("p") != -1) return perfectChoice;
			else if (UtilBase.trim(input).length() == 0) return blankChoice;
			else {
				try {
					int intVal = Integer.parseInt(input);
					if (intVal > Constants.CBR_NULL_DATA_EQUIVALENT_VALUE) return new Integer(intVal);
					else return oldValue;
				} catch (Exception x) {
					return oldValue;
				}
			}
		}
	}

	class ComboBoxRenderer extends JLabel
    implements ListCellRenderer {
		public ComboBoxRenderer() {
			setOpaque(true);
			//setHorizontalAlignment(CENTER);
			setVerticalAlignment(CENTER);
		}

		/*
		 * This method finds the image and text corresponding
		 * to the selected value and returns the label, set up
		 * to display the text and image.
		 */
		public Component getListCellRendererComponent(
				JList list,
				Object value,
				int index,
				boolean isSelected,
				boolean cellHasFocus) {
			
			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			if (index > -1) {
				Integer choice = choices[index];
				if (choice != null)
					setText(choice.intValue() == CBRAttribute.PERFECT_VALUE ? "Perfect" : "<blank>");
			}
			return this;
		}
	}

	/**
	 * 
	 */
	public PerfectNumberComboBox() {
		super(choices);
		this.setEditable(true);
		this.setRenderer(new ComboBoxRenderer());
		this.setEditor(new PerfectNumberComboBoxEditor());
		UIFactory.setLookAndFeel(this);
		// this is a hack to put the correct border on this widget.  There's probably a better way.
		this.setBorder((new JTextField()).getBorder());	
	}

}
