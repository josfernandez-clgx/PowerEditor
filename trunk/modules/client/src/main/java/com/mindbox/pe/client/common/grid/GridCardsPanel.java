package com.mindbox.pe.client.common.grid;

import java.awt.CardLayout;

import com.mindbox.pe.client.common.PanelBase;
import com.mindbox.pe.model.template.GridTemplate;

public class GridCardsPanel extends PanelBase {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	public GridCardsPanel(GridTablePanel gridtablepanel) {
		mGridTablePanel = null;
		mSelectedCard = null;
		mGridTablePanel = gridtablepanel;
		setLayout(new CardLayout());
		addGridTablePanel();
		displayGridTablePanel();
	}

	public IGridDataCard getSelectedCard() {
		return mSelectedCard;
	}

	public void setTemplate(GridTemplate gridtemplate) {
		displayGridTablePanel();
		getSelectedCard().setTemplate(gridtemplate);
	}

	protected void addGridTablePanel() {
		add(mGridTablePanel, TABLE_DISPLAY);
	}

	private void displayGridTablePanel() {
		CardLayout cardlayout = (CardLayout) getLayout();
		cardlayout.show(this, TABLE_DISPLAY);
		mSelectedCard = mGridTablePanel;
	}

	protected GridTablePanel mGridTablePanel;
	private IGridDataCard mSelectedCard;
	private static String TABLE_DISPLAY = "TableDisplay";
}
