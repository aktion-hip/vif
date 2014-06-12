package org.hip.kernel.code;

/*
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

/**
 * CodeListItem is a helper class to transfer item information out of an
 * XML-document into the CodeList the item belongs to.
 * Basically the item consists of the elementID and the associated label.
 *
 * @author: Benno Luthiger
 */
public class CodeListItem  {

	//instance attributes
 	private String codeID;
  	private String elementID;
  	private String language;
  	private String label;
/**
 * CodeListItem default constructor.
 */
public CodeListItem() {
}
/**
 * Returns a CodeList belonging to the codeID of this CodeElementItem
 *
 * @return the CodeList for this CodeElementLabel
 */
public CodeList getAssociatedCodeList() throws CodeListNotFoundException {
  	return CodeListHome.instance().getCodeList(codeID , language);
}
/**
 * The codeID of the CodeList the item is belonging to.
 *
 * @return java.lang.String
 */
public String getCodeID() {
	return codeID;
}
/**
 * The ID of this item.
 *
 * @return java.lang.String
 */
public String getElementID() {
	return elementID;
}
/**
 * The label of this item in the language of the CodeList the item is belonging to.
 *
 * @return java.lang.String
 */
public String getLabel() {
	return label;
}
/**
 * The language of the CodeList this item is belonging to.
 *
 * @return java.lang.String
 */
public String getLanguage() {
	return language;
}
public void setCodeID(String inCodeID) {
	codeID = inCodeID;
}
public void setElementID(String inElementID) {
	elementID = inElementID;
}
public void setLabel(String inLabel) {
	label = inLabel;
}
public void setLanguage(String inLanguage ) {
	language = inLanguage;
}
}
