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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.lucene.document.Document;
import org.hip.kernel.bom.DomainObjectVisitor;
import org.hip.kernel.bom.GeneralDomainObject;
import org.hip.kernel.bom.GeneralDomainObjectHome;
import org.hip.kernel.bom.GettingException;
import org.hip.kernel.bom.KeyObject;
import org.hip.kernel.bom.Property;
import org.hip.kernel.bom.PropertySet;
import org.hip.kernel.bom.SettingException;
import org.hip.kernel.bom.impl.KeyObjectImpl;
import org.hip.kernel.bom.impl.PropertyImpl;
import org.hip.kernel.bom.impl.PropertySetImpl;
import org.hip.kernel.bom.model.ObjectDef;
import org.hip.kernel.exc.VException;

/** A domain object backed by a lucene Document object.
 *
 * @author Benno Luthiger Created on 27.09.2005
 * @see org.apache.lucene.document.Document */
public abstract class AbstractHitsDomainObject implements GeneralDomainObject {
    protected transient Document document;

    /** HitsDomainObject constructor.
     *
     * @param inDocument Document */
    public AbstractHitsDomainObject(final Document inDocument) {
        super();
        document = inDocument;
    }

    /** @see org.hip.kernel.bom.GeneralDomainObject#accept(org.hip.kernel.bom.DomainObjectVisitor) */
    @Override
    public void accept(final DomainObjectVisitor inVisitor) {
        inVisitor.visitDomainObject(this);
    }

    /** @see org.hip.kernel.bom.GeneralDomainObject#getHome() */
    @Override
    public GeneralDomainObjectHome getHome() { // NOPMD
        return null;
    }

    /** @see org.hip.kernel.bom.GeneralDomainObject#getHomeClassName() */
    @Override
    public String getHomeClassName() { // NOPMD
        return "";
    }

    /** @see org.hip.kernel.bom.GeneralDomainObject#getKey() */
    @Override
    public KeyObject getKey() {
        return getKey(true);
    }

    /** @see org.hip.kernel.bom.GeneralDomainObject#getKey(boolean) */
    @Override
    public KeyObject getKey(final boolean inInitial) {
        final KeyObject outKey = new KeyObjectImpl();
        try {
            outKey.setValue(getIDFieldName(), document.get(getIDFieldName()));
        } catch (final VException exc) { // NOPMD
            // Left empty intentionally.
        }
        return outKey;
    }

    /** @see org.hip.kernel.bom.GeneralDomainObject#getObjectDef() */
    @Override
    public ObjectDef getObjectDef() { // NOPMD
        return null;
    }

    /** @see org.hip.kernel.bom.GeneralDomainObject#getObjectName() */
    @Override
    public String getObjectName() {
        final String lName = getClass().getName();
        return lName.substring(lName.lastIndexOf('.') + 1);
    }

    /** @see org.hip.kernel.bom.GeneralDomainObject#isChanged() */
    @Override
    public boolean isChanged() {
        return false;
    }

    /** @see org.hip.kernel.bom.GeneralDomainObject#release() */
    @Override
    public void release() { // NOPMD
        // Intentionally left empty.
    }

    /** @see org.hip.kernel.bom.SemanticObject#set(java.lang.String, java.lang.Object) */
    @Override
    public void set(final String inName, final Object inValue) throws SettingException {
        throw new SettingException("Not applicable with this object");
    }

    /** @see org.hip.kernel.bom.SemanticObject#setVirgin() */
    @Override
    public void setVirgin() { // NOPMD
        // Intentionally left empty.
    }

    /** Returns the ID of the wrapped document.
     *
     * @return String ID */
    public String getID() {
        return document.get(getIDFieldName());
    }

    /** Subclasses have to provide the name of the ID field.
     *
     * @return String */
    protected abstract String getIDFieldName();

    /** @see org.hip.kernel.bom.SemanticObject#get(java.lang.String) */
    @Override
    public Object get(final String inName) throws GettingException {
        if (getFields().contains(inName)) {
            return document.get(inName);
        }
        throw new GettingException("No field named " + inName);
    }

    @Override
    public Iterator<String> getPropertyNames() { // NOPMD
        return getPropertyNames2().iterator();
    }

    @Override
    public Collection<String> getPropertyNames2() { // NOPMD
        return new ArrayList<String>(getFields());
    }

    /** @see org.hip.kernel.bom.SemanticObject#propertySet() */
    @Override
    public PropertySet propertySet() {
        final PropertySet outProperties = new PropertySetImpl(null);
        for (final String lName : getFields()) {
            final Property lProperty = new PropertyImpl(outProperties, lName, document.get(lName));
            outProperties.add(lProperty);
        }
        return outProperties;
    }

    /** Hook for subclasses to return the appropriate List of Document fields.
     *
     * @return List<String> */
    protected abstract List<String> getFields();

    @Override // NOPMD by lbenno 
    public Object clone() throws CloneNotSupportedException { // NOPMD
        return super.clone();
    }

}
