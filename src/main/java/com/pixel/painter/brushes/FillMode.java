package com.pixel.painter.brushes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import javax.swing.Action;

import com.pixel.painter.brushes.undoables.BrushUndoable;
import com.pixel.painter.brushes.undoables.FillModeBrushUndoable;
import com.pixel.painter.controller.ImageController;
import com.pixel.painter.ui.PixelPainter;

public class FillMode extends Brush {

  private String unicodeIcon;

  public FillMode() {
    super("FillMode");
    this.unicodeIcon = "\uf576";
  }

  @Override
  public Action createAsAction(PixelPainter pp) {
    return new BrushAction(unicodeIcon, getIcon(), pp, this);
  }

  @Override
  public BrushUndoable apply(ImageController ctrl, int x, int y) {
    Graphics2D        g = ctrl.getImage().createGraphics();
    LinkedList<Point> q = new LinkedList<Point>();

    Color color = ctrl.getFillColor();

    Dimension size = ctrl.getSize();
    Point     w, e, n;
    Color     c;
    // all similar colors we need to change
    Color      target        = ctrl.sample(x, y);
    Set<Point> alreadyFilled = new HashSet<Point>();

    q.add(new Point(x, y));
    g.setColor(color);
    while (!q.isEmpty()) {
      n = q.removeFirst();

      c = ctrl.sample(n.x, n.y);

      // determine if we need to change the pixel
      if(c.equals(target)) {
        w = new Point(n);
        e = new Point(n);
        while (w.x >= 0 && ctrl.sample(w.x, w.y).equals(target)) {
          --w.x;
        }
        while (e.x < size.width && ctrl.sample(e.x, e.y).equals(target)) {
          ++e.x;
        }
        for (int i = w.x + 1; i < e.x; ++i) {
          if(n.y + 1 < size.height && ctrl.sample(i, n.y + 1).equals(target)) {
            // Adding point i, n.y + 1
            q.add(new Point(i, n.y + 1));
          }

          if(n.y - 1 >= 0 && ctrl.sample(i, n.y - 1).equals(target)) {
            // Adding point i, n.y - 1
            q.add(new Point(i, n.y - 1));
          }

          // Setting Color for i, n.y, color
          if(!alreadyFilled.contains(new Point(i, n.y))) {
            ctrl.setColorAt(i, n.y, color);
            Point filledPoint = new Point(i, n.y);
            alreadyFilled.add(filledPoint);
          } else {
            // Refilling position
            return null;
          }
        }
      } else {
        // Pixels aren't getting changed!
      }
    }
    return buildUndoable(ctrl, target, color, x, y, alreadyFilled);
  }

  private BrushUndoable buildUndoable(ImageController ctrl, Color starting, Color ending, int x, int y,
      Set<Point> changed) {
    return new FillModeBrushUndoable(ctrl, starting, ending, x, y, changed);
  }

}
