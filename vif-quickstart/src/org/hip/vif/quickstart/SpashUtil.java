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

package org.hip.vif.quickstart;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.SplashScreen;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Helper class to paint text on spash screen.
 * 
 * @author Luthiger
 * Created: 08.03.2012
 */
public class SpashUtil {
	//the splash screens background color
	public static final Color BG_COLOR = new Color(255, 148, 24);
	
	private static final int Y_ERROR_MSG = 15;
	private static final int Y_UPPER = 33;
	private static final int Y_LOWER = 46;
	private static final int X_INDENT = 10;
	private static final int WIDTH = 340;
	private static final int HEIGHT = 14;
	
	private Graphics graphics;
	private String oldText; 
	
	/**
	 * Private constructor.
	 * 
	 * @param inSpash {@link SplashScreen}
	 */
	private SpashUtil(SplashScreen inSpash) {
		graphics = inSpash.createGraphics();
		graphics.setClip(new Rectangle(X_INDENT, Y_LOWER-HEIGHT+2, WIDTH, HEIGHT));
		oldText = "";
	}
	
	/**
	 * Writes the echo string.
	 * 
	 * @param inText String
	 */
	public void writeEcho(String inText) {
		graphics.setColor(BG_COLOR);
		graphics.drawString(oldText, X_INDENT, Y_LOWER);
		graphics.setColor(Color.WHITE);
		graphics.drawString(inText, X_INDENT, Y_LOWER);
		oldText = inText;
	}
	
	/**
	 * Factory method, creates an instance of this utility class.
	 * 
	 * @param inSpash {@link SplashScreen}
	 * @return {@link SpashUtil}
	 */
	public static SpashUtil createEchoArea(SplashScreen inSpash) {
		return new SpashUtil(inSpash);
	}
	
	public static void writeUpper(String inText, Graphics inGraphics) {
		inGraphics.drawString(inText, X_INDENT, Y_UPPER);
	}
	
	public static void writeLower(String inText, Graphics inGraphics) {
		inGraphics.setColor(Color.WHITE);
		inGraphics.drawString(inText, X_INDENT, Y_LOWER);
	}
	
	public static void writeErrorMsg(String inErrorMsg, Graphics inGraphics) {
		inGraphics.drawString(inErrorMsg, X_INDENT, Y_ERROR_MSG);		
	}
	
	/**
	 * Returns the splash screen's image.
	 * 
	 * @param inScreen {@link SplashScreen} may be <code>null</code>
	 * @return {@link Image} may be <code>null</code>
	 */
	public static Image getImage(SplashScreen inScreen) {
		if (inScreen == null) {
			return null;
		}
		try {
			return ImageIO.read(inScreen.getImageURL());
		}
		catch (IllegalStateException exc) {
			return null;
		}
		catch (IOException exc) {
			return null;
		}
	}

}
