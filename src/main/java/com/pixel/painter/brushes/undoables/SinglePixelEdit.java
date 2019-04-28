package com.pixel.painter.brushes.undoables;

import java.awt.Color;

import com.pixel.painter.controller.ImageController;

public class SinglePixelEdit extends BrushUndoable {

  private Color                     oldColor;
  private Color                     color;
  private int                       x;
  private int                       y;

  /**
   * @param ctrl
   * @param oldColor Old color being replaced
   * @param color    New color to put in the old colors position
   * @param x        Position of the pixel on the x-axis
   * @param y        Position of the pixel on the y-axis
   */
  public SinglePixelEdit(ImageController ctrl, Color oldColor, Color color, int x, int y) {
    super(ctrl);
    this.oldColor = oldColor;
    this.color    = color;
    this.x        = x;
    this.y        = y;
  }

  @Override
  public void redo() {
    getController().setColorAt(x, y, color);
  }

  @Override
  public void undo() {
    getController().setColorAt(x, y, oldColor);
  }

}
