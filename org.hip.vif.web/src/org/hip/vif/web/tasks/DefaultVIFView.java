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

package org.hip.vif.web.tasks;

import org.hip.vif.core.exc.NoTaskFoundException;
import org.hip.vif.core.interfaces.IMessages;
import org.hip.vif.web.Activator;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;

/**
 * Default implementation of a view component.
 * 
 * @author Luthiger
 * Created: 19.05.2011
 */
@SuppressWarnings("serial")
public class DefaultVIFView extends CustomComponent {

	/**
	 * Creates view component displaying an exceptions message.
	 * 
	 * @param inExc Exception
	 */
	public DefaultVIFView(Exception inExc) {
		IMessages lMessages = Activator.getMessages();
		String lMessage = inExc.getMessage() == null ? inExc.toString() : inExc.getMessage();
		if (inExc instanceof NoTaskFoundException) {
			lMessage = lMessages.getMessage("errmsg.error.contactAdmin"); //$NON-NLS-1$
		}
		init(String.format("<span style=\"color:red;\"><strong>%s:</strong> %s</span>",  //$NON-NLS-1$
				lMessages.getMessage("label.error"), lMessage)); //$NON-NLS-1$
	}
	
	/**
	 * Creates view component displaying a simple message.
	 * 
	 * @param inMessage String
	 */
	public DefaultVIFView(String inMessage) {
		init(String.format("<span>%s</span>", inMessage)); //$NON-NLS-1$
	}
	
	private void init(String inMessage) {
		setSizeFull();		
		Label lLabel = new Label(inMessage, Label.CONTENT_XHTML);
		setCompositionRoot(lLabel);
	}
	
}