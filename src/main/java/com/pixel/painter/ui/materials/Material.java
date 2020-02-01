package com.pixel.painter.ui.materials;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

public class Material {

  protected final Material                      parent;
  protected final Map<String, MaterialRenderProperty> properties;
  private List<Renderer>                        renderers;

  protected Material(Material m) {
    this.parent = m;
    this.properties = new HashMap<>();
    renderers = new LinkedList<>();
  }

  public void addRenderer(Renderer renderer) {
    this.renderers.add(renderer);
  }

  public List<Renderer> getRenderers() {
    List<Renderer> renderers = new LinkedList<Renderer>();
    renderers.addAll(this.renderers);
    if(this.parent != null) {
      renderers.addAll(this.parent.getRenderers());
    }
    return renderers;
  }

  protected void put(String name, MaterialRenderProperty property) {
    properties.put(name, property);
  }

  protected MaterialRenderProperty get(String name) {
    return properties.get(name);
  }

  protected MaterialRenderProperty remove(String name) {
    return properties.remove(name);
  }

  public void applyProperties(Graphics2D g) {
    this.parent.applyProperties(g);
    for (MaterialRenderProperty prop : properties.values()) {
      prop.apply(g, this);
    }
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

  public void mouseOver(MouseEvent e) {
    if(this.parent != null) {
      this.parent.mouseOver(e);
    }
  }

  public void mouseClicked() {
    if(this.parent != null) {
      this.parent.mouseClicked();
    }
  }

  public void mouseDown(MouseEvent e) {
    if(this.parent != null) {
      this.parent.mouseDown(e);
    }
  }

  public void mouseUp(MouseEvent e) {
    if(this.parent != null) {
      this.parent.mouseUp(e);
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
  
  /**
   * This way materials are all renderered from the same object instead of each.
   * Parent and each super class rendering itsself.
   * @param m
   * @param g
   */
  public static void draw(Material m, Graphics2D g) {
    g.setComposite(AlphaComposite.Src);
    m.applyProperties(g);
    for (Renderer renderer : m.getRenderers()) {
      renderer.draw(g, m);
    }
  }

  public static Material getScreenFor(final JComponent c) {
    Material m = new Material(null) {
      public int getX() {
        return 0;
      }

      @Override
      public int getY() {
        return 0;
      }

      @Override
      public int getWidth() {
        return c.getWidth();
      }

      @Override
      public int getHeight() {
        return c.getHeight();
      }

      @Override
      public void applyProperties(Graphics2D g) {
        for (MaterialRenderProperty prop : this.properties.values()) {
          prop.apply(g, this);
        }
      }
    };
    return m;
  }

}
