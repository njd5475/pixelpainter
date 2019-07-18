package com.pixel.painter.ui.overlays;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.JToolBar;

import com.pixel.painter.controller.ImageController;

public class PreviewOverlay extends Overlay {

  private Image previewImage;
  private int   scale = 1;

  public PreviewOverlay(JToolBar toolbar, ImageController ctrl) {
    super(toolbar, ctrl);
  }

  @Override
  public void render(Graphics2D init, int width, int height) {
    super.render(init, width, height);
    Graphics2D g = (Graphics2D) init.create();

    Dimension imgSize = ctrl.getSize();
    g.setStroke(new BasicStroke(1));
    g.translate(getX(), getY());
    Graphics2D g2 = (Graphics2D) g.create();
    g2.scale(scale, scale);
    ctrl.render(g2, Math.min(64, imgSize.width), Math.min(64, imgSize.height));
    g2.dispose();
    g.setColor(Color.yellow.darker());
    g.drawRect(-1, -1, getWidth(), getHeight());
    g.dispose();
  }

  public int getX() {
    return getScreenWidth() - getWidth() - 1;
  }

  public int getY() {
    return 2;
  }

  public int getWidth() {
    return scale * Math.min(64, ctrl.getSize().width);
  }

  public int getHeight() {
    return scale * Math.min(64, ctrl.getSize().height);
  }

  @Override
  public boolean isInside(Point point) {
    Rectangle r = new Rectangle(getX(), getY(), getWidth(), getHeight());
    return r.contains(point);
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    if(isInside(e.getPoint())) {
      scale += 2;
      if(scale > 6) {
        scale = 1;
      }
    }
  }

  private void drawAnimationPreview(Graphics init) {
    Graphics2D g       = (Graphics2D) init.create();
    Dimension  imgSize = ctrl.getSize();
    if(previewImage != null) {
      imgSize = new Dimension(previewImage.getWidth(null), previewImage.getHeight(null));
    }
    g.setStroke(new BasicStroke(1));
    g.translate(getWidth() - Math.min(64, imgSize.width) - 1, 2);
    g.setColor(Color.yellow.darker());
    if(previewImage == null) {
      int w = g.getFontMetrics().stringWidth("N/A");
      g.drawString("N/A", imgSize.width / 2 - w / 2, imgSize.height / 2 + g.getFontMetrics().getAscent() / 2);
    } else {
      g.drawImage(previewImage, 0, 0, null);
    }
    g.drawRect(-1, -1, Math.min(64, imgSize.width + 1), Math.min(64, imgSize.height + 1));
    g.setColor(Color.yellow.darker());
    g.drawRect(-1, -1, Math.min(64, imgSize.width + 1), Math.min(64, imgSize.height + 1));
    g.dispose();
  }

}
