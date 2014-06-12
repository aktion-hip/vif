package org.hip.kernel.servlet.test;

import org.hip.kernel.servlet.TaskManager;
import org.hip.kernel.servlet.Task;
import org.hip.kernel.servlet.impl.AbstractTaskManager;

/**
 * Concrete implementation of an AbstractTaskManager for testing purpose.
 * 
 * @author: Benno Luthiger
 */
@SuppressWarnings("serial")
public class TestTaskManager extends AbstractTaskManager {
	private static final String DEFAULT_TASK_NAME 	= "startup";
	/**
	 * TestTaskManager constructor comment.
	 */
	private TestTaskManager() {
		super();
		initialize();
	}
	
	/**
	 * Returns an instance of the default task.
	 *
	 * @return org.hip.kernel.servlet.Task
	 */
	public Task getDefaultTask() {
		return create(DEFAULT_TASK_NAME);
	}
	
	/**
	 * Returns an instance of the error task.
	 *
	 * @return org.hip.kernel.servlet.Task
	 */
	public Task getErrorTask() {
		return create(ERROR_TASK_NAME);
	}
	
	/**
	 * Returns singelton of taskmanager.
	 *
	 * @return org.hip.kernel.servlet.TaskManager instance of task manager
	 */ 
	public static synchronized TaskManager getInstance() {
		if (cManager == null) {
			cManager = (TaskManager)new TestTaskManager();
		}
		return cManager;
	}
	
	/**
	 * Subclasses have to override this method, which initalizes
	 * the mapping taskmappingtable.
	 */
	protected void initialize() {
		taskMappingTable().put(DEFAULT_TASK_NAME, 	"org.hip.kernel.servlet.test.TestTask");
		taskMappingTable().put(ERROR_TASK_NAME, 	"org.hip.kernel.servlet.test.TestErrorTask");
		taskMappingTable().put("testtask", 			"org.hip.kernel.servlet.test.TestTask");
	}
}
