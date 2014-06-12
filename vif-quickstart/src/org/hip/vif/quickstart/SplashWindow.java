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

package org.hip.vif.quickstart;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.SplashScreen;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Helper class to create and manage a splash window.<br/>
 * The splash window displays a shutdown button to stop the OSGi runtime started by the application's main method.<br />
 * The splash window is displayed after the application's splash screen is disposed.
 * 
 * @author Luthiger
 * Created: 24.01.2012
 */
public class SplashWindow {
	
	/**
	 * Factory method, creates an instance of <code>SplashFrame</code>.
	 * 
	 * @param inSplash {@link SplashScreen} the application's splash screen
	 * @param inStarter {@link OSGiStarter}
	 * @return {@link SplashFrame}
	 */
	public static SplashFrame createSplashWindow(SplashScreen inSplash, final OSGiStarter inStarter) {
		SplashFrame out = new SplashFrame(inSplash, inStarter);
		ComponentMover lMover = new ComponentMover();
		lMover.registerComponent(out);
		return out;
	}
	
// --- the splash window class ---
	
	public static class SplashFrame extends JFrame {
		private static final long serialVersionUID = 1L;
		
		private JButton shutdown;
		private SplashScreen splash;
		private BackgroundPanel background;

		SplashFrame(SplashScreen inSplash, final OSGiStarter inStarter) {
			Container lContainer = getContentPane();
			
			background = new BackgroundPanel();
			if (inSplash != null) {				
				splash = inSplash;
				Rectangle lBounds = inSplash.getBounds();
				setBounds(inSplash.getBounds());
				background = new BackgroundPanel(SpashUtil.getImage(splash));
				background.setBounds(0, 0, lBounds.width, lBounds.height);
			}
			lContainer.add(background);

			setUndecorated(true);
			lContainer.setLayout(null);
			
			shutdown = new JButton("Shutdown");
			shutdown.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent inEvent) {
					inStarter.shutdown();
				}
			});
			shutdown.setBounds(10, 53, 100, 25);
			background.add(shutdown);
		}
		
		public void setErrorMsg(String inMsg) {
			background.setErrorMsg(inMsg);
		}

		void showSplash() {
			if (splash != null) {
				splash.close();
			}
			
			setVisible(true);
			toFront();
		}

	}
	
	private static class BackgroundPanel extends JPanel {
		private static final Logger LOG = Logger.getLogger(BackgroundPanel.class.getName());
		
		private static final long serialVersionUID = 1L;
		private String errMsg = null;
		private Image image = null;;		

		BackgroundPanel() {
			setLayout(null);
		}
		BackgroundPanel(Image inImage) {
			this();
			image = inImage;
		}
		@Override
		public void paintComponent(Graphics inGraphics) {
			if (image == null) {
				super.paint(inGraphics);
			}
			try {
				inGraphics.drawImage(image, 0, 0, null);
				if (inGraphics instanceof Graphics2D) {					
					((Graphics2D) inGraphics).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
				}
				inGraphics.setColor(Color.WHITE);
				if (errMsg != null) {
					SpashUtil.writeErrorMsg(errMsg, inGraphics);
				}
				SpashUtil.writeUpper("OSGi runtime started.", inGraphics);
				SpashUtil.writeLower(String.format("OSGi runtime is listening on port %s.", Constants.OSGI_CONSOLE_PORT), inGraphics);
			}
			catch (IllegalStateException exc) {
				LOG.log(Level.SEVERE, "Error encountered while painting the splash window!", exc);
			}
		}
		public void setErrorMsg(String inMsg) {
			errMsg = inMsg;
			this.repaint();
		}
	}

