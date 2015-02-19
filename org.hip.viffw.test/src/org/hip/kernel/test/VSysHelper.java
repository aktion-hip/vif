/**
    This package is part of the application VIF.
    Copyright (C) 2014, Benno Luthiger

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
package org.hip.kernel.test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/** @author lbenno */
public enum VSysHelper {
    INSTANCE;

    private static final String NAME_WAITING = "vif.properties.sample";
    private static final String NAME_RUNNING = "vif.properties";

    private int counter;

    /** Call this method in a suite's or class' <code>@BeforeClass</code> method. */
    public void copyToRunning() {
        this.counter++;
        final File lDir = new File("./bin");
        final File lWaiting = new File(lDir, NAME_WAITING);
        final File lRunning = new File(lDir, NAME_RUNNING);
        if (lWaiting.exists() && !lRunning.exists()) {
            try {
                Files.copy(lWaiting.toPath(), lRunning.toPath());
                System.out.println("Successfully copied properties to " + lRunning.getAbsolutePath());
            } catch (final IOException exc) {
                // intentionally left empty
            }
        }
    }

    /** Call this method in a suite's or class' <code>@AfterClass</code> method. */
    public void deleteRunning() {
        this.counter--;
        final File lDir = new File("./bin");
        final File lRunning = new File(lDir, NAME_RUNNING);
        if (lRunning.exists() && this.counter <= 0) {
            if (lRunning.delete()) {
                System.out.println("Successfully deleted properties " + lRunning.getAbsolutePath());
            }
            else {
                lRunning.deleteOnExit();
            }
        }
    }

}
