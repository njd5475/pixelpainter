package com.pixel.painter.ui.materials;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import javax.swing.Action;
import javax.swing.JComponent;

public class MaterialBuilder {

  private Map<String, Material> built = new HashMap<>();
  private Material              root;
  private Material              beingBuilt;
  private Stack<Renderer>       renderFunction;
  private Stack<Material>       deriveStack;
  private JComponent            rootComp;

  public MaterialBuilder(JComponent comp) {
    this(Material.getScreenFor(comp));
    this.rootComp = comp;
  }

  public MaterialBuilder(Material root) {
    this.root = root;
    this.beingBuilt = new Material(root);
    this.renderFunction = new Stack<>();
    this.deriveStack = new Stack<>();
  }

  public Material build(String name) {
    if(beingBuilt == null) {
      beingBuilt = new Material(root);
    }
    while (!renderFunction.empty()) {
      Renderer cur = renderFunction.pop();
      beingBuilt.addRenderer(cur);
    }

    this.built.put(name, beingBuilt);

    return beingBuilt;
  }

  public MaterialBuilder origin() {
    this.beingBuilt = new Material(this.beingBuilt) {

      @Override
      public int getX() {
        return 0;
      }

      @Override
      public int getWidth() {
        return 0;
      }

    };
    return this;
  }

  public MaterialBuilder background(Color background) {
    this.renderFunction.push((Graphics2D g, Material m) -> {
      g.fillRect(m.getX(), m.getY(), m.getWidth(), m.getHeight());
    });
    this.beingBuilt.put("color", new ColorProperty(background));
    return this;
  }

  public MaterialBuilder subtractBorder(int north, int south, int east, int west) {
    this.beingBuilt = new Material(this.beingBuilt) {

      @Override
      public int getX() {
        return super.getX() + west;
      }

      @Override
      public int getY() {
        return super.getY() + north;
      }

      @Override
      public int getWidth() {
        return super.getWidth() - (east + west);
      }

      @Override
      public int getHeight() {
        return super.getHeight() - (north + south);
      }

    };
    return this;
  }

  public MaterialBuilder right(float percentage) {
    this.beingBuilt = new Material(this.beingBuilt) {

      @Override
      public int getX() {
        return parent.getX() + (int) (parent.getWidth() - (parent.getWidth() * percentage));
      }

      @Override
      public int getWidth() {
        return (int) (parent.getWidth() * percentage);
      }

    };
    return this;
  }

  public MaterialBuilder fixedSize(int width, int height) {
    this.beingBuilt = new Material(this.beingBuilt) {

      @Override
      public int getWidth() {
        return width;
      }

      @Override
      public int getHeight() {
        return height;
      }

    };
    return this;
  }

  public MaterialBuilder handleMouseUp(MaterialActionHandler handler) {
    this.beingBuilt = new Material(this.beingBuilt) {

      @Override
      public void mouseUp(MouseEvent e) {
        super.mouseUp(e);
        handler.handleAction(this, "MouseUp");
      }
      
    };
    return this;
  }
  
  public MaterialBuilder push() {
    this.deriveStack.push(beingBuilt);
    this.beingBuilt = Material.getScreenFor(rootComp);
    return this;
  }

  public MaterialBuilder roundedClip(int arcW, int arcH) {
    this.beingBuilt.put("clipArea", new ClipProperty((Material m) -> {
      return new RoundRectangle2D.Float(m.getX(), m.getY(), m.getWidth(), m.getHeight(), arcW, arcH);
    }));
    return this;
  }

  public MaterialContainerBuilder container(String... names) {
    MaterialContainerBuilder b = new MaterialContainerBuilder(this);
    b.add(names);
    return b;
  }

  public Material get(String name) {
    return this.built.get(name);
  }
  
  public MaterialBuilder set(String name) {
    this.beingBuilt = this.get(name);
    return this;
  }

  public void put(String name, Material containerM) {
    this.built.put(name, containerM);
  }

  public MaterialBuilder minimumSize(int width, int height) {
    this.beingBuilt = new Material(this.beingBuilt) {

      @Override
      public int getWidth() {
        return Math.max(width, super.getWidth());
      }

      @Override
      public int getHeight() {
        return Math.max(height, super.getHeight());
      }
      
    };
    return this;
  }

}