// --- the mover class for the splash window ---
	
	private static class ComponentMover extends MouseAdapter {
		private Component destination;
		private Component source;
		
		private Point pressed;
		private Point location;
		
		private Cursor originalCursor;
		private boolean autoscrolls;
		private boolean potentialDrag;
		
		/**
		 *  Add the required listeners to the specified component
		 *
		 *  @param component  the component the listeners are added to
		 */
		public void registerComponent(Component... inComponents) {
			for (Component lComponent : inComponents)
				lComponent.addMouseListener(this);
		}
		
		/**
		 *  Remove listeners from the specified component
		 *
		 *  @param component  the component the listeners are removed from
		 */
		@SuppressWarnings("unused")
		public void deregisterComponent(Component... inComponents) {
			for (Component lComponent : inComponents)
				lComponent.removeMouseListener( this );
		}
		
		/**
		 *  Setup the variables used to control the moving of the component:
		 *
		 *  source - the source component of the mouse event
		 *  destination - the component that will ultimately be moved
		 *  pressed - the Point where the mouse was pressed in the destination
		 *      component coordinates.
		 */
		@Override
		public void mousePressed(MouseEvent inEvent) {
			source = inEvent.getComponent();
			int lWidth  = source.getSize().width;
			int lHeight = source.getSize().height;
			Rectangle lRectangle = new Rectangle(0, 0, lWidth, lHeight);
			
			if (lRectangle.contains(inEvent.getPoint()))
				setupForDragging(inEvent);
		}
		
		private void setupForDragging(MouseEvent inEvent)	{
			source.addMouseMotionListener(this);
			potentialDrag = true;
			
			destination = source;
			pressed = inEvent.getLocationOnScreen();
			location = destination.getLocation();
			
			originalCursor = source.getCursor();
			source.setCursor( Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR) );
			
			//  Making sure autoscrolls is false will allow for smoother dragging of
			//  individual components
			
			if (destination instanceof JComponent) {
				JComponent lComponent = (JComponent)destination;
				autoscrolls = lComponent.getAutoscrolls();
				lComponent.setAutoscrolls(false);
			}
		}
		
		/**
		 *  Move the component to its new location. The dragged Point must be in
		 *  the destination coordinates.
		 */
		@Override
		public void mouseDragged(MouseEvent inEvent) {
			Point lDragged = inEvent.getLocationOnScreen();
			int lDragX = getDragDistance(lDragged.x, pressed.x);
			int lDragY = getDragDistance(lDragged.y, pressed.y);
			
			int lLocationX = location.x + lDragX;
			int lLocationY = location.y + lDragY;
			
			//  Mouse dragged events are not generated for every pixel the mouse
			//  is moved. Adjust the location to make sure we are still on a
			//  snap value.
			
			while (lLocationX < 0)
				lLocationX += 1;
			
			while (lLocationY < 0)
				lLocationY += 1;
			
			Dimension lDimension = getBoundingSize( destination );
			
			while (lLocationX + destination.getSize().width > lDimension.width)
				lLocationX -= 1;
			
			while (lLocationY + destination.getSize().height > lDimension.height)
				lLocationY -= 1;
			
			//  Adjustments are finished, move the component
			destination.setLocation(lLocationX, lLocationY);
		}
		
		/*
		 *  Determine how far the mouse has moved from where dragging started
		 *  (Assume drag direction is down and right for positive drag distance)
		 */
		private int getDragDistance(int inLarger, int inSmaller) {
			int outDrag = inLarger - inSmaller;
			outDrag += (outDrag < 0) ? -1 : 1;
			return outDrag;
		}
		
		/*
		 *  Get the bounds of the parent of the dragged component.
		 */
		private Dimension getBoundingSize(Component inSource)	{
			if (inSource instanceof Window) {
				Rectangle lBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
				return new Dimension(lBounds.width, lBounds.height);
			}
			else {
				return inSource.getParent().getSize();
			}
		}
		
		/**
		 *  Restore the original state of the Component
		 */
		@Override
		public void mouseReleased(MouseEvent inEvent)	{
			if (!potentialDrag) return;
			
			source.removeMouseMotionListener( this );
			potentialDrag = false;
			source.setCursor(originalCursor);
			
			if (destination instanceof JComponent) {
				((JComponent)destination).setAutoscrolls(autoscrolls);
			}
		}
	}
	
// --- the splash screen's progress bar ---

	/**
	 * Helper class to indicate progress on the application's splash screen. 
	 * 
	 * @author Luthiger
	 * Created: 24.01.2012
	 */
	public static class ProgressBar {
		private static final int WIDTH = 220;
		private static final int OFFSET_X = 10;		
		
		private SplashScreen splash;
		private int maxValue;
		private Graphics2D graphics;
		private int actValue;
		private int increment;
		private SpashUtil splashUtil;
		
		ProgressBar(SplashScreen inSplash) {
			splash = inSplash;
			splashUtil = SpashUtil.createEchoArea(splash);
			actValue = 0;
			maxValue = 100;
			increment = WIDTH / maxValue;
			
			if (inSplash != null) {
				graphics = splash.createGraphics();
				graphics.setComposite(AlphaComposite.Clear);
				graphics.setPaintMode();
				graphics.setColor(new Color(255,212,163));
				graphics.fillRect(OFFSET_X, 60, WIDTH, 12);
				graphics.setColor(Color.WHITE);
				graphics.drawRect(OFFSET_X, 60, WIDTH, 12);
				graphics.setColor(Color.BLACK);
			}
		}
		public void setMaxValue(int inMaxValue) {
			maxValue = inMaxValue;
			increment = WIDTH / maxValue;
		}
		public void progress() {
			graphics.fillRect(OFFSET_X + (actValue++ * increment), 60, increment, 13);
			splash.update();
		}
		/**
		 * Writes the entry name on the spash screen.
		 * 
		 * @param inEntryName String
		 */
		public void echoExtracted(String inEntryName) {
			splashUtil.writeEcho(inEntryName);
		}
	}

}
