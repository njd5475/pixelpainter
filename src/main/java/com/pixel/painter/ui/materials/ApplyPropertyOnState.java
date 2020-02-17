package com.pixel.painter.ui.materials;

import java.awt.Graphics2D;

public class ApplyPropertyOnState<T> implements MaterialRenderProperty<T> {

  private MaterialRenderProperty<T> property;
  private String state;
  
  public ApplyPropertyOnState(String state, MaterialRenderProperty<T> property) {
    this.property = property;
    this.state = state;
  }
  
  @Override
  public void apply(Graphics2D g, Material m) {
    if(m.isState(state)) {
      property.apply(g, m);
    }
  }
  
  public static <C> ApplyPropertyOnState<C> create(String state, MaterialRenderProperty<C> property) {
    ApplyPropertyOnState<C> onStateProperty = new ApplyPropertyOnState<>(state, property);
    return onStateProperty;
  }

}
