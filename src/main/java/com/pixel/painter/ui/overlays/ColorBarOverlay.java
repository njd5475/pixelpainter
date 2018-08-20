package com.pixel.painter.ui.overlays;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JToolBar;

import com.pixel.painter.brushes.Brush;
import com.pixel.painter.controller.ImageController;
import com.pixel.painter.model.ColorPalette;
import com.pixel.painter.ui.PixelPainter;

public class ColorBarOverlay extends Overlay {

	private Color							highlightedColor;
	private Color							selected;
	private final Set<Color>	colors;

	public ColorBarOverlay(JToolBar toolbar, ImageController ctrl, ColorPalette palette) {
		super(toolbar, ctrl);
		colors = new LinkedHashSet<>(Arrays.asList(palette.getColors()));

	}

	public void addSelectedBrush() {
		Brush brush = ctrl.createColorBrush(selected);
		JToolBar toolbar = getToolBar();
		JButton but = toolbar.add(brush);
		but.setPreferredSize(PixelPainter.toolButtonSize);
		toolbar.invalidate();
		toolbar.repaint();
		ctrl.setBrush(brush);
		ctrl.setFillColor(selected);
	}

	@Override
	public void render(Graphics2D init, int width, int height) {
		drawColorBar(init, width, height);
	}

	private void drawColorBar(Graphics2D init, int width, int height) {
		Graphics2D g = (Graphics2D) init.create();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		int stX = 40;
		int bW = 25;
		int offset = 5;
		int y = 55;
		int slideBorder = 4;
		int numVariants = 10;
		int slideWidth = numVariants * 31;

		g.setColor(background);
		g.fillRoundRect(width - stX, y - 5, bW + 2 * offset, height - 60, 10, 10);

		stX -= offset;
		for (Color c : colors) {
			boolean highlight = (new Rectangle(width - stX, y, bW, bW))
					.contains(new Point(mouseX, mouseY));

			if (highlightedColor != null && !highlight
					&& highlightedColor.hashCode() == c.hashCode()) {
				highlight = (new Rectangle(width - slideWidth, y - slideBorder / 2,
						slideWidth - stX + bW + slideBorder / 2, bW + slideBorder))
								.contains(new Point(mouseX, mouseY));
				if (!highlight) {
					highlightedColor = null;
				}
			}

			// draw horizontal selection
			if (highlight) {
				// g.setColor(new Color(255,0,255));
				// g.setColor(Color.gray);
				g.setColor(SLIDEMENU_BACKGROUND);
				g.fillRoundRect(width - slideWidth, y - slideBorder / 2,
						slideWidth - stX + bW + slideBorder / 2, bW + slideBorder, 10, 10);
				drawColorVariants(g, width - slideWidth, y, c, numVariants);
				highlightedColor = c;
			}

			g.setColor((highlightedColor == c && selected != null) ? selected : c);
			g.fillRoundRect(width - stX, y, bW, bW, 8, 8);
			if (highlight) {
				g.setColor(Color.yellow);
			} else {
				g.setColor(Color.white);
			}
			g.drawRoundRect(width - stX, y, bW, bW, 8, 8);

			y += 35;
		}

		// drawPlusButton(g, new Rectangle2D.Double(width - stX, y, bW, bW));
		// y += bW;
		drawTrashButton(g, new Rectangle2D.Double(width - stX, y, bW, bW));

		g.dispose();
	}

	private void drawTrashButton(Graphics2D g, Double double1) {
		g.setFont(PixelPainter.getFontAwesome());
		String trash = "\uf2ed";
		
		float strWidth = g.getFontMetrics().stringWidth(trash);
		g.setColor(Color.white);
		g.drawString(trash, (float) (double1.getX()+double1.getWidth()/2-strWidth/2),
				(float) (double1.getMaxY()));
	}

	private void drawColorVariants(Graphics2D graphics, int x, int y, Color c,
			int numVariants) {
		// int r = c.getRed();
		// int g = c.getGreen();
		// int b = c.getBlue();
		// int a = c.getAlpha();
		float[] hsb = new float[3];
		Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsb);
		float hue = hsb[0];
		float sat = hsb[1];
		float bri = hsb[2];
		int sep = 2;
		x += 3;
		Graphics2D g2d = (Graphics2D) graphics.create();
		int max = numVariants;
		float percent = 1.0f;
		Color color = null;
		Color validColor = null;
		for (int i = 0; i < max; ++i) {
			// percent = 1.0 - ((i+1)/(double)(max));
			// if(percent < 0.15) {
			// percent = 0.15;
			// }
			percent -= 0.10f;
			float p = 1.0f - percent;
			// color = new Color((int) (r * p), (int) (g * p), (int) (b * p),
			// 255);

			color = Color.getHSBColor(hue, sat, bri * p);
			g2d.setColor(color);
			g2d.fillRect(x, y, 25, 25);

			// highlight variant
			if ((new Rectangle(x, y, 25, 25)).contains(new Point(mouseX, mouseY))) {
				g2d.setColor(Color.white);
				g2d.drawRect(x, y, 25, 25);

				drawRGBHint(g2d, x, y, color);
				validColor = color;
			}

			x += 25 + sep;
		}

		selected = validColor;
		if (selected == null) {
			performMouseOp = false;
		} else {
			// perform mouse op
			if (performMouseOp) {
				addSelectedBrush();
				performMouseOp = false;
			}
		}

		g2d.dispose();
	}

	private void drawRGBHint(Graphics2D g2d, int x, int y, Color color) {
		// determine if we are at the edge and if so calculate the start x,
		// such that the hint does not go beyond the edge of the window.
		String rgb = String.format("R(%d) G(%d) B(%d)", color.getRed(),
				color.getGreen(), color.getBlue());
		int strW = g2d.getFontMetrics().stringWidth(rgb);
		x = Math.min(x, width - strW);

		g2d.setColor(SLIDEMENU_BACKGROUND);
		g2d.fillRect(x, y - 20, strW, 20);
		g2d.setColor(Color.white);
		g2d.drawString(rgb, x, y - 5);
	}
}
