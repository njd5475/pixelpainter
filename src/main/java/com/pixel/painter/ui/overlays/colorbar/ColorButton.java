package com.pixel.painter.ui.overlays.colorbar;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class ColorButton {

  private int             x;
  private int             y;
  private Color           color;
  private ColorBarOverlay overlay;
  private boolean         highlighted;

  public ColorButton(int offX, int offY, Color color, ColorBarOverlay overlay) {
    this.x       = offX;
    this.y       = offY;
    this.color   = color;
    this.overlay = overlay;
  }

  public Color getColor() {
    return color;
  }

  public void apply() {
    overlay.addSelectedBrush(this.color);
  }

  public void draw(Graphics2D g2d) {
    int width  = overlay.getButtonWidth();
    int height = overlay.getButtonHeight();

    g2d.setColor(color);
    g2d.fillRect(x, y, width, height);

    if(highlighted) {
      g2d.setColor(Color.white);
      g2d.drawRect(x, y, width, height);

      overlay.drawRGBHint(g2d, x, y, color);
    }
  }

  public void highlight() {
    this.highlighted = true;
  }

  public void unhighlight() {
    this.highlighted = false;
  }

  public boolean contains(int mousex, int mousey) {
    return getBounds().contains(mousex, mousey);
  }

  public Rectangle getBounds() {
    return (new Rectangle(overlay.getX() + x, overlay.getY() + y, overlay.getButtonWidth(), overlay.getButtonHeight()));
  }
}
