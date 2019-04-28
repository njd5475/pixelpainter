package com.pixel.painter.brushes.undoables;

import java.awt.Color;
import java.awt.Point;
import java.util.Set;

import com.pixel.painter.controller.ImageController;

public class FillModeBrushUndoable extends BrushUndoable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private Set<Point>                changed;
  private int                       y;
  private int                       x;
  private Color                     ending;
  private Color                     starting;

  public FillModeBrushUndoable(ImageController ctrl, Color starting, Color ending, int x, int y, Set<Point> changed) {
    super(ctrl);
    this.starting = starting;
    this.ending   = ending;
    this.x        = x;
    this.y        = y;
    this.changed  = changed;
  }

  @Override
  public void redo() {
    ImageController ctrl = getController();
    for (Point pt : changed) {
      ctrl.setColorAt(pt.x, pt.y, ending);
    }
  }

  @Override
  public void undo() {
    ImageController ctrl = getController();
    for(Point pt : changed) {
      ctrl.setColorAt(pt.x, pt.y, starting);
    }
  }
}