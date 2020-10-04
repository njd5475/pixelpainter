package com.pixel.painter.brushes;

import java.awt.Color;
import java.awt.Graphics2D;

import javax.swing.Action;

import com.pixel.painter.brushes.undoables.BrushUndoable;
import com.pixel.painter.brushes.undoables.SinglePixelEdit;
import com.pixel.painter.controller.ImageController;
import com.pixel.painter.ui.PixelPainter;

public class EraseBrush extends Brush {

  private String eraseUnicode;

  public EraseBrush() {
    super("EraseBrush");
    this.eraseUnicode = "\uf12d";
  }

  @Override
  public Action createAsAction(PixelPainter pp) {
    return new BrushAction(eraseUnicode, getIcon(), pp, this);
  }

  @Override
  public BrushUndoable apply(ImageController ctrl, int x, int y) {
    Graphics2D g        = ctrl.getImage().createGraphics();
    Color      oldColor = ctrl.sample(x, y);
    ctrl.clearColor(x, y);
    g.dispose();
    return new SinglePixelEdit(ctrl, oldColor, new Color(0, 0, 0, 0), x, y);
  }

}
