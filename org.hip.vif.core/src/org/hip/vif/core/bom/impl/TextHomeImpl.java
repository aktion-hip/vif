/*
	This package is part of the persistency layer of the application VIF.
	Copyright (C) 2010, Benno Luthiger

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
package org.hip.vif.core.bom.impl;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.hip.kernel.bom.ColumnModifierFactory;
import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.KeyObject.BinaryBooleanOperator;
import org.hip.kernel.bom.OrderObject;
import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.impl.DomainObjectHomeImpl;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.bom.impl.OrderObjectImpl;
import org.hip.kernel.exc.VException;
import org.hip.vif.core.bom.BOMHelper;
import org.hip.vif.core.bom.Text;
import org.hip.vif.core.bom.Text.ITextValues;
import org.hip.vif.core.bom.TextHome;

/**
 * This domain object home implements the <code>TextHome</code> interface.
 *
 * @author Luthiger
 * Created: 14.06.2010
 */
@SuppressWarnings("serial")
public class TextHomeImpl extends DomainObjectHomeImpl implements TextHome {

	private final static String OBJECT_CLASS_NAME = "org.hip.vif.core.bom.impl.TextImpl";
	private final static String XML_OBJECT_DEF = 
		"<?xml version='1.0' encoding='ISO-8859-1'?>	" +
		"<objectDef objectName='TextImpl' parent='org.hip.kernel.bom.DomainObject' version='1.0'>	" +
		"	<keyDefs>	" +
		"		<keyDef>	" +
		"			<keyItemDef seq='0' keyPropertyName='" + KEY_ID + "'/>	" +
		"			<keyItemDef seq='1' keyPropertyName='" + KEY_VERSION + "'/>	" +
		"		</keyDef>	" +
		"	</keyDefs>	" +
		"	<propertyDefs>	" +
		"		<propertyDef propertyName='" + KEY_ID + "' valueType='Long' propertyType='simple'>	" +
		"			<mappingDef tableName='tblTextVersion' columnName='TextID'/>	" +
		"		</propertyDef>	" +
		"		<propertyDef propertyName='" + KEY_VERSION + "' valueType='Long' propertyType='simple'>	" +
		"			<mappingDef tableName='tblTextVersion' columnName='nVersion'/>	" +
		"		</propertyDef>	" +
		"		<propertyDef propertyName='" + KEY_TITLE + "' valueType='String' propertyType='simple'>	" +
		"			<mappingDef tableName='tblTextVersion' columnName='sTitle'/>	" +
		"		</propertyDef>	" +
		"		<propertyDef propertyName='" + KEY_AUTHOR + "' valueType='String' propertyType='simple'>	" +
		"			<mappingDef tableName='tblTextVersion' columnName='sAuthor'/>	" +
		"		</propertyDef>	" +
		"		<propertyDef propertyName='" + KEY_COAUTHORS + "' valueType='String' propertyType='simple'>	" +
		"			<mappingDef tableName='tblTextVersion' columnName='sCoAuthors'/>	" +
		"		</propertyDef>	" +
		"		<propertyDef propertyName='" + KEY_SUBTITLE + "' valueType='String' propertyType='simple'>	" +
		"			<mappingDef tableName='tblTextVersion' columnName='sSubtitle'/>	" +
		"		</propertyDef>	" +
		"		<propertyDef propertyName='" + KEY_YEAR + "' valueType='String' propertyType='simple'>	" +
		"			<mappingDef tableName='tblTextVersion' columnName='sYear'/>	" +
		"		</propertyDef>	" +
		"		<propertyDef propertyName='" + KEY_PUBLICATION + "' valueType='String' propertyType='simple'>	" +
		"			<mappingDef tableName='tblTextVersion' columnName='sPublication'/>	" +
		"		</propertyDef>	" +
		"		<propertyDef propertyName='" + KEY_PAGES + "' valueType='String' propertyType='simple'>	" +
		"			<mappingDef tableName='tblTextVersion' columnName='sPages'/>	" +
		"		</propertyDef>	" +
		"		<propertyDef propertyName='" + KEY_VOLUME + "' valueType='String' propertyType='simple'>	" +
		"			<mappingDef tableName='tblTextVersion' columnName='nVolume'/>	" +
		"		</propertyDef>	" +
		"		<propertyDef propertyName='" + KEY_NUMBER + "' valueType='String' propertyType='simple'>	" +
		"			<mappingDef tableName='tblTextVersion' columnName='nNumber'/>	" +
		"		</propertyDef>	" +
		"		<propertyDef propertyName='" + KEY_PUBLISHER + "' valueType='String' propertyType='simple'>	" +
		"			<mappingDef tableName='tblTextVersion' columnName='sPublisher'/>	" +
		"		</propertyDef>	" +
		"		<propertyDef propertyName='" + KEY_PLACE + "' valueType='String' propertyType='simple'>	" +
		"			<mappingDef tableName='tblTextVersion' columnName='sPlace'/>	" +
		"		</propertyDef>	" +
		"		<propertyDef propertyName='" + KEY_REMARK + "' valueType='String' propertyType='simple'>	" +
		"			<mappingDef tableName='tblTextVersion' columnName='sRemark'/>	" +
		"		</propertyDef>	" +
		"		<propertyDef propertyName='" + KEY_REFERENCE + "' valueType='String' propertyType='simple'>	" +
		"			<mappingDef tableName='tblTextVersion' columnName='sReference'/>	" +
		"		</propertyDef>	" +
		"		<propertyDef propertyName='" + KEY_TYPE + "' valueType='Long' propertyType='simple'>	" +
		"			<mappingDef tableName='tblTextVersion' columnName='nType'/>	" +
		"		</propertyDef>	" +
		"		<propertyDef propertyName='" + KEY_STATE + "' valueType='Long' propertyType='simple'>	" +
		"			<mappingDef tableName='tblTextVersion' columnName='nState'/>	" +
		"		</propertyDef>	" +
		"		<propertyDef propertyName='" + KEY_FROM + "' valueType='Timestamp' propertyType='simple'>	" +
		"			<mappingDef tableName='tblTextVersion' columnName='dtFrom'/>	" +
		"		</propertyDef>	" +
		"		<propertyDef propertyName='" + KEY_TO + "' valueType='Timestamp' propertyType='simple'>	" +
		"			<mappingDef tableName='tblTextVersion' columnName='dtTo'/>	" +
		"		</propertyDef>	" +
		"	</propertyDefs>	" +
		"</objectDef>";

