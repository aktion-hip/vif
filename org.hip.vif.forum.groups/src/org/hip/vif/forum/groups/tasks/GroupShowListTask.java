/*
 This package is part of the application VIF.
 Copyright (C) 2008, Benno Luthiger

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.hip.vif.forum.groups.tasks;

import java.sql.SQLException;

import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.KeyObject.BinaryBooleanOperator;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.SetOperatorHome;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.bom.impl.SetOperatorHomeImpl;
import org.hip.kernel.code.CodeList;
import org.hip.kernel.code.CodeListHome;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.annotations.Partlet;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.GroupHome;
import org.hip.vif.core.bom.ParticipantHome;
import org.hip.vif.core.bom.VIFGroupWorkflow;
import org.hip.vif.core.bom.impl.NestedGroupHome;
import org.hip.vif.core.bom.impl.NestedGroupHome2;
import org.hip.vif.core.code.GroupState;
import org.hip.vif.forum.groups.data.GroupContainer;
import org.hip.vif.forum.groups.data.GroupWrapper;
import org.hip.vif.forum.groups.ui.GroupListView;
import org.hip.vif.web.tasks.AbstractVIFTask;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Component;

/**
 * Default task after login:
 * shows the list of available discussion groups.
 * 
 * @author: Benno Luthiger
 */
@SuppressWarnings("serial")
@Partlet
public class GroupShowListTask extends AbstractVIFTask implements ValueChangeListener {
	private static final String SORT_ORDER = GroupHome.KEY_NAME + ", " + NestedGroupHome.KEY_GROUP_ID; //$NON-NLS-1$
	
	/**
	 * @see org.hip.vif.servlets.AbstractVIFTask#needsPermission()
	 */
	protected String needsPermission() {
		return ""; //$NON-NLS-1$
	}

	/**
	 * Collect the information to display the list of discussion groups
	 */
	protected Component runChecked() throws VException {		
		try {
			emptyContextMenu();
			
			KeyObject lKeyVisible = createKey(VIFGroupWorkflow.VISIBLE_STATES);
			KeyObject lKey1 = new KeyObjectImpl();
			lKey1.setValue(GroupHome.KEY_PRIVATE, GroupHome.IS_PUBLIC);
			lKey1.setValue(lKeyVisible, BinaryBooleanOperator.AND);
			KeyObject lKey2 = new KeyObjectImpl();
			lKey2.setValue(GroupHome.KEY_PRIVATE, GroupHome.IS_PRIVATE);
			lKey2.setValue(ParticipantHome.KEY_MEMBER_ID, getActor().getActorID(), "=", BinaryBooleanOperator.AND); //$NON-NLS-1$
			lKey2.setValue(lKeyVisible, BinaryBooleanOperator.AND);
			
			QueryResult lResult = null;
			if (getActor().isGuest()) {
				lKey1.setValue(GroupHome.KEY_GUEST_DEPTH, new Integer(0), ">"); //$NON-NLS-1$
				NestedGroupHome lGroupHome = getNestedGroupHome();
				lResult = lGroupHome.select(lKey1, createOrder(SORT_ORDER, false));
			}
			else {
				NestedGroupHome lGroupHome = getNestedGroupHome();
				NestedGroupHome2 lGroupHome2 = getNestedGroupHome2();
				SetOperatorHome lUnionHome = new SetOperatorHomeImpl(SetOperatorHome.UNION);
				lUnionHome.addSet(lGroupHome, lKey1);
				lUnionHome.addSet(lGroupHome2, lKey2);
				lResult = lUnionHome.select(createOrder(SORT_ORDER, false));
			}

			CodeList lCodeList = CodeListHome.instance().getCodeList(GroupState.class, getAppLocale().getLanguage());
			return new GroupListView(GroupContainer.createData(lResult, lCodeList), this);
		}
		catch (SQLException exc) {
			throw createContactAdminException(exc);
		}
	}

	private NestedGroupHome2 getNestedGroupHome2() {
		return BOMHelper.getNestedGroupHome2();
	}

	private NestedGroupHome getNestedGroupHome() {
		return BOMHelper.getNestedGroupHome();
	}

	/**
	 * Method of the <code>ValueChangeListener</code> interface.
	 * 
	 * @param inEvent {@link ValueChangeEvent}
	 */
	public void valueChange(ValueChangeEvent inEvent) {
		Object lGroup = inEvent.getProperty().getValue();
		if (lGroup instanceof GroupWrapper) {
			setGroupID(((GroupWrapper)lGroup).getGroupID());
			sendEvent(GroupShowTask.class);
		}
	}

}
