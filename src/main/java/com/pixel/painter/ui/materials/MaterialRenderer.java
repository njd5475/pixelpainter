package com.pixel.painter.ui.materials;

import java.awt.Graphics2D;

public interface MaterialRenderer {

  public void predraw(Graphics2D g, Material material, Object properties);
  public void draw(Graphics2D g, Material material, Object properties);
  public void postdraw(Graphics2D g, Material material, Object properties);
}
