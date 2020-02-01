package com.pixel.painter.ui.materials;

import java.awt.Graphics2D;

public interface MaterialRenderProperty<T> {

  public void apply(Graphics2D g, Material m);
  
}
