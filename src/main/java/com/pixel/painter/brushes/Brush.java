package com.pixel.painter.brushes;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.image.WritableRaster;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

import com.pixel.painter.controller.ImageController;

public abstract class Brush {

  private String name;
  private Icon   icon;

  public Brush(String name) {
    this.name         = name;
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

  public abstract void apply(ImageController ctrl, int x, int y, UndoManager manager);

  public Action createAsAction(ImageController ctrl) {
    return new BrushAction(name, icon, ctrl, this);
  }

  public abstract Rectangle getAffectedArea(int x, int y);

  protected UndoableEdit createUndoableEdit(ImageController ctrl, Rectangle affectedArea, int x, int y) {
    final int[] oldData = ctrl.samplePixels(affectedArea); // this could become too expensive
    return new AbstractUndoableEdit() {

      /**
       * 
       */
      private static final long serialVersionUID = 1L;

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
        Brush.this.apply(ctrl, x, y, null);
      }

      @Override
      public void undo() throws CannotUndoException {
        WritableRaster raster = ctrl.getImage().getRaster();
        // reset raster for the affected area.
        raster.setPixels(affectedArea.x, affectedArea.y, affectedArea.width, affectedArea.height, oldData);
      }

    };

  }
  
  public static class BrushAction extends AbstractAction {
    private Brush brush;
    private ImageController ctrl;

    public BrushAction(String name, Icon icon, ImageController ctrl, Brush brush) {
      super(name, icon);
      this.ctrl = ctrl;
      this.brush = brush;
    }
    
    public Brush getBrush() {
      return brush;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
      JComponent comp = (JComponent) e.getSource();
      comp.setBorder(BorderFactory.createEmptyBorder(7, 7, 7, 7));
      ctrl.setBrush(brush); // set this brush
    }
  }
}
