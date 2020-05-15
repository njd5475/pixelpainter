package com.pixel.painter.ui.materials;

import java.awt.Graphics2D;

public class ChainRenderProperty<T> implements MaterialRenderProperty<T> {

  private MaterialRenderProperty<T> property;
  private MaterialRenderProperty<?> next;
  
  public ChainRenderProperty(MaterialRenderProperty<T> property, MaterialRenderProperty<?> next) {
    this.property = property;
    this.next = next;
  }
  
  public MaterialRenderProperty<T> getProperty() {
    return property;
  }
  
  @Override
  public void apply(Graphics2D g, Material m) {
    next.apply(g, m);
    property.apply(g, m);
  }
  
  public static <C> ChainRenderProperty<C> create(MaterialRenderProperty<C> property, MaterialRenderProperty<?> next) {
    ChainRenderProperty<C> onStateProperty = new ChainRenderProperty<>(property, next);
    return onStateProperty;
  }

}

