/*
	This package is part of the application VIF.
	Copyright (C) 2011, Benno Luthiger

	This program is free software; you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation; either version 2 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program; if not, write to the Free Software
	Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package org.hip.vif.web.components;

import java.io.IOException;

import org.hip.vif.core.interfaces.IMessages;
import org.hip.vif.core.util.EmbeddedDBHelper;
import org.hip.vif.web.Activator;
import org.hip.vif.web.tasks.DBAccessWorkflowItems.ShowConfigPopup;
import org.hip.vif.web.util.ConfigurationItem;
import org.hip.vif.web.util.DBDriverSelect;
import org.hip.vif.web.util.GenericSelect.IProcessor;
import org.hip.vif.web.util.VIFViewHelper;

import com.vaadin.data.Property;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Select;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * Child window displaying the field for that the SU can provide the information
 * for initial DB access configuration.
 * 
 * @author Luthiger
 * Created: 08.02.2012
 */
@SuppressWarnings("serial")
public class DBConfigurationPopup extends AbstractConfigurationPopup {
	private static final int WIDTH = 300;
	
	private int counter = 0;	

	/**
	 * Constructor.
	 * 
	 * @param inMain {@link Window}
	 * @param inShowConfigPopup {@link ShowConfigPopup}
	 * @throws IOException
	 */
	public DBConfigurationPopup(Window inMain, ShowConfigPopup inShowConfigPopup) throws IOException {		
		Window lPopup = createPopup(Activator.getMessages().getMessage("config.label.db.title")); //$NON-NLS-1$
		lPopup.setWidth(700, UNITS_PIXELS);
		lPopup.setHeight(300, UNITS_PIXELS);
		
		VerticalLayout lLayout = createLayout((VerticalLayout) lPopup.getContent());
		lLayout.addComponent(getDBAccessConfig(ConfigurationItem.createConfiguration(), inShowConfigPopup));
		lLayout.addComponent(createCloseButton());
		inMain.addWindow(lPopup);
	}
	
	private VerticalLayout getDBAccessConfig(final ConfigurationItem inConfiguration, final ShowConfigPopup inController) {
		final IMessages lMessages = Activator.getMessages();
		
		final VerticalLayout outLayout = new VerticalLayout();
		final LabelValueTable lTable = new LabelValueTable();
		final Form lForm = new Form();
		lForm.setValidationVisible(true);

		counter = 0;
		
		final TextField lServer = createInput(ConfigurationItem.PropertyDef.DB_SERVER, inConfiguration);
		final String lServerField = lMessages.getMessage("config.label.db.server"); //$NON-NLS-1$
		final TextField lSchema = createInput(ConfigurationItem.PropertyDef.DB_SCHEMA, inConfiguration);
		final String lSchemaField = lMessages.getMessage("config.label.db.schema"); //$NON-NLS-1$
		final TextField lUser = createInput(ConfigurationItem.PropertyDef.DB_USER, inConfiguration);
		final String lUserField = lMessages.getMessage("config.label.db.user"); //$NON-NLS-1$
		final TextField lPassword = createInput(ConfigurationItem.PropertyDef.DB_PASSWD, inConfiguration);
		final String lPasswordField = lMessages.getMessage("config.label.db.password"); //$NON-NLS-1$
		
		IProcessor lProcessor = new IProcessor() {			
			@Override
			public void process(String inItemID) {
				boolean lEnabled = !EmbeddedDBHelper.checkEmbedded(inItemID);
				decorateField(lServer, lEnabled, lMessages, lServerField);
				decorateField(lSchema, lEnabled, lMessages, lSchemaField);
				decorateField(lUser, lEnabled, lMessages, lUserField);
				decorateField(lPassword, lEnabled, lMessages, lPasswordField);
			}
		};
		final Property lDriver = inConfiguration.getItemProperty(ConfigurationItem.PropertyDef.DB_DRIVER.getPID());
		final Select lSelect = DBDriverSelect.getDBDriverSelection(lDriver, WIDTH, false, lProcessor);
		lSelect.focus();
		lTable.addRow(lMessages.getMessage("config.label.db.driver"), createInput(lSelect, "config.desc.db.driver", lMessages, lForm)); //$NON-NLS-1$ //$NON-NLS-2$
		boolean lEnabled = !EmbeddedDBHelper.checkEmbedded(lDriver.getValue().toString());		
		lTable.addRow(lServerField, addToForm(decorateField(lServer, lEnabled, lMessages, lSchemaField), lForm));
		lTable.addRow(lSchemaField, addToForm(decorateField(lSchema, lEnabled, lMessages, lServerField), lForm)); 
		lTable.addRow(lUserField, addToForm(decorateField(lUser, lEnabled, lMessages, lUserField), lForm));
		lTable.addRow(lPasswordField, addToForm(decorateField(lPassword, lEnabled, lMessages, lPasswordField), lForm));
		lTable.addEmtpyRow();
		
		lForm.getLayout().addComponent(lTable);
		outLayout.addComponent(lForm);
		
		Button lSave = new Button(lMessages.getMessage("config.button.save")); //$NON-NLS-1$
		lSave.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent inEvent) {
				try {
					lForm.commit();
					inController.save(inConfiguration);						
				}
				catch (InvalidValueException exc) {
					//intentionally left empty
				}
			}
		});
		lTable.addRow(lSave);
		
		outLayout.addComponent(lTable);
		return outLayout;
	}

	private HorizontalLayout createInput(AbstractField inInput, String inMsgKey, IMessages inMessages, Form inForm) {
		HorizontalLayout out = new HorizontalLayout();
		out.setSizeFull();
		inForm.addField(String.valueOf(++counter), inInput);
		out.addComponent(inInput);
		
		Label lDescription = new Label(String.format(VIFViewHelper.TMPL_TITLE, "vif-contribution-date", inMessages.getMessage(inMsgKey)), Label.CONTENT_XHTML); //$NON-NLS-1$
		out.addComponent(lDescription);
		out.setExpandRatio(lDescription, 1);
		return out;
	}
	private TextField createInput(ConfigurationItem.PropertyDef inPropertyDef, ConfigurationItem inConfiguration) {
		TextField out = new TextField(inConfiguration.getItemProperty(inPropertyDef.getPID()));
		out.setWidth(WIDTH, UNITS_PIXELS);
		out.setStyleName("vif-input-config"); //$NON-NLS-1$
		out.setImmediate(true);
		return out;
	}
	private AbstractField addToForm(AbstractField inInput, Form inForm) {
		inForm.addField(String.valueOf(++counter), inInput);
		return inInput;
	}
	private AbstractField decorateField(AbstractField inInput, boolean inEnable, IMessages inMessages, String inFieldName) {
		inInput.setRequired(inEnable);
		inInput.setRequiredError(inMessages.getFormattedMessage("errmsg.error.not.empty", inFieldName)); //$NON-NLS-1$
		inInput.setEnabled(inEnable);
		return inInput;
	}

}
