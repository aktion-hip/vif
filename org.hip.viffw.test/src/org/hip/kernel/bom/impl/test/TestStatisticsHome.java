package org.hip.kernel.bom.impl.test;

import java.util.Vector;

import org.hip.kernel.bom.BOMException;
import org.hip.kernel.bom.GroupByObject;
import org.hip.kernel.bom.HavingObject;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.LimitObject;
import org.hip.kernel.bom.impl.AbstractStatisticsHomeImpl;

/**
 * @author: Benno Luthiger
 */
@SuppressWarnings("serial")
public class TestStatisticsHome extends AbstractStatisticsHomeImpl {
	private static final String XML_INTERN_DEF =
		"<?xml version='1.0' encoding='us-ascii'?>					\n" +
		"<objectDef objectName='TestStatistics' parent='org.hip.kernel.bom.ReadOnlyDomainObject' version='1.0'> \n" +
		"	<keyDefs>																				    \n" +
		"		<keyDef>																				\n" +
		"			<keyItemDef seq='0' keyPropertyName=''/>                                            \n" +
		"		</keyDef>																				\n" +
		"	</keyDefs>																					\n" +
		"	<propertyDefs>																				\n" +
		"		<propertyDef propertyName='timeStamp' valueType='Timestamp' propertyType='simple'>		\n" +
		"			<mappingDef tableName='tblQustion' columnName='MUTDATUM'/>							\n" +
		"		</propertyDef>																			\n" +
		"		<propertyDef propertyName='laufNummer' valueType='Integer' propertyType='simple'>		\n" +
		"			<mappingDef tableName='tblQustion' columnName='SERIALNR'/>							\n" +
		"		</propertyDef>																			\n" +
		"		<propertyDef propertyName='dokTypIndir' valueType='String' propertyType='simple'>		\n" +
		"			<mappingDef tableName='tblQustion' columnName='DOKTYPE'/>							\n" +
		"		</propertyDef>																			\n" +
		"		<propertyDef propertyName='questionQuali' valueType='String' propertyType='simple'>		\n" +
		"			<mappingDef tableName='tblQustion' columnName='QUALIFIER'/>							\n" +
		"		</propertyDef>																			\n" +
		"		<propertyDef propertyName='questionID' valueType='String' propertyType='simple'>		\n" +
		"			<mappingDef tableName='tblQustion' columnName='ID'/>								\n" +
		"		</propertyDef>																			\n" +
		"		<propertyDef propertyName='groupQuali' valueType='String' propertyType='simple'>		\n" +
		"			<mappingDef tableName='tblGroup' columnName='QUALIFIER'/>							\n" +
		"		</propertyDef>																			\n" +
		"		<propertyDef propertyName='groupID' valueType='Integer' propertyType='simple'>			\n" +
		"			<mappingDef tableName='tblGroup' columnName='GROUPID'/>								\n" +
		"		</propertyDef>																			\n" +
		"	</propertyDefs>																				\n" +
		"</objectDef>																					  ";

	private static final String XML_EXTERN_DEF =
		"<?xml version='1.0' encoding='us-ascii'?>					\n" +
		"<objectDef objectName='TestStatistics' parent='org.hip.kernel.bom.ReadOnlyDomainObject' version='1.0'>	\n" +
		"	<keyDefs>	\n" +
		"		<keyDef>	\n" +
		"			<keyItemDef seq='0' keyPropertyName='sortOrder'/>	\n" +
		"			<keyItemDef seq='1' keyPropertyName='period'/>	\n" +
		"		</keyDef>	\n" +
		"	</keyDefs>	\n" +
		"	<propertyDefs>	\n" +
		"		<propertyDef propertyName='sortOrder' valueType='String' propertyType='simple'>	\n" +
		"			<mappingDef tableName='TEMP' columnName='SORTORDER'/>	\n" +
		"		</propertyDef>	\n" +
		"		<propertyDef propertyName='period' valueType='String' propertyType='simple'>	\n" +
		"			<mappingDef tableName='TEMP' columnName='PERIOD'/>	\n" +
		"		</propertyDef>	\n" +
		"		<propertyDef propertyName='count1' valueType='Integer' propertyType='simple'>	\n" +
		"			<mappingDef tableName='TEMP' columnName='C1'/>	\n" +
		"		</propertyDef>	\n" +
		"		<propertyDef propertyName='count2' valueType='Integer' propertyType='simple'>	\n" +
		"			<mappingDef tableName='TEMP' columnName='C2'/>	\n" +
		"		</propertyDef>	\n" +
		"		<propertyDef propertyName='count3' valueType='Integer' propertyType='simple'>	\n" +
		"			<mappingDef tableName='TEMP' columnName='C3'/>	\n" +
		"		</propertyDef>	\n" +
		"	</propertyDefs>	\n" +
		"</objectDef>	";

