package com.pixel.painter.ui.overlays;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JToolBar;

import com.pixel.painter.controller.ImageController;
import com.pixel.painter.controller.LayeredController;
import com.pixel.painter.controller.SingleImageController;
import com.pixel.painter.ui.PixelPainter;

public class LayerOverlay extends Overlay {

  private static final int  LAYER_LABEL_SEPARATION = 3;
  private PixelPainter      viewer;
  private int               myHeight;
  private int               myWidth;
  private LayeredController layerCtrl = new LayeredController();
  private Font              fontAwesome;
  private Font              font;
  private FontRenderContext fontRenderContext;

  public LayerOverlay(JToolBar toolbar, PixelPainter viewer, ImageController ctrl) {
    super(toolbar, ctrl);
    this.layerCtrl.addLayer(ctrl);
    this.viewer            = viewer;
    this.myHeight          = 20;
    this.myWidth           = 0;
    this.fontAwesome       = viewer.getFontAwesome();
    this.font              = viewer.getFont();
    this.fontRenderContext = viewer.getFontMetrics(viewer.getFont()).getFontRenderContext();
  }

  public void render(Graphics2D g, int width, int height) {
    this.screenWidth  = width;
    this.screenHeight = height;
    g.setColor(Color.WHITE);

    int                            curX        = getX();
    Map<Integer, Rectangle.Double> layerBounds = getLayerButtonBounds();
    boolean                        highlight   = false;
    for (Integer layer = 1; layer <= layerCtrl.getLayerCount(); ++layer) {
      highlight = layerBounds.get(layer).contains(new Point(mouseX, mouseY));
      if(layerCtrl.getCurrentLayer() == layer) {
        g.setColor(Color.blue);
        g.fillRect(curX, getY(), g.getFontMetrics().stringWidth(layer.toString()) + LAYER_LABEL_SEPARATION, g.getFontMetrics().getHeight());
      }
      g.setColor(Color.white);
      if(layerCtrl.isVisible(layer)) {
        g.setColor(Color.yellow);
      }
      if(highlight) {
        g.setColor(Color.pink);
      }
      g.drawString(layer.toString(), curX, getY() + g.getFontMetrics().getHeight());
      curX += g.getFontMetrics().stringWidth(layer.toString()) + LAYER_LABEL_SEPARATION;
    }

    Double plusRect = getPlusBounds();
    this.drawPlusButton(g, plusRect);
  }

  public Double getBounds() {
    int width = 0;
    for (Integer layer = 1; layer <= layerCtrl.getLayerCount(); ++layer) {
      width += this.font.getStringBounds(layer.toString(), fontRenderContext).getWidth() + LAYER_LABEL_SEPARATION;
    }
    return new Rectangle2D.Double(getX(), getY(), getStringBounds().getWidth() + width, this.myHeight);
  }

  public Double getPlusBounds() {
    int width = 0;
    for (Integer layer = 1; layer <= layerCtrl.getLayerCount(); ++layer) {
      width += this.font.getStringBounds(layer.toString(), fontRenderContext).getWidth() + LAYER_LABEL_SEPARATION;
    }
    return new Rectangle2D.Double(getX() + width, getY(), getStringBounds().getWidth(), this.myHeight);
  }

  public Rectangle2D getStringBounds() {
    Rectangle2D stringBounds = fontAwesome.getStringBounds("\uf0fe", this.fontRenderContext);
    return stringBounds;
  }

  public int getX() {
    Point p    = viewer.getPointInViewer(0, 0);
    int   curX = p.x;
    return curX;
  }

  public int getY() {
    Point p    = viewer.getPointInViewer(0, 0);
    int   curY = p.y - this.myHeight;
    return curY;
  }

  public void mouseReleased(MouseEvent e) {
    super.mouseReleased(e);
    if(getPlusBounds().contains(e.getX(), e.getY())) {
      layerCtrl.addLayer(new SingleImageController(
          new BufferedImage(ctrl.getImage().getWidth(), ctrl.getImage().getHeight(), BufferedImage.TYPE_INT_ARGB),
          true));
      if(viewer.getImageController() != layerCtrl) {
        viewer.changeImageController(layerCtrl, viewer.getCurrentFile());
      }
      e.consume();
    } else {
      Set<Map.Entry<Integer, Rectangle.Double>> entries = getLayerButtonBounds().entrySet();
      for (Map.Entry<Integer, Rectangle.Double> layerButton : entries) {
        if(layerButton.getValue().contains(e.getPoint())) {
          System.out.println("Changing to layer " + layerButton.getKey());
          
          ImageController ctrl = viewer.getImageController();
          if(ctrl != layerCtrl) {
            layerCtrl.addLayer(ctrl);
            viewer.changeImageController(layerCtrl, viewer.getCurrentFile());
          }
          if(ctrl instanceof LayeredController) {
            LayeredController lctrl = (LayeredController) ctrl;
            lctrl.changeLayer(layerButton.getKey());
          }
          e.consume();
        }
      }
    }
  }
  
  @Override
  public void mousePressed(MouseEvent e) {
    super.mousePressed(e);
    Set<Map.Entry<Integer, Rectangle.Double>> entries = getLayerButtonBounds().entrySet();
    for (Map.Entry<Integer, Rectangle.Double> layerButton : entries) {
      if(layerButton.getValue().contains(e.getPoint())) {
        e.consume();
      }
    }
  }

  public Map<Integer, Rectangle.Double> getLayerButtonBounds() {
    Map<Integer, Rectangle.Double> bounds        = new HashMap<>();
    Double                         overlayBounds = getBounds();
    int                            curX          = 0;
    for (Integer layer = 1; layer <= layerCtrl.getLayerCount(); ++layer) {
      Rectangle2D strBounds = this.font.getStringBounds(layer.toString(), fontRenderContext);
      bounds.put(layer, new Rectangle.Double(overlayBounds.getX() + curX, overlayBounds.getY(), strBounds.getWidth(),
          strBounds.getHeight()));
      curX += strBounds.getWidth() + LAYER_LABEL_SEPARATION;
    }
    return bounds;
  }
}
