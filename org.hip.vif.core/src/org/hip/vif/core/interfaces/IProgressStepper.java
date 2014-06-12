/*
	This package is part of the application VIF.
	Copyright (C) 2012, Benno Luthiger

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

package org.hip.vif.core.interfaces;

/**
 * Interface for classes providing a process indicator.
 * 
 * @author Luthiger
 * Created: 17.02.2012
 */
public interface IProgressStepper {
	
	/**
	 * Indicates the next step in the upgrade process.
	 * 
	 * @param inActStep int the actual step, 0 <= inActStep <= inMaxSteps
	 * @param inMaxSteps int the total number of steps
	 */
	void nextStep(int inActStep, int inMaxSteps);

}
