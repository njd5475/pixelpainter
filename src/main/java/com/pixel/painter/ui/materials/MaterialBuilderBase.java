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

public class MaterialBuilderBase implements MaterialBuilder {

  private Map<String, Material> built = new HashMap<>();
  private Material              root;
  private Material              beingBuilt;
  private Stack<Renderer>       renderFunctions;
  private Stack<Material>       deriveStack;
  private JComponent            rootComp;
  private Stack<String>         states;

  public MaterialBuilderBase(JComponent comp) {
    this(Material.getScreenFor(comp));
    this.rootComp = comp;
  }

  public MaterialBuilderBase(Material root) {
    this.root = root;
    this.beingBuilt = new Material(root);
    this.renderFunctions = new Stack<>();
    this.deriveStack = new Stack<>();
    this.states = new Stack<>();
  }

  @Override
  public Material build(String name) {
    if(beingBuilt == null) {
      beingBuilt = new Material(root);
    }
    while (!renderFunctions.empty()) {
      Renderer cur = renderFunctions.pop();
      beingBuilt.addRenderer(cur);
    }

    this.built.put(name, beingBuilt);

    return beingBuilt;
  }

  public Material getRoot() {
    return root;
  }

  @Override
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

  protected void addRenderer(String state, Renderer renderer) {
    if(state != null) {
      renderer = new RenderOnState(state, renderer);
    }
    this.renderFunctions.push(renderer);
  }
  
  protected void setProperty(String name, MaterialRenderProperty<?> property, String state) {
    if(state != null) {
      property = ApplyPropertyOnState.create(state, property);
    }
    if(this.beingBuilt.get(name) != null) {
      //need to chain property
      property = ChainRenderProperty.create(property, this.beingBuilt.get(name));
    }
    this.beingBuilt.put(name, property);
  }

  @Override
  public MaterialBuilder onState(String state) {
    states.push(state);
    return this;
  }

  @Override
  public MaterialBuilder background(Color background) {
    String state = null;
    if(!this.states.empty()) {
      state = this.states.pop();
    }
    addRenderer(state, (Graphics2D g, Material m) -> {
      g.fillRect(m.getX(), m.getY(), m.getWidth(), m.getHeight());
    });
    setProperty("color", new ColorProperty(background), state);
    return this;
  }

  @Override
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

  @Override
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

  @Override
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

  @Override
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
  
  @Override
  public MaterialBuilder handleMouseMove(MaterialActionHandler handler) {
    this.beingBuilt = new Material(this.beingBuilt) {
      @Override
      public void mouseOver(MouseEvent e) {
        super.mouseOver(e);
        handler.handleAction(this, "MouseMove");
      }
    };
    return this;
  }
  
  @Override
  public MaterialBuilder handleMouseOut(MaterialActionHandler handler) {
    this.beingBuilt = new Material(this.beingBuilt) {
      @Override
      public void mouseOut(MouseEvent e) {
        super.mouseOut(e);
        handler.handleAction(this, "MouseOut");
      }
    };
    return this;
  }


  @Override
  public MaterialBuilder push() {
    this.deriveStack.push(beingBuilt);
    this.beingBuilt = Material.getScreenFor(rootComp);
    return this;
  }

  @Override
  public MaterialBuilder roundedClip(int arcW, int arcH) {
    this.beingBuilt.put("clipArea", new ClipProperty((Material m) -> {
      return new RoundRectangle2D.Float(m.getX(), m.getY(), m.getWidth(), m.getHeight(), arcW, arcH);
    }));
    return this;
  }

  @Override
  public MaterialContainerBuilder container(String... names) {
    MaterialContainerBuilder b = new MaterialContainerBuilder(this);
    b.add(names);
    return b;
  }

  @Override
  public Material get(String name) {
    return this.built.get(name);
  }

  @Override
  public MaterialBuilder set(String name) {
    this.beingBuilt = this.get(name);
    return this;
  }

  @Override
  public void put(String name, Material containerM) {
    this.built.put(name, containerM);
  }

  @Override
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
