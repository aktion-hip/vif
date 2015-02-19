/**
 This package is part of the application VIF.
 Copyright (C) 2005-2015, Benno Luthiger

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 General Public License for more details.

 You should have received a copy of the GNU General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.hip.vif.core.search;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;

/** A member domain object backed by a lucene Document object. This domain object can be used as result of a full text
 * search.
 *
 * @author Benno Luthiger Created on 02.10.2005 */
public class MemberHitsObject extends AbstractHitsDomainObject {
    private static List<String> fields = new ArrayList<String>();
    static {
        for (final AbstractSearching.IndexField lField : AbstractSearching.MEMBER_FIELDS) {
            fields.add(lField.fieldName);
        }
    }

    /** @param inDocument */
    public MemberHitsObject(final Document inDocument) {
        super(inDocument);
    }

    @Override
    protected List<String> getFields() { // NOPMD
        return fields;
    }

    @Override
    protected String getIDFieldName() { // NOPMD
        return AbstractSearching.IndexField.MEMBER_ID.fieldName;
    }
}
