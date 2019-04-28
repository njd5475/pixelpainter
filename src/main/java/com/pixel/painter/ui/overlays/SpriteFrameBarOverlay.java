package com.pixel.painter.ui.overlays;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

import javax.swing.JToolBar;

import com.pixel.painter.controller.ImageController;
import com.pixel.painter.controller.SpriteController;

public class SpriteFrameBarOverlay extends Overlay {

  private SpriteController   spritesCtrl;
  private Integer            selectedIndex;
  private Rectangle2D.Double spritePreview;

  public SpriteFrameBarOverlay(JToolBar toolbar, ImageController ctrl, SpriteController sprites) {
    super(toolbar, ctrl);
    this.spritesCtrl = sprites;
  }

  public Rectangle2D.Double getSpritePreviewArea() {
    return spritePreview;
  }

  @Override
  public void render(Graphics2D init, int width, int height) {
    super.render(init, width, height);
    drawFrameBar(init, width, height);
  }

  public static BufferedImage deepCopy(BufferedImage bi) {
    ColorModel     cm                   = bi.getColorModel();
    boolean        isAlphaPremultiplied = cm.isAlphaPremultiplied();
    WritableRaster raster               = bi.copyData(null);
    return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
  }

  private void drawFrameBar(Graphics2D g, int width, int height) {

    int stX         = 15;
    int bH          = 32;
    int offset      = 5;
    int totalHeight = bH + 2 * offset;
    // int slideBorder = 4;
    // int numVariants = 10;
    // int slideWidth = numVariants * 31;
    int imageWidth  = 32;
    int imageHeight = 32;
    selectedIndex = null;
    g.setColor(background);
    g.fillRoundRect(stX, height - totalHeight - 5, width - 60, totalHeight, 10, 10);

    Image[] images = spritesCtrl.getFrames();
    int     x      = stX + offset;
    int     y      = height - totalHeight;
    int     i      = 0;
    for (Image image : images) {
      ++i;
      g.drawImage(image, x, y, imageWidth, imageHeight, null);
      Rectangle2D.Double imgRect   = new Rectangle2D.Double(x, y, imageWidth, imageHeight);
      boolean            highlight = imgRect.contains(new Point2D.Double(mouseX, mouseY));
      if(highlight) {
        selectedIndex = i;
        g.setColor(Color.yellow);
      } else {
        g.setColor(Color.white);
      }
      g.draw(imgRect);
      x += imageWidth + offset;
    }
    Rectangle2D.Double butRect = new Rectangle2D.Double(x + offset, y, imageWidth, imageHeight);
    drawPlusButton(g, butRect);
    boolean highlight = butRect.contains(new Point2D.Double(mouseX, mouseY));

    if(highlight && performMouseOp) {
      spritePreview = new Rectangle2D.Double(butRect.x + 2 * (imageWidth + offset), butRect.y, imageWidth, imageHeight);
      if(spritesCtrl.getFrameCount() == 0) {
        spritesCtrl.createNewImage(ctrl.getImage());
      } else {
        BufferedImage image = ctrl.getImage();
        spritesCtrl.createNewImage(deepCopy(image));
      }
      spritesCtrl.changeImage(spritesCtrl.getFrameCount());
      performMouseOp = false;
    }

    if(selectedIndex != null && performMouseOp) {
      spritesCtrl.changeImage(selectedIndex);
      performMouseOp = false;
    } else if(selectedIndex == null && performMouseOp) {
      performMouseOp = false;
    }
  }
}
