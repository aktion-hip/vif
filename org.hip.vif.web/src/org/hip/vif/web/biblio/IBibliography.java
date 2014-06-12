/**
 This package is part of the application VIF.
 Copyright (C) 2010-2014, Benno Luthiger

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
package org.hip.vif.web.biblio;

import org.hip.kernel.exc.VException;

/**
 * Interface for bibliography objects.
 * 
 * @author Luthiger Created: 24.06.2010
 */
public interface IBibliography {

	public TextType getType() throws VException;

	public String getAuthor() throws VException;

	public String getCoAuthor() throws VException;

	public String getYear() throws VException;

	public String getTitle() throws VException;

	public String getSubtitle() throws VException;

	public String getPlace() throws VException;

	public String getPublisher() throws VException;

	public String getPublication() throws VException;

	public String getVolume() throws VException;

	public String getNumber() throws VException;

	public String getPages() throws VException;

}
