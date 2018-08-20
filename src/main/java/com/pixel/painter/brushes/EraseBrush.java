package com.pixel.painter.brushes;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.Icon;

import com.pixel.painter.controller.ImageController;

public class EraseBrush extends Brush {

	public EraseBrush(ImageController ctrl) {
		super(ctrl);
	}

	public EraseBrush(ImageController ctrl, String name, Icon icon) {
		super(ctrl, name, icon);
	}

	@Override
	public void apply(Graphics2D g, int x, int y) {
		this.getController().clearColor(x, y);
	}

	@Override
	public Rectangle getAffectedArea(int x, int y) {
		return new Rectangle(x, y, 1, 1);
	}

}
