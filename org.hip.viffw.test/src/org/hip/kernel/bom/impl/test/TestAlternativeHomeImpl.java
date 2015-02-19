package org.hip.kernel.bom.impl.test;

import java.sql.SQLException;

import org.hip.kernel.bom.QueryResult;
import org.hip.kernel.bom.QueryStatement;
import org.hip.kernel.bom.impl.AlternativeQueryResult;
import org.hip.kernel.bom.impl.AlternativeQueryStatement;
import org.hip.kernel.sys.Assert;
import org.hip.kernel.sys.VSys;

/** @author Benno Luthiger Created on Sep 22, 2004 */
@SuppressWarnings("serial")
public class TestAlternativeHomeImpl extends Test2DomainObjectHomeImpl {

    /** This method creates a QueryStatement as part of the frameworks QueryService.
     *
     * @return org.hip.kernel.bom.QueryStatement */
    @Override
    public QueryStatement createQueryStatement() {
        return new AlternativeQueryStatement(this);
    }

    @Override
    public QueryResult select(final QueryStatement inStatement) throws SQLException {
        if (VSys.assertNotNull(this, "select(QueryStatement)", inStatement) == Assert.FAILURE) {
            return new AlternativeQueryResult(null, null, null);
        }

        return inStatement.executeQuery();
    }
}
