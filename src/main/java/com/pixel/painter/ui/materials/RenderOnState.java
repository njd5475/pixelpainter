package com.pixel.painter.ui.materials;

import java.awt.Graphics2D;

public class RenderOnState implements Renderer {

  private Renderer renderer;
  private String   state;

  public RenderOnState(String state, Renderer renderer) {
    this.renderer = renderer;
    this.state = state;
  }

  @Override
  public void draw(Graphics2D g, Material m) {
    if(m.isState(state)) {
      this.renderer.draw(g, m);
    }
  }

}
