package com.mindbox.pe.client.common.rowheader;

import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class JScrollPaneAdjuster implements PropertyChangeListener, Serializable {
	private static class Adjuster implements ChangeListener, Runnable {

		public void setHeader(JViewport jviewport) {
			if (header != null) header.removeChangeListener(this);
			header = jviewport;
			if (header != null) header.addChangeListener(this);
		}

		public void stateChanged(ChangeEvent changeevent) {
			if (viewport == null || header == null) return;
			if (type == 1) {
				if (viewport.getViewPosition().x != header.getViewPosition().x) SwingUtilities.invokeLater(this);
			}
			else if (viewport.getViewPosition().y != header.getViewPosition().y) SwingUtilities.invokeLater(this);
		}

		public void setViewport(JViewport jviewport) {
			viewport = jviewport;
		}

		public void dispose() {
			if (header != null) header.removeChangeListener(this);
			viewport = header = null;
		}

		public void run() {
			if (viewport == null || header == null) return;
			Point point = viewport.getViewPosition();
			Point point1 = header.getViewPosition();
			if (type == 1) {
				if (point.x != point1.x) viewport.setViewPosition(new Point(point1.x, point.y));
			}
			else if (point.y != point1.y) viewport.setViewPosition(new Point(point.x, point1.y));
		}

		private JViewport viewport;
		private JViewport header;
		private int type;

		public Adjuster(JViewport jviewport, JViewport jviewport1, int i) {
			viewport = jviewport;
			header = jviewport1;
			type = i;
			if (jviewport1 != null) jviewport1.addChangeListener(this);
		}
	}


	public JScrollPaneAdjuster(JScrollPane jscrollpane) {
		pane = jscrollpane;
		x = new Adjuster(jscrollpane.getViewport(), jscrollpane.getColumnHeader(), 1);
		y = new Adjuster(jscrollpane.getViewport(), jscrollpane.getRowHeader(), 2);
		jscrollpane.addPropertyChangeListener(this);
	}

	private void readObject(ObjectInputStream objectinputstream) throws IOException, ClassNotFoundException {
		objectinputstream.defaultReadObject();
		x = new Adjuster(pane.getViewport(), pane.getColumnHeader(), 1);
		y = new Adjuster(pane.getViewport(), pane.getRowHeader(), 2);
	}

	public void propertyChange(PropertyChangeEvent propertychangeevent) {
		String s = propertychangeevent.getPropertyName();
		if (s.equals("viewport")) {
			x.setViewport((JViewport) propertychangeevent.getNewValue());
			y.setViewport((JViewport) propertychangeevent.getNewValue());
		}
		else if (s.equals("rowHeader"))
			y.setHeader((JViewport) propertychangeevent.getNewValue());
		else if (s.equals("columnHeader")) x.setHeader((JViewport) propertychangeevent.getNewValue());
	}

	public void dispose() {
		x.dispose();
		y.dispose();
		pane.removePropertyChangeListener(this);
		pane = null;
	}

	private JScrollPane pane;
	private transient Adjuster x;
	private transient Adjuster y;
}
