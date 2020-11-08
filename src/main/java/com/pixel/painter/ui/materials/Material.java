package com.pixel.painter.ui.materials;

import java.awt.AlphaComposite;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

public class Material {

  protected final Material                            parent;
  protected final Map<String, MaterialRenderProperty> properties;
  private List<Renderer>                              renderers;
  private boolean                                     display = true;

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
    if (this.parent != null) {
      renderers.addAll(this.parent.getRenderers());
    }
    return renderers;
  }

  protected void put(String name, MaterialRenderProperty property) {
    properties.put(name, property);
  }

  public MaterialRenderProperty getUpChain(String name) {
    MaterialRenderProperty p = properties.get(name);
    if (p == null) {
      p = parent.getUpChain(name);
    }
    return p;
  }

  public MaterialRenderProperty get(String name) {
    return properties.get(name);
  }

  protected MaterialRenderProperty remove(String name) {
    return properties.remove(name);
  }
  
  public FontMetrics getFontMetrics() {
    if(parent == null) {
      return null;
    }
    return this.parent.getFontMetrics();
  }

  public void applyProperties(Graphics2D g) {
    this.parent.applyProperties(g);
    for (MaterialRenderProperty prop : properties.values()) {
      prop.apply(g, this);
    }
  }

  public boolean isState(String state) {
    if (this.parent != null) {
      return parent.isState(state);
    }
    return false;
  }

  public void setState(String state) {
    if (this.parent != null) {
      this.parent.setState(state);
    }
  }

  public void unsetState(String state) {
    if (this.parent != null) {
      this.parent.unsetState(state);
    }
  }
  
  public boolean toggleDisplay() {
    return (this.display = !this.display);
  }

  public boolean isDisplayable() {
    return this.display;
  }
  
  /**
   * Sets the display property to true and returns what it was before the call.
   * 
   * @return Old display value before setting to true.
   */
  public boolean show() {
    boolean oldDisplay = this.display;
    this.display = true;
    return oldDisplay;
  }
  
  /**
   * Sets the display property to false and returns what it was before the call.
   * 
   * @return Old display value before setting to false.
   */
  public boolean hide() {
    boolean oldDisplay = this.display;
    this.display = false;
    return oldDisplay;
  } 
  
  public MaterialBuilder derive() {
    return new MaterialBuilderBase(this);
  }
  
  public boolean contains(Point pt) {
    Rectangle2D r = new Rectangle2D.Float(this.getX(), this.getY(), this.getWidth(), this.getHeight());
    return r.contains(pt);
  }

  public int getX() {
    if (this.parent == null) {
      return 0;
    }
    return this.parent.getX();
  }

  public int getY() {
    if (this.parent == null) {
      return 0;
    }
    return this.parent.getY();
  }

  public int getWidth() {
    if (this.parent == null) {
      return 0;
    }
    return this.parent.getWidth();
  }

  public int getHeight() {
    if (this.parent == null) {
      return 0;
    }
    return this.parent.getHeight();
  }

  public void mouseOut(MouseEvent e) {
    if (this.parent != null) {
      this.parent.mouseOut(e);
    }
  }
  
  public void mouseIn(MouseEvent e) {
    if(this.parent != null) {
      this.parent.mouseIn(e);
    }
  }

  public void mouseOver(MouseEvent e) {
    if (this.parent != null) {
      this.parent.mouseOver(e);
    }
  }

  public void mouseClicked() {
    if (this.parent != null) {
      this.parent.mouseClicked();
    }
  }

  public void mouseDown(MouseEvent e) {
    if (this.parent != null) {
      this.parent.mouseDown(e);
    }
  }

  public void mouseUp(MouseEvent e) {
    if (this.parent != null) {
      this.parent.mouseUp(e);
    }
  }

  public void keyDown() {
    if (this.parent != null) {
      this.parent.keyDown();
    }
  }

  public void keyUp() {
    if (this.parent != null) {
      this.parent.keyUp();
    }
  }

  /**
   * This way materials are all renderered from the same object instead of each.
   * Parent and each super class rendering itsself.
   * 
   * @param m
   * @param g
   */
  public static void draw(Material m, Graphics2D g) {
    if(!m.isDisplayable()) {
      return;
    }
    g.setComposite(AlphaComposite.Src);
    m.applyProperties(g);
    for (Renderer renderer : m.getRenderers()) {
      renderer.draw(g, m);
    }
  }

  public static Material getScreenFor(final JComponent c) {
    Material m = new Material(null) {
      protected Map<String, Boolean> states = new HashMap<>();

      public int getX() {
        return 0;
      }

      @Override
      public boolean isState(String state) {
        if (!states.containsKey(state)) {
          return false;
        }
        return states.get(state);
      }

      @Override
      public int getY() {
        return 0;
      }

      @Override
      public void setState(String state) {
        states.put(state, true);
      }

      @Override
      public void unsetState(String state) {
        states.put(state, false);
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
      public FontMetrics getFontMetrics() {
        return c.getFontMetrics(c.getFont());
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
