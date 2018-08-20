package com.pixel.painter.brushes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import javax.swing.Icon;

import com.pixel.painter.controller.ImageController;

public class FillMode extends Brush {

	private Color	color;

	public FillMode(ImageController ctrl, Color color) {
		super(ctrl);
		this.color = color;
	}

	public FillMode(ImageController ctrl, String name) {
		super(ctrl, name);
	}

	public FillMode(ImageController ctrl, String name, Icon icon, Color color) {
		super(ctrl, name, icon);
		this.color = color;
	}

	@Override
	public void apply(Graphics2D g, int x, int y) {
		LinkedList<Point> q = new LinkedList<Point>();

		color = getController().getFillColor();

		Dimension size = getController().getSize();
		q.add(new Point(x, y));
		Point w, e, n;
		Color c;
		Color target = getController().sample(x, y);
		g.setColor(color);
		Set<Point> alreadyFilled = new HashSet<Point>();
		while (!q.isEmpty()) {
			n = q.removeFirst();

			c = getController().sample(n.x, n.y);
			if (c.equals(target)) {
				w = new Point(n);
				e = new Point(n);
				while (w.x >= 0
						&& getController().sample(w.x, w.y).equals(target)) {
					--w.x;
				}
				while (e.x < size.width
						&& getController().sample(e.x, e.y).equals(target)) {
					++e.x;
				}
				for (int i = w.x + 1; i < e.x; ++i) {
					if (n.y + 1 < size.height
							&& getController().sample(i, n.y + 1)
									.equals(target)) {
						// System.out.println("Adding point " + i + ", "
						// + (n.y + 1));
						q.add(new Point(i, n.y + 1));
					}

					if (n.y - 1 >= 0
							&& getController().sample(i, n.y - 1)
									.equals(target)) {
						// System.out.println("Adding point " + i + ", "
						// + (n.y - 1));
						q.add(new Point(i, n.y - 1));
					}
					// System.out.format("Setting Color for %d, %d to %s\n", i,
					// n.y, color);
					if (!alreadyFilled.contains(new Point(i, n.y))) {
						getController().setColorAt(i, n.y, color);
						alreadyFilled.add(new Point(i, n.y));
					} else {
						// System.err.println("Refilling position");
						return;
					}
				}
			} else {
				// System.out.println("Pixels aren't getting changed!");
			}
		}
	}

	@Override
	public Rectangle getAffectedArea(int x, int y) {
		return null;
	}

}