	public TestStatisticsHome() {
		super();
	}
	/**
	 * createCountAllString method comment.
	 */
	protected String createCountAllString() {
		return null;
	}
	/**
	 * createCountString method comment.
	 */
	protected String createCountString(KeyObject inKey) {
		return null;
	}
	protected String createCountString(KeyObject inKey, HavingObject inHaving, GroupByObject inGroupBy) {
		return null;
	}
	/**
	 * createKeyCountColumnList method comment.
	 */
	protected String createKeyCountColumnList() {
		return null;
	}
	/**
	 * createSelectAllString method comment.
	 */
	protected String createSelectAllString() {
		return null;
	}
	/**
	 * createSelectString method comment.
	 */
	protected String createSelectString(org.hip.kernel.bom.KeyObject inKey) {
		return null;
	}
	protected String createSelectString(KeyObject inKey, LimitObject inLimit) throws BOMException {
		return null;
	}
	/**
	 * The purpose of this public method is to create an SQL query string
	 * contingent on the inputted parameter.
	 * The aim is to construct this string without using an
	 * explicite name (table or column) of the underlying table.
	 * The resulting SQL string can then be used with the select-method of 
	 * this home: lStatisticsHome.select(lStatisticsHome.createSQL(inParameter))
	 *
	 * @return java.lang.String SQL query string
	 */
	public String createSQL(Object inParam) {
		String[] lTest = {"111", "222", "123"};
		
		return createSQLWhereAbs(lTest, 2);
	}
	/**
	 * Returns a SQL select string.
	 * Counts the number of processed entries of the specified group.
	 * 
	 * @return java.lang.String
	 * @param inGroupID int
	 * @param inSortOrderDateTemplate java.lang.String
	 * @param inPeriodDateTemplate java.lang.String
	 */
	private String createSQLperDoktype(int inGroupID, String inSortOrderDateTemplate, String inPeriodDateTemplate) {
		StringBuffer lSQL = new StringBuffer();
		String lGroupID = String.valueOf(inGroupID);
		
		lSQL.append("SELECT TO_CHAR(" + getColumnNameInternFor("timeStamp") + ", '" + inSortOrderDateTemplate + "') \"S\", ");
		lSQL.append("TO_CHAR(" + getColumnNameInternFor("timeStamp") + ", '" + inPeriodDateTemplate + "') \"P\", ");
		lSQL.append(getColumnNameInternFor("laufNummer") + ", " + getColumnNameInternFor("dokTypIndir") + ", ");
		
		lSQL.append(" FROM " + tableNameString());
		lSQL.append(" WHERE " + getColumnNameInternFor("questionQuali") + " = " + getColumnNameInternFor("groupQuali"));
		lSQL.append(" AND " + getColumnNameInternFor("groupID") + " = " + lGroupID);
		
		return lSQL.toString();
	}
	/**
	 * Returns a SQL select string.
	 * Counts the number of processed documents from the specified sender.
	 * 
	 * @return java.lang.String
	 * @param inGroupIDs java.lang.String[]
	 * @param inSenderQuali java.lang.String
	 * @param inSenderID java.lang.String
	 * @param inPeriod int
	 */
	private String createSQLWhereAbs(String[] inGroupIDs, int inPeriod) {
		StringBuffer lSQL = new StringBuffer();
		String lSortOrderDateTemplate = "";
		String lPeriodDateTemplate = "";
	
		switch (inPeriod) {
			case 1: //PER_DAY:
				lSortOrderDateTemplate = "yyyyMMdd";
				lPeriodDateTemplate = "dd.MM.yyyy";
				break;
			case 2: //PER_MONTH:
				lSortOrderDateTemplate = "yyyyMM";
				lPeriodDateTemplate = "MM yyyy";
				break;
			case 3: //PER_YEAR:
				lSortOrderDateTemplate = "yyyy";
				lPeriodDateTemplate = "yyyy";
				break;
		}
		boolean lFirst = true;
	
		lSQL.append("SELECT S \"SORTORDER\", P \"PERIOD\", FROM ( \n");
	
		for (int i = 0; i < inGroupIDs.length; i++) {
			if (!lFirst) {
				lSQL.append("UNION ALL \n");
			}
			lFirst = false;
			lSQL.append(createSQLperDoktype(Integer.parseInt(inGroupIDs[i]), lSortOrderDateTemplate, lPeriodDateTemplate) + "\n");
		}
		
		lSQL.append(") GROUP BY S, P");
			
		return lSQL.toString();
	}
	/**
	 * Creates a vector containing objects for testing purpose.
	 */
	protected Vector<Object> createTestObjects() {
	
		Vector<Object> outTest = new Vector<Object>();
		outTest.addElement(createSQL(null));
		
		return outTest;
	}
	/**
	 * getObjectClassName method comment.
	 */
	public String getObjectClassName() {
		return null;
	}
	/**
	 * getObjectDefString method comment.
	 */
	protected String getObjectDefString() {
		return null;
	}
	/**
	 * getObjectDefStringExtern method comment.
	 */
	protected String getObjectDefStringExtern() {
		return XML_EXTERN_DEF;
	}
	/**
	 * getObjectDefStringIntern method comment.
	 */
	protected String getObjectDefStringIntern() {
		return XML_INTERN_DEF;
	}
	/**
	 * newInstance method comment.
	 */
	public org.hip.kernel.bom.ReadOnlyDomainObject newInstance() throws org.hip.kernel.bom.BOMException {
		return null;
	}
}
