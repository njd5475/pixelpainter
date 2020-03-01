package com.pixel.painter.ui.materials;

import java.awt.Color;
import java.awt.Graphics2D;

public class ColorProperty implements MaterialRenderProperty<Color> {

  private MaterialPropertyGenerator<Color> color;
  
  public ColorProperty(Color color) {
    this.color = (Material m) -> {
      return color;
    };
  }
  
  public ColorProperty(MaterialPropertyGenerator<Color> generator) {
    this.color = generator;
  }
  
  @Override
  public void apply(Graphics2D g, Material m) {
    g.setColor(this.color.generate(m));
  }

  public Color getColor(Material m) {
    return color.generate(m);
  }
  
}
