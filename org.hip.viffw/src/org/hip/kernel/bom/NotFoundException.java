/**
 This package is part of the servlet framework used for the application VIF.
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
package org.hip.kernel.bom;

import org.hip.kernel.exc.VException;

/** This class simply inherits from org.hip.kernel.exc.VException. Actually it does not add any functionality. */
@SuppressWarnings("serial")
public class NotFoundException extends VException {
    /** NotFoundException default constructor. */
    public NotFoundException() {
        super();
    }

    /** NotFoundException constructor with a given message.
     *
     * @param inMessage java.lang.String */
    public NotFoundException(final String inMessage) {
        super(inMessage);
    }
}
