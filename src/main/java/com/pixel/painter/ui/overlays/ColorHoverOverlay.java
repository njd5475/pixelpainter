package com.pixel.painter.ui.overlays;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.Set;

import javax.swing.JToolBar;

import com.pixel.painter.controller.ImageController;
import com.pixel.painter.ui.PixelPainter;

public class ColorHoverOverlay extends Overlay {

	private PixelPainter	viewer;
	private int				red;
	private int				blue;
	private int				green;
	private int				alpha;
	private boolean			displayPoints;
	private Set<Point>		allColors;
	private boolean			redSelected		= false;
	private boolean			blueSelected	= false;
	private boolean			greenSelected	= false;
	private boolean			alphaSelected	= false;

	public ColorHoverOverlay(JToolBar toolbar, PixelPainter painter,
			ImageController ctrl) {
		super(toolbar, ctrl);
		this.viewer = painter;
	}

	@Override
	public void render(Graphics2D init, int width, int height) {
		super.render(init, width, height);
		Graphics2D g = (Graphics2D) init.create();
		g.setColor(Color.white);
		g.setColor(redSelected ? Color.yellow : Color.white);
		g.drawString(String.format("Red %d", red), 10, 15);
		g.setColor(blueSelected ? Color.yellow : Color.white);
		g.drawString(String.format("Blue %d", blue), 10, 30);
		g.setColor(greenSelected ? Color.yellow : Color.white);
		g.drawString(String.format("Green %d", green), 10, 45);
		g.setColor(alphaSelected ? Color.yellow : Color.white);
		g.drawString(String.format("Alpha %d", alpha), 10, 60);
		if (allColors != null && displayPoints) {
			Graphics2D inImage = (Graphics2D) g.create();
			// viewer.translateToImage(inImage);
			viewer.setImageTransform(inImage);
			inImage.setColor(new Color(255, 0, 255, 50));
			for (Point p : allColors) {
				inImage.fillRect(p.x, p.y, 1, 1);
			}
			inImage.dispose();
		}
		g.dispose();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		super.keyPressed(e);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_R) {
			redSelected = !redSelected;
		}

		if (e.getKeyCode() == KeyEvent.VK_G) {
			greenSelected = !greenSelected;
		}

		if (e.getKeyCode() == KeyEvent.VK_B) {
			blueSelected = !blueSelected;
		}

		if (e.getKeyCode() == KeyEvent.VK_A) {
			alphaSelected = !alphaSelected;
		}

		viewer.repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		super.mouseMoved(e);

		// image point
		Point pt = viewer.getPointInImage(e.getX(), e.getY());
		if (pt != null && pt.x > 0 && pt.y > 0 && pt.x < ctrl.getSize().width
				&& pt.y < ctrl.getSize().height) {
			int color = ctrl.getImage().getRGB(pt.x, pt.y);
			final int mask = 0x000000FF;
			blue = color & mask;
			green = (color >> 8) & mask;
			red = (color >> 16) & mask;
			alpha = (color >> 24) & mask;
			allColors = ctrl.getAll(red, green, blue, alpha);
		} else {
			allColors = null;
		}

		displayPoints = e.isControlDown();
	}

	@Override
	public void mouseWheel(MouseWheelEvent mwe) {
		Point pt = viewer.getPointInImage(mwe.getX(), mwe.getY());
		if (pt != null && pt.x > 0 && pt.y > 0 && pt.x < ctrl.getSize().width
				&& pt.y < ctrl.getSize().height && mwe.isControlDown()) {
			int color = ctrl.getImage().getRGB(pt.x, pt.y);
			final int mask = 0x000000FF;
			blue = color & mask;
			green = (color >> 8) & mask;
			red = (color >> 16) & mask;
			alpha = (color >> 24) & mask;
			int wheelRotation = mwe.getWheelRotation();
			if (redSelected) {
				red += wheelRotation;
			}
			if (blueSelected) {
				blue += wheelRotation;
			}
			if (greenSelected) {
				green += wheelRotation;
			}
			if (alphaSelected) {
				alpha += wheelRotation;
			}

			if (!mwe.isShiftDown()) {
				ctrl.setColorAt(pt.x, pt.y, new Color(red, green, blue, alpha));
			} else {
				ctrl.setAllColorsAt(pt.x, pt.y, new Color(red, green, blue,
						alpha));
			}
			mwe.consume();
		}

	}

}
