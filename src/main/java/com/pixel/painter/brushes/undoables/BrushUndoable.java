package com.pixel.painter.brushes.undoables;

import javax.swing.undo.AbstractUndoableEdit;

import com.pixel.painter.controller.ImageController;

public abstract class BrushUndoable extends AbstractUndoableEdit {

  private transient ImageController ctrl;

  public BrushUndoable(ImageController ctrl) {
    this.ctrl = ctrl;
  }

  public void setController(ImageController ctrl) {
    if(ctrl == null) {
      throw new NullPointerException("Cannot set a brush to a null image controller");
    }
    this.ctrl = ctrl;
  }

  public ImageController getController() {
    return this.ctrl;
  }

  @Override
  public abstract void redo();

  @Override
  public abstract void undo();

}
