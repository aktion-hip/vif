/**
	This package is part of the framework used for the application VIF.
	Copyright (C) 2006-2014, Benno Luthiger

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

package org.hip.kernel.bom.directory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.Iterator;

import javax.naming.directory.SearchResult;

import org.hip.kernel.bom.BOMException;
import org.hip.kernel.bom.BOMNotFoundException;
import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.impl.AbstractQueryResult;
import org.hip.kernel.exc.DefaultExceptionHandler;
import org.hip.kernel.exc.VException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Implementation of <code>QueryResult</code> for LDAP queries.
 *
 * @author Luthiger Created on 03.07.2007
 * @see org.hip.kernel.bom.QueryResult */
@SuppressWarnings("serial")
public class LDAPQueryResult extends AbstractQueryResult {
    private static final Logger LOG = LoggerFactory.getLogger(LDAPQueryResult.class);

    private LDAPObjectHome home;
    private Iterator<SearchResult> result;
    private LDAPQueryStatement statement;

    private GeneralDomainObject nextObj;
    private GeneralDomainObject current;
    private final int count;

    /** LDAPQueryResult constructor
     *
     * @param inHome LDAPObjectHome
     * @param inResult Iterator<SearchResult>
     * @param inCount int number of entries in the result enumeration
     * @param inStatement LDAPQueryStatement */
    public LDAPQueryResult(final LDAPObjectHome inHome, final Iterator<SearchResult> inResult, final int inCount,
            final LDAPQueryStatement inStatement) {
        super(inHome);

        home = inHome;
        result = inResult;
        count = inCount;
        statement = inStatement;

        LOG.debug("count: {}", inCount);
        firstRead();
    }

    private void firstRead() {
        try {
            if (result.hasNext()) {
                nextObj = home.newInstance(result.next());
            }
        } catch (final VException exc) {
            DefaultExceptionHandler.instance().handle(exc);
        }
    }

    @Override
    public void close() throws SQLException { // NOPMD by lbenno
        if (statement != null) {
            statement.close();
        }
    }

    @Override
    public GeneralDomainObject getCurrent() { // NOPMD by lbenno
        return current;
    }

    @Override
    public KeyObject getKey() throws BOMNotFoundException { // NOPMD by lbenno
        if (current == null) {
            throw new BOMNotFoundException();
        }
        else {
            return current.getKey(); // NOPMD by lbenno
        }
    }

    @Override
    public boolean hasMoreElements() { // NOPMD by lbenno
        return nextObj != null;
    }

    @Override
    public GeneralDomainObject next() throws SQLException, BOMException { // NOPMD by lbenno
        GeneralDomainObject outModel = null;
        if (result != null) {
            current = nextObj;
            outModel = current;
            if (result.hasNext()) {
                nextObj = home.newInstance(result.next());
            }
            else {
                nextObj = null; // NOPMD by lbenno
                this.close();
                result = null; // NOPMD by lbenno
            }
        }
        return outModel;
    }

    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.writeObject(home);
        out.writeObject(current);
        out.writeObject(nextObj);
        out.writeObject(statement);
    }

    private void readObject(final ObjectInputStream inStream) throws IOException, ClassNotFoundException {
        home = (LDAPObjectHome) inStream.readObject();
        final GeneralDomainObject lCurrent = (GeneralDomainObject) inStream.readObject();
        nextObj = (GeneralDomainObject) inStream.readObject();
        statement = (LDAPQueryStatement) inStream.readObject();

        try {
            // we retrieve the NamingEnumeration backing this object using the LDAPQueryStatement
            result = statement.retrieveStatement();
            if (result == null) {
                return;
            }

            // now we position the cursor on the correct place
            if (lCurrent == null) {
                firstRead();
            }
            else {
                while (!lCurrent.equals(current)) {
                    next();
                    if (!hasMoreElements()) {
                        return;
                    }
                }
            }
        } catch (final SQLException | BOMException exc) {
            throw new IOException(exc.getMessage(), exc);
        }
    }

    /** Returns the number of entries contained in this result.
     *
     * @return int the number of entries the result enumeration contains. */
    public int getCount() {
        return count;
    }

}
