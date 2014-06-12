package org.hip.kernel.stext;

/*
	This package is part of the structured text framework used for the application VIF.
	Copyright (C) 2003, Benno Luthiger

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

/**
 * Interface for StructuredTextSerializer.
 * Classes implementing this interface can be used to create
 * HTML formatted texts out of texts which are formatted using the
 * StructuredText rules.
 * 
 * @author: Benno Luthiger
 */
public interface StructuredTextSerializer {
	void visitStructuredText(StructuredText inStructuredText);
	void visitStructuredTextBullet(StructuredTextBullet inStructuredTextBullet);
	void visitStructuredTextNumbered(StructuredTextNumbered inStructuredTextNumbered);
	void visitStructuredTextPlain(StructuredTextPlain inStructuredTextPlain);
}
