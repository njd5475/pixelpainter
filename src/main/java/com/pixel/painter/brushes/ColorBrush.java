package com.pixel.painter.brushes;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;

import com.pixel.painter.controller.ImageController;

public class ColorBrush extends Brush {

	private static Map<Color, ColorBrush>	brushes	= new HashMap<Color, ColorBrush>();
	private final Color										color;
	private Color													oldColor;

	public ColorBrush(ImageController ctrl, Color color) {
		super(ctrl, null, getColorBrushIcon(color));
		this.color = color;
	}

	public static ColorBrush createColorBrush(ImageController ctrl, Color color) {
		ColorBrush brush = brushes.get(color);
		if (brush == null) {
			brush = new ColorBrush(ctrl, color);
			brushes.put(color, brush);
		}
		return brush;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);
		getController().setFillColor(color);
	}

	private static Icon getColorBrushIcon(final Color color2) {
		Icon ico = new Icon() {

			@Override
			public int getIconHeight() {
				return 32;
			}

			@Override
			public int getIconWidth() {
				return 32;
			}

			@Override
			public void paintIcon(Component c, Graphics init, int x, int y) {
				Graphics2D g = (Graphics2D) init.create();
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				g.setColor(color2);
				g.fillRoundRect(x, y, getIconWidth() - 1, getIconHeight() - 1, 8, 8);
				g.dispose();
			}

		};
		return ico;
	}

	@Override
	public void apply(Graphics2D g, int x, int y) {
		oldColor = this.getController().sample(x, y);
		g.setColor(color);
		this.getController().setColorAt(x, y, color);
		// g.drawOval(x - 1, y - 1, 1, 1);
	}

	@Override
	public Rectangle getAffectedArea(int x, int y) {
		return new Rectangle(x, y, 1, 1);
	}

	public Color getColor() {
		return color;
	}

}
