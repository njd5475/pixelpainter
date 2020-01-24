package com.pixel.painter.ui.materials;

import javax.swing.JComponent;

public class Material {

  private final Material         parent;
  private final MaterialRenderer renderer;

  protected Material(Material m, MaterialRenderer renderer) {
    this.parent = m;
    this.renderer = renderer;
  }

  public MaterialRenderer getRenderer() {
    return renderer;
  }
  
  public MaterialBuilder derive() {
    return new MaterialBuilder(this);
  }

  public int getX() {
    if(this.parent == null) {
      return 0;
    }
    return this.parent.getX();
  }

  public int getY() {
    if(this.parent == null) {
      return 0;
    }
    return this.parent.getY();
  }

  public int getWidth() {
    if(this.parent == null) {
      return 0;
    }
    return this.parent.getWidth();
  }

  public int getHeight() {
    if(this.parent == null) {
      return 0;
    }
    return this.parent.getHeight();
  }

  public void mouseOver() {
    if(this.parent != null) {
      this.parent.mouseOver();
    }
  }

  public void mouseClicked() {
    if(this.parent != null) {
      this.parent.mouseOver();
    }
  }

  public void mouseDown() {
    if(this.parent != null) {
      this.parent.mouseDown();
    }
  }

  public void mouseUp() {
    if(this.parent != null) {
      this.parent.mouseUp();
    }
  }

  public void keyDown() {
    if(this.parent != null) {
      this.parent.keyDown();
    }
  }

  public void keyUp() {
    if(this.parent != null) {
      this.parent.keyUp();
    }
  }

  public static Material getScreenFor(final JComponent c) {
    Material m = new Material(null, MaterialRenderers.CLEAR) {
      public int getX() {
        return c.getX();
      }

      @Override
      public int getY() {
        return c.getY();
      }

      @Override
      public int getWidth() {
        return c.getWidth();
      }

      @Override
      public int getHeight() {
        return c.getHeight();
      }
      
    };
    return m;
  }

}
