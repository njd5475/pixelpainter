package com.pixel.painter.palettes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import com.pixel.painter.model.ColorPalette;

public class ColorPaletteRenderer {

	public static void render(Graphics2D initial, Rectangle bounds, ColorPalette p, ColorPalette[] rest) {
		Graphics2D g = (Graphics2D) initial.create();
		g.setClip(bounds);
		g.translate(bounds.getX(), bounds.getY());

		g.setColor(Color.black);
		g.fillRect(0, 0, (int) bounds.getWidth(), (int) bounds.getHeight());

		g.setColor(Color.white);
		g.drawString(p.getName(), 0, g.getFontMetrics().getHeight());
		FontMetrics fm = g.getFontMetrics();
		int strW = fm.stringWidth(p.getName());
		if (rest != null) {
			for (ColorPalette cp : rest) {
				strW = Math.max(strW, fm.stringWidth(cp.getName()));
			}
		}

		g.translate(strW, 0);
		Color[] colors = p.getColors();
		double cW = (bounds.getWidth() - strW) / colors.length;
		double cH = bounds.getHeight();

		int x = 0, y = 0;
		for (Color c : colors) {
			g.setColor(c);
			g.fillRect(x, y, (int) cW, (int) cH);
			x += cW;
		}

		g.dispose();
	}

}
