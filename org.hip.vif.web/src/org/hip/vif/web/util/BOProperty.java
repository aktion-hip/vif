/**
	This package is part of the application VIF.
	Copyright (C) 2011-2014, Benno Luthiger

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

package org.hip.vif.web.util;

import java.lang.reflect.Constructor;

import org.hip.kernel.bom.DomainObject;
import org.hip.kernel.bom.GettingException;
import org.hip.kernel.bom.SettingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.util.AbstractProperty;

/**
 * <p>
 * Proxy class for creating <code>Properties</code> from pairs of getter and
 * setter methods of a VIF BOM.
 * </p>
 * 
 * @author Luthiger Created: 31.07.2011
 */
@SuppressWarnings("serial")
public class BOProperty<T> extends AbstractProperty<T> {
	private static final Logger LOG = LoggerFactory.getLogger(BOProperty.class);

	private final DomainObject boInstance;
	private final String key;
	private final Class<T> type;

	/**
	 * Constructor
	 * 
	 * @param inInstance
	 *            {@link DomainObject}
	 * @param inKey
	 *            String the key to access the object's property
	 * @param inType
	 *            Class the value's type
	 */
	public BOProperty(final DomainObject inInstance, final String inKey,
			final Class<T> inType) {
		boInstance = inInstance;
		key = inKey;
		type = inType;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getValue() {
		try {
			final T out = (T) boInstance.get(key);
			return out == null ? (type == String.class ? (T) "" : null) : out; //$NON-NLS-1$
		}
		catch (final GettingException exc) {
			LOG.error("Can't get value for \"{}\"!", key, exc); //$NON-NLS-1$
		}
		return null;
	}

	@Override
	public void setValue(final Object inNewValue) throws ReadOnlyException {
		try {
			if (inNewValue == null) {
				boInstance.set(key, null);
				return;
			}
			Object lValue = inNewValue;
			if (!getType().equals(inNewValue.getClass())) {
				final Constructor<?> lConstructor = getType().getConstructor(
						inNewValue.getClass());
				lValue = lConstructor.newInstance(inNewValue);
			}
			boInstance.set(key, lValue);
		}
		catch (final SettingException exc) {
			LOG.error("Can't set value for \"{}\"!", key, exc); //$NON-NLS-1$
		}
		catch (final Exception exc) {
			LOG.error("Can't set value for \"{}\"!", key, exc); //$NON-NLS-1$
		}
	}

	@Override
	public Class<T> getType() {
		return type;
	}

}
