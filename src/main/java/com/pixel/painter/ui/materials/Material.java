package com.pixel.painter.ui.materials;

import java.awt.Color;
import java.awt.Graphics2D;

public class Material {

  private int   height;
  private int   width;
  private int   y;
  private int   x;
  private Color color;

  public Material(int x, int y, int width, int height, Color color) {
    this.color = color;
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }

  public void draw(Graphics2D g) {
    g.setColor(color);
    g.fillRect(x, y, width, height);
  }
  
  public void mouseOver() {
    
  }
  
  public void mouseClicked() {
    
  }
  
  public void mouseDown() {
    
  }
  
  public void mouseUp() {
    
  }
  
  public void keyDown() {
    
  }
  
  public void keyUp() {
    
  }
}
