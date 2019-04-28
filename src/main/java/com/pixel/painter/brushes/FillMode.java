package com.pixel.painter.brushes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import javax.swing.Action;
import javax.swing.undo.UndoManager;

import com.pixel.painter.controller.ImageController;

public class FillMode extends Brush {

  private String    unicodeIcon;

  public FillMode() {
    super("FillMode");
    this.unicodeIcon = "\uf576";
  }
  
  public Action createAsAction(ImageController ctrl) {
    return new BrushAction(unicodeIcon, getIcon(), ctrl, this);
  }

  @Override
  public void apply(ImageController ctrl, int x, int y, UndoManager undolog) {
    Graphics2D        g = ctrl.getImage().createGraphics();
    LinkedList<Point> q = new LinkedList<Point>();

    Color color = ctrl.getFillColor();

    Dimension size = ctrl.getSize();
    Point     w, e, n;
    Color     c;
    // all similar colors we need to change
    Color      target        = ctrl.sample(x, y);
    Set<Point> alreadyFilled = new HashSet<Point>();

    Rectangle affectedArea = new Rectangle(x, y, 1, 1);
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
            affectedArea.add(filledPoint);
          } else {
            // Refilling position
            return;
          }
        }
      } else {
        // Pixels aren't getting changed!
      }
    }
  }

  @Override
  public Rectangle getAffectedArea(int x, int y) {
    // TODO Auto-generated method stub
    return null;
  }

}
