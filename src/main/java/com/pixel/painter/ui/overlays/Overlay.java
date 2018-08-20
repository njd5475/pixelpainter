package com.pixel.painter.ui.overlays;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JToolBar;

import com.pixel.painter.controller.ImageController;
import com.pixel.painter.ui.PixelPainter;

public class Overlay {

	private static final int			menuAlpha							= 225;
	protected static final Color	SLIDEMENU_BACKGROUND	= new Color(50, 50, 50,
			menuAlpha);
	private final JToolBar				toolbar;

	protected final Color					background;
	protected int									mouseX;
	protected int									mouseY;

	protected boolean							performMouseOp;
	protected ImageController			ctrl;

	protected int									width;
	private int										height;

	public Overlay(JToolBar toolbar, ImageController ctrl) {
		background = new Color(0, 0, 0, 100);

		this.toolbar = toolbar;
		this.ctrl = ctrl;
	}

	protected final JToolBar getToolBar() {
		return toolbar;
	}

	public void changeControllers(ImageController ctrl) {
		this.ctrl = ctrl;
	}

	public void render(Graphics2D init, int width, int height) {
		// save off the width and height for other functions
		this.width = width;
		this.height = height;

		Graphics2D g = (Graphics2D) init.create();

		g.dispose();
	}

	protected void drawPlusButton(Graphics2D g, Rectangle2D.Double rect) {
		// also draw a box for adding new colors
		boolean highlight = rect.contains(new Point2D.Double(mouseX, mouseY));
		g.setColor(Color.lightGray);
		Graphics2D tmpG = (Graphics2D) g.create();
		String plus = "\uf0fe";
		tmpG.setFont(PixelPainter.getFontAwesome());
		if (highlight) {
			tmpG.setColor(Color.LIGHT_GRAY.brighter());
			;
		}
		float strWidth = tmpG.getFontMetrics().stringWidth(plus);
		float strHeight = tmpG.getFontMetrics().getHeight();
		tmpG.drawString(plus, (float) (rect.getX() + strWidth / 2),
				(float) (rect.getMaxY() - strHeight / 2));
		tmpG.dispose();
	}

	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
	}

	public void mouseReleased(MouseEvent e) {
		performMouseOp = true;
	}

	public void mousePressed(MouseEvent e) {

	}

	public void keyPressed(KeyEvent e) {

	}

	public void keyReleased(KeyEvent e) {

	}

	public void keyTyped(KeyEvent e) {

	}

	public void mouseWheel(MouseWheelEvent mwe) {

	}

	public boolean isInside(Point point) {

		return false;
	}
}
