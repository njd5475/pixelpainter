package com.pixel.painter.brushes;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.undo.UndoableEdit;

import com.pixel.painter.brushes.undoables.BrushSnapshotUndoable;
import com.pixel.painter.brushes.undoables.BrushUndoable;
import com.pixel.painter.controller.ImageController;
import com.pixel.painter.ui.PixelPainter;

public abstract class Brush {

  private String name;
  private Icon icon;

  public Brush(String name) {
    this.name = name;
  }

  public Brush(String name, Icon icon) {
    this.name = name;
    this.icon = icon;
  }

  public String getName() {
    return name;
  }

  public Icon getIcon() {
    return icon;
  }

  public abstract BrushUndoable apply(ImageController ctrl, int x, int y);

  public Action createAsAction(PixelPainter pp) {
    return new BrushAction(name, icon, pp, this);
  }

  protected UndoableEdit createUndoableEdit(ImageController ctrl, Rectangle affectedArea, int x, int y) {
    return new BrushSnapshotUndoable(ctrl, affectedArea, x, y, this);
  }

  public static class BrushAction extends AbstractAction {
    private Brush brush;
    private PixelPainter pp;

    public BrushAction(String name, Icon icon, PixelPainter pp, Brush brush) {
      super(name, icon);
      this.pp = pp;
      this.brush = brush;
    }

    public Brush getBrush() {
      return brush;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      JComponent comp = (JComponent) e.getSource();
      comp.setBorder(BorderFactory.createEmptyBorder(7, 7, 7, 7));
      pp.getImageController().setBrush(brush); // set this brush
    }
  }
}
