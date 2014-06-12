/*
	This package is part of the application VIF.
	Copyright (C) 2011, Benno Luthiger

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

package org.hip.vif.core.util;

import java.util.Stack;

import org.hip.vif.core.interfaces.IPluggableTask;

/**
 * A stack of <code>IPluggableTask</code> instances.
 * 
 * @author Luthiger
 * Created: 14.07.2011
 */
public class TaskStack {
	private static final int DFT_SIZE = 5;
	
	private Stack<IPluggableTask> taskStack;
	private int stackSize;
	
	/**
	 * Constructor with specified stack size.
	 * 
	 * @param inStackSize int the stack size
	 */
	public TaskStack(int inStackSize) {
		taskStack = new Stack<IPluggableTask>();
		stackSize = inStackSize;
	}

	/**
	 * Constructor with default stack size.
	 */
	public TaskStack() {
		this(DFT_SIZE);
	}
	
	/**
	 * Tests if this stack is empty.
	 * 
	 * @return boolean <code>true</code> it the stack is empty, <code>false</code> otherwise
	 */
	public boolean empty() {
		return taskStack.empty();
	}
	
	/**
	 * Looks at the task at the top of this stack without 
	 * removing it from the stack.
	 * 
	 * @return {@link IPluggableTask}
	 */
	public IPluggableTask peek() {
		return taskStack.peek();
	}
	
	/**
	 * Removes the task at the top of this 
	 * stack and returns that object as the value of this function.
	 * If the stack is empty, an EmptyStackException is thrown.
	 * 
	 * @return {@link IPluggableTask}
	 */
	public IPluggableTask pop() {
		return taskStack.pop();
	}

	/**
	 * Pushes a task onto the top of this stack.
	 * If the stack exceeds the specified size, the element at the stack bottom
	 * is thrown away.
	 * The task is pushed to the stack only if it's not equal to the task at the stack's top. 
	 * 
	 * @param inTask {@link IPluggableTask}
	 * @return {@link IPluggableTask}
	 */
	public IPluggableTask push(IPluggableTask inTask) {
		if (!empty()) {
			if (inTask.equals(taskStack.peek())) return inTask;
		}
		
		IPluggableTask out = taskStack.push(inTask);
		if (taskStack.size() > stackSize) {
			taskStack.removeElementAt(0);
		}
		return out;
	}
	
	/**
	 * Removes all of the elements from this stack.
	 */
	public void clear() {
		taskStack.clear();
	}	
	
}
