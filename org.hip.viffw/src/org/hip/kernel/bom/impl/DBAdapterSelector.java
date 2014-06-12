/*
	This package is part of the framework used for the application VIF.
	Copyright (C) 2001, Benno Luthiger

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
package org.hip.kernel.bom.impl;

import java.io.IOException;

import org.hip.kernel.bom.ColumnModifier;
import org.hip.kernel.bom.DBAdapterJoin;
import org.hip.kernel.bom.DBAdapterSimple;
import org.hip.kernel.bom.model.JoinedObjectDef;
import org.hip.kernel.bom.model.ObjectDef;
import org.hip.kernel.exc.DefaultExceptionWriter;
import org.hip.kernel.sys.VObject;
import org.hip.kernel.sys.VSys;
import org.hip.kernel.sys.VSysConstants;

/**
 * This class selects the appropriate adapter for the application 
 * either by evaluating the connection's metadata (JNDI case) or
 * according to the information provided by the application's properties file
 * (i.e. <code>org.hip.vif.db.url</code> in <i>vif.properties</i>).
 * 
 * Created on 30.08.2002
 * @author Benno Luthiger
 * @deprecated
 */
public class DBAdapterSelector extends VObject {
	//The singleton.
	private static DBAdapterSelector singleton = new DBAdapterSelector();	
	
	public enum AdapterType {
		DB_TYPE_MYSQL("jdbc:mysql:", new DefaultDBAdapterSimpleFactory(), new MySQLAdapterJoinFactory(), new MySQLColumnModifierUCase()),
		DB_TYPE_POSTGRESQL("jdbc:postgresql:", new DefaultDBAdapterSimpleFactory(), new MySQLAdapterJoinFactory(), new MySQLColumnModifierUCase()),
		DB_TYPE_ORACLE("jdbc:oracle:thin:", new DefaultDBAdapterSimpleFactory(), new OracleAdapterJoinFactory(), new OracleColumnModifierUCase()),
		DB_TYPE_DERBY("jdbc:derby:", new DerbyDBAdapterSimpleFactory(), new MySQLAdapterJoinFactory(), new MySQLColumnModifierUCase());
		
		private String type;
		private IAdapterSimpleFactory simpleAdapterFactory;
		private IAdapterJoinFactory joinAdapterFactory;
		private ColumnModifier columnModifier;
		AdapterType(String inType, IAdapterSimpleFactory inSimpleAdapterFactory, IAdapterJoinFactory inJoinAdapterFactory, ColumnModifier inColumnModifier) {
			type = inType;
			simpleAdapterFactory = inSimpleAdapterFactory;
			joinAdapterFactory = inJoinAdapterFactory;
			columnModifier = inColumnModifier;
		}
		public String getType() {
			return type;
		}
		public boolean isOfType(String inUrl) {
			return inUrl.indexOf(type) == 0;
		}
		public DBAdapterSimple getSimpleDBAdapter(ObjectDef inObjectDef) {
			return simpleAdapterFactory.createAdapterSimple(inObjectDef);
		}
		public DBAdapterJoin getJoinDBAdapter(JoinedObjectDef inObjectDef) {
			return joinAdapterFactory.createAdapterJoin(inObjectDef);
		}
		public ColumnModifier getColumnModifierUCase() {
			return columnModifier;
		}
	}
	
	private AdapterType adapterType = null;

	/**
	 * Constructor for DBAdapterSelector.
	 */
	private DBAdapterSelector() {
		super();
	}

	/**
	 * <p>Returns the singleton instance of DBAdapterSelector.</p>
	 * <p><b>Note:</b> this method is synchronized to ensure that every thread gets an initialized singleton.</p>
	 * 
	 * @return DBAdapterSelector
	 */
	public static DBAdapterSelector getInstance() {
		synchronized (singleton) {
			if (singleton.isUnitialized()) {
				singleton.initialize();
			}
		}
		return singleton;
	}
	
	private boolean isUnitialized() {
		return adapterType == null;
	}

	private void initialize() {
		adapterType = AdapterType.DB_TYPE_MYSQL;
	}
	
	private String getFromProperties() {
		try {
			return VSys.getVSysProperties().getProperty(VSysConstants.KEY_DB_URL);
		} 
		catch (IOException exc) {
			DefaultExceptionWriter.printOut(this, exc, true);
			return AdapterType.DB_TYPE_MYSQL.getType();
		}
	}
	
	/**
	 * Returns the database adapter for simple domain objects
	 * appropriate to the running system.
	 * 
	 * @param inObjectDef org.hip.kernel.bom.model.ObjectDef
	 * @return org.hip.kernel.bom.DBAdapterSimple
	 */
	public DBAdapterSimple getSimpleDBAdapter(ObjectDef inObjectDef) {
		return adapterType.getSimpleDBAdapter(inObjectDef);
	}
	
	/**
	 * Returns the database adapter for joined domain objects
	 * appropriate to the running system.
	 * 
	 * @param inObjectDef org.hip.kernel.bom.model.JoinedObjectDef
	 * @return org.hip.kernel.bom.DBAdapterJoin
	 */
	public DBAdapterJoin getJoinDBAdapter(JoinedObjectDef inObjectDef) {
		return adapterType.getJoinDBAdapter(inObjectDef);
	}
	
	/**
	 * Returns the column modifier to modify a column value to upper case.
	 * 
	 * @return org.hip.kernel.bom.ColumnModifier
	 */
	public ColumnModifier getColumnModifierUCase() {
		return adapterType.getColumnModifierUCase();
	}
	
	/**
	 * <p>Resets the database selector driver.
	 * This method has to be called to change the database.</p>
	 * <p><b>Note:</b> we synchronize this method for that the changed value gets flushed immediately to the main memory
	 * (according to the rule that we have to synchronize whenever we are writing a variable that may be read next by another thread).</p>
	 */
	public synchronized void reset() {
		adapterType = null;
	}
	
//	--- private classes ---
	
	private interface IAdapterSimpleFactory {
		public DBAdapterSimple createAdapterSimple(ObjectDef inObjectDef);
	}
	static private class DefaultDBAdapterSimpleFactory implements IAdapterSimpleFactory {
		public DBAdapterSimple createAdapterSimple(ObjectDef inObjectDef) {
			return new DefaultDBAdapterSimple(inObjectDef);
		}		
	}
	static private class DerbyDBAdapterSimpleFactory implements IAdapterSimpleFactory {
		public DBAdapterSimple createAdapterSimple(ObjectDef inObjectDef) {
			return new DerbyDBAdapterSimple(inObjectDef);
		}		
	}
	
	private interface IAdapterJoinFactory {
		public DBAdapterJoin createAdapterJoin(JoinedObjectDef inObjectDef);
	}
	static private class MySQLAdapterJoinFactory implements IAdapterJoinFactory {
		public DBAdapterJoin createAdapterJoin(JoinedObjectDef inObjectDef) {
			return new MySQLAdapterJoin(inObjectDef);
		}		
	}
	static private class OracleAdapterJoinFactory implements IAdapterJoinFactory {
		/**
		 * After switching to ANSI join, we can reuse <code>MySQLAdapterJoin</code> for oracle too.
		 */
		public DBAdapterJoin createAdapterJoin(JoinedObjectDef inObjectDef) {
			return new MySQLAdapterJoin(inObjectDef);
		}
	}
	
}