	/**
	 * Returns the name of the objects which this home can create.
	 *
	 * @return java.lang.String
	 */
	public String getObjectClassName() {
		return OBJECT_CLASS_NAME;
	}

	/**
	 * Returns the object definition string of the class managed by this home.
	 *
	 * @return java.lang.String
	 */
	protected String getObjectDefString() {
		return XML_OBJECT_DEF;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.hip.vif.bom.TextHome#getTextPublished(java.lang.String)
	 */
	public Text getTextPublished(Long inTextID) throws VException, SQLException {
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(TextHome.KEY_ID, inTextID);
		lKey.setValue(BOMHelper.getKeyPublished(TextHome.KEY_STATE), BinaryBooleanOperator.AND);
		return (Text) findByKey(lKey);
	}
	
	public boolean hasPublishedVersion(Long inTextID) throws VException, SQLException {
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(TextHome.KEY_ID, inTextID);
		lKey.setValue(TextHome.KEY_STATE, WorkflowAwareContribution.S_OPEN, "=", BinaryBooleanOperator.AND);
		QueryResult lResult = select(lKey);
		return lResult.hasMoreElements();
	}

	/*
	 * (non-Javadoc)
	 * @see org.hip.vif.bom.TextHome#getText(Long, int)
	 */
	public Text getText(Long inTextID, int inVersion) throws VException, SQLException {
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(TextHome.KEY_ID, inTextID);
		lKey.setValue(TextHome.KEY_VERSION, new Integer(inVersion));
		return (Text) findByKey(lKey);
	}

	/*
	 * (non-Javadoc)
	 * @see org.hip.vif.bom.TextHome#getText(java.lang.String)
	 */
	public Text getText(String inIDVersion) throws VException, SQLException {
		String[] lTextID = inIDVersion.split("-");
		return getText(new Long(lTextID[0]), Integer.parseInt(lTextID[1]));
	}

	/*
	 * (non-Javadoc)
	 * @see org.hip.vif.bom.TextHome#getAutoCompleteSelection(java.lang.String, java.lang.String)
	 */
	public List<String> getAutoCompleteSelection(String inField, String inLookup) throws VException, SQLException {
		KeyObject lKey = new KeyObjectImpl();
		lKey.setDistinct(true);
		lKey.setValue(inField, inLookup.toUpperCase() + "%", "LIKE", null, ColumnModifierFactory.getMySQLColumnModifierUCase());
		lKey.setValue(BOMHelper.getKeyPublished(TextHome.KEY_STATE), BinaryBooleanOperator.AND);
		OrderObject lOrder = new OrderObjectImpl();
		lOrder.setValue(inField, 0);
		//no grouping allowed in proper SQL (e.g. Derby)
//		GroupByObject lGroup = new GroupByObjectImpl();
//		lGroup.setValue(TextHome.KEY_ID, 0);
		
		Set<String> lSelected = new HashSet<String>();
//		QueryResult lResult = select(lKey, lOrder, null, lGroup);
		QueryResult lResult = select(lKey, lOrder);
		while (lResult.hasMoreElements()) {
			GeneralDomainObject lText = lResult.nextAsDomainObject();
			lSelected.add(lText.get(inField).toString());
		}
		List<String> out = new Vector<String>(lSelected);
		Collections.sort(out);
		return out;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hip.vif.bom.TextHome#selectTitleOrAuthor(java.lang.String, java.lang.String)
	 */
	public QueryResult selectTitleOrAuthor(String inTitle, String inAuthor) throws VException, SQLException {
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(BOMHelper.getKeyPublished(TextHome.KEY_STATE), BinaryBooleanOperator.AND);
		KeyObject lAdd = new KeyObjectImpl();
		lAdd.setValue(TextHome.KEY_TITLE, inTitle);
		lAdd.setValue(TextHome.KEY_AUTHOR, inAuthor, "=", BinaryBooleanOperator.OR);
		lKey.setValue(lAdd, BinaryBooleanOperator.AND);
		return select(lKey);
	}

	/*
	 * (non-Javadoc)
	 * @see org.hip.vif.bom.TextHome#createNewVersion(java.lang.String, org.hip.vif.bom.Text.ITextValues, java.lang.Long)
	 */
	public void createNewVersion(Text inText, ITextValues inValues, Long inActorID) throws VException, SQLException {
		Long lTextID = (Long) inText.get(TextHome.KEY_ID);
		int lVersion = BOMHelper.getTextMaxHome().getMaxVersion(lTextID) + 1;
		Text lText = (Text) create();
		lText.ucNew(new Long(lTextID), lVersion, inText.get(TextHome.KEY_REFERENCE).toString(), inValues, inActorID);
	}
	
	public String checkReference(String inReference) throws VException, SQLException {
		KeyObject lKey = new KeyObjectImpl();
		lKey.setValue(KEY_REFERENCE, inReference + "%", "LIKE");
		lKey.setValue(KEY_STATE, WorkflowAwareContribution.S_DELETED, "!=", BinaryBooleanOperator.AND);
		QueryResult lResult = select(lKey);
		
		//the reference is unique
		if (!lResult.hasMoreElements()) return inReference;
		
		Collection<String> lReferences = new Vector<String>();
		while (lResult.hasMoreElements()) {
			lReferences.add(lResult.nextAsDomainObject().get(KEY_REFERENCE).toString());
		}
		
		//the reference is unique
		if (!lReferences.contains(inReference)) return inReference;
		
		//calculate next unique reference
		CharAppender lAppender = new CharAppender();
		while (true) {
			String out = String.format("%s%s", inReference, lAppender.drawNext());
			if (!lReferences.contains(out)) {
				return out;
			}
			if (lAppender.doBreak()) break;
		}
		return String.format("%s%s", inReference, "*");
	}
	
// --- 
	
	final class CharAppender extends Object {
		private static final int OFFSET = 97; //a
		private static final int RANGE = 26;
		private static final int MAX = 676; //26 * 26
		
		private int current = 0;
		
		String drawNext() {
			String lPrevious = "";
			int lPosition = current / RANGE;
			if (lPosition >= 1) {
				lPrevious = new String(new byte[] {(byte)(OFFSET + lPosition - 1)});
			}
			byte[] lChar = new byte[] {(byte)(OFFSET + (current++ % RANGE))};
			return String.format("%s%s", lPrevious, new String(lChar));
		}
		boolean doBreak() {
			return current > MAX;
		}
	}

}
