package com.pixel.painter.ui.materials;

import javax.swing.JComponent;

public class MaterialBuilder {

  private MaterialRenderer base = MaterialRenderers.CLEAR;
  private Material root;
  private Material beingBuilt;
  
  public MaterialBuilder(JComponent comp) {
     this(Material.getScreenFor(comp));
  }
  
  public MaterialBuilder(Material root) {
    this.root = root;
    this.beingBuilt = new Material(root, base);
  }
  
  public MaterialBuilder right(float percentage) {
    this.beingBuilt = new Material(this.beingBuilt, base) {

      @Override
      public int getX() {
        return super.getX() + (int)(super.getWidth() - (super.getWidth() * percentage));
      }

      @Override
      public int getWidth() {
        return (int)(super.getWidth() * percentage);
      }
      
    };
    return this;
  }
  
  public Material build() {
    if(beingBuilt == null) {
      beingBuilt = new Material(root, base);
    }
    return beingBuilt;
  }
  
}
