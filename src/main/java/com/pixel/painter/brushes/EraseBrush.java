package com.pixel.painter.brushes;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.Action;
import javax.swing.undo.UndoManager;

import com.pixel.painter.controller.ImageController;

public class EraseBrush extends Brush {

  private String eraseUnicode;

	public EraseBrush() {
		super("EraseBrush");
		this.eraseUnicode = "\uf12d";
	}
	
	public Action createAsAction(ImageController ctrl) {
	  return new BrushAction(eraseUnicode, getIcon(), ctrl, this);
	}

	@Override
	public void apply(ImageController ctrl, int x, int y, UndoManager undolog) {
	  Graphics2D g = ctrl.getImage().createGraphics();
		ctrl.clearColor(x, y);
		g.dispose();
	}

	@Override
	public Rectangle getAffectedArea(int x, int y) {
		return new Rectangle(x, y, 1, 1);
	}

}
