package com.pixel.painter.brushes.undoables;

import java.awt.Rectangle;
import java.awt.image.WritableRaster;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import com.pixel.painter.brushes.Brush;
import com.pixel.painter.controller.ImageController;

public class BrushSnapshotUndoable extends AbstractUndoableEdit {
  private ImageController ctrl;
  private final int[]     oldData;
  private int             x;
  private int             y;
  private Brush           brush;
  private Rectangle       affectedArea;

  public BrushSnapshotUndoable(ImageController ctrl, Rectangle affectedArea, int x, int y, Brush brush) {
    this.ctrl  = ctrl;
    this.x     = x;
    this.y     = y;
    this.brush = brush;
    oldData    = ctrl.samplePixels(affectedArea); // this could become too expensive
  }

  @Override
  public boolean canRedo() {
    return true;
  }

  @Override
  public boolean canUndo() {
    return true;
  }

  @Override
  public void redo() throws CannotRedoException {
    brush.apply(ctrl, x, y);
  }

  @Override
  public void undo() throws CannotUndoException {
    WritableRaster raster = ctrl.getImage().getRaster();
    // reset raster for the affected area.
    raster.setPixels(affectedArea.x, affectedArea.y, affectedArea.width, affectedArea.height, oldData);
  }

}