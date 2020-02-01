package com.pixel.painter.ui.materials;

import java.awt.Graphics2D;
import java.awt.Shape;

public class ClipProperty implements MaterialRenderProperty<Shape> {

  private MaterialPropertyGenerator<Shape> clipArea;

  public ClipProperty(Shape shape) {
    this.clipArea = (Material m) -> {
      return shape;
    };
  }
  
  public ClipProperty(MaterialPropertyGenerator<Shape> generator) {
    this.clipArea = generator;
  }
  
  @Override
  public void apply(Graphics2D g, Material m) {
    g.setClip(clipArea.generate(m));
  }

}
