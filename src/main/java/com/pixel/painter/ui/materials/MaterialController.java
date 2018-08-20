package com.pixel.painter.ui.materials;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

public class MaterialController {

  Map<String, Object> materials = new HashMap<String, Object>();
  
  public MaterialController() {
  }
  
  public void draw(Graphics2D g) {
    Rectangle clipBounds = g.getClipBounds();
  }
}
