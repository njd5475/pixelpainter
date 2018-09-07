package com.pixel.painter.ui.overlays;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import javax.swing.JToolBar;

import com.pixel.painter.controller.ImageController;
import com.pixel.painter.ui.PixelPainter;

public class LayerOverlay extends Overlay {

  private PixelPainter                  viewer;
  private int                           myHeight;
  private int                           myWidth;
  private Map<Integer, ImageController> layers = new HashMap<>();
  private Font                          fontAwesome;

  public LayerOverlay(JToolBar toolbar, PixelPainter viewer, ImageController ctrl) {
    super(toolbar, ctrl);
    this.viewer = viewer;
    this.myHeight = 20;
    this.myWidth = 0;
    fontAwesome = viewer.getFontAwesome();
  }

  public void render(Graphics2D g, int width, int height) {
    g.setColor(Color.WHITE);
    Point p = viewer.getPointInViewer(0, 0);
    int curX = p.x;
    int curY = p.y-this.myHeight;
    for(Integer layer : new TreeSet<>(layers.keySet())) {
      g.drawString(layer.toString(), curX, curY+g.getFontMetrics().getHeight());
      curX += g.getFontMetrics().stringWidth(layer.toString());
    }
    
    Rectangle2D stringBounds = fontAwesome.getStringBounds("\uf0fe", g.getFontRenderContext());
    Double plusRect = new Rectangle2D.Double(curX, p.y-this.myHeight, stringBounds.getWidth(), this.myHeight);
    this.drawPlusButton(g, plusRect);
    boolean highlight = plusRect.contains(new Point2D.Double(mouseX, mouseY));
    
    if(highlight && performMouseOp) {
      layers.put(layers.size()+1, new ImageController(new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB), true));
      performMouseOp = false;
    }else {
      performMouseOp = false;
    }
  }
}
