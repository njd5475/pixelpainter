package com.pixel.painter.ui.materials;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

public class MaterialContainerBuilder {

  private MaterialBuilder                    parent;
  private Collection<String>                 componentNames   = new HashSet<String>();
  private MaterialPropertyGenerator<Integer> width;
  private MaterialPropertyGenerator<Integer> height;
  private MaterialPropertyGenerator<Integer> totalWidth;
  private MaterialPropertyGenerator<Integer> totalHeight;
  private Material                           built;
  private boolean                            resizeComponents = false;

  public MaterialContainerBuilder(MaterialBuilder parent) {
    this.parent = parent;
    setResizeableWidthHeight();
    totalWidth = (Material m) -> {
      int w = 0;
      for (String name : componentNames) {
        w += parent.get(name).getWidth();
      }
      return w;
    };
    totalHeight = (Material m) -> {
      int h = 0;
      for (String name : componentNames) {
        h += parent.get(name).getHeight();
      }
      return h;
    };
  }

  private void setResizeableWidthHeight() {
    width = totalWidth;
    height = totalHeight;
  }

  public MaterialContainerBuilder add(String... names) {
    componentNames.addAll(Arrays.asList(names));
    return this;
  }

  public MaterialContainerBuilder fixedWidth() {
    width = (Material m) -> {
      return m.parent.getWidth();
    };
    return this;
  }

  public MaterialContainerBuilder fixedHeight() {
    height = (Material m) -> {
      return m.parent.getHeight();
    };
    return this;
  }

  public MaterialContainerBuilder resizableComponents() {
    // setResizeableWidthHeight();
    resizeComponents = true;
    return this;
  }

  public Material build(String name) {
    if(built == null) {
      built = new Material(this.parent.build(name + "_parent")) {

        @Override
        public int getWidth() {
          if(width != null) {
            return width.generate(this);
          }
          return super.getWidth();
        }

        @Override
        public int getHeight() {
          if(height != null) {
            return height.generate(this);
          }
          return super.getHeight();
        }

        @Override
        public void mouseUp(MouseEvent e) {
          super.mouseUp(e);
          if(!e.isConsumed()) {
            for (String name : componentNames) {
              Material    m = MaterialContainerBuilder.this.parent.get(name);
              Rectangle2D r = new Rectangle2D.Float(this.getX() + m.getX(), this.getY() + m.getY(), m.getWidth(),
                  m.getHeight());
              if(r.contains(e.getPoint())) {
                m.mouseUp(e);
                e.consume();
                break;
              }
            }
          }
        }

        @Override
        public void mouseOver(MouseEvent e) {
          super.mouseOver(e);
          if(!e.isConsumed()) {
            boolean consume = false;
            for (String name : componentNames) {
              Material    m = MaterialContainerBuilder.this.parent.get(name);
              Rectangle2D r = new Rectangle2D.Float(this.getX() + m.getX(), this.getY() + m.getY(), m.getWidth(),
                  m.getHeight());
              if(r.contains(e.getPoint())) {
                m.setState("mouseContainerItemOver");
                m.mouseOver(e);
                consume = true;
              }else if(m.isState("mouseContainerItemOver")) {
                m.unsetState("mouseContainerItemOver");
                m.mouseOut(e);
              }
            }
            if(consume) {
              e.consume();
            }
          }
        }

        @Override
        public void mouseOut(MouseEvent e) {
          super.mouseOut(e);
          for (String name : componentNames) {
            Material    m = MaterialContainerBuilder.this.parent.get(name);
            Rectangle2D r = new Rectangle2D.Float(this.getX() + m.getX(), this.getY() + m.getY(), m.getWidth(),
                m.getHeight());
            if(!r.contains(e.getPoint())) {
              if(m.isState("mouseContainerItemOver")) {
                m.unsetState("mouseContainerItemOver");
                m.mouseOut(e);
              }
            }
          }
        }

        @Override
        public void mouseClicked() {
          // TODO Auto-generated method stub
          super.mouseClicked();
        }

        @Override
        public void mouseDown(MouseEvent e) {
          super.mouseDown(e);
        }

      };
      final Dimension originalSize = new Dimension(40, 40);
      final Dimension compSize     = new Dimension(40, 40);
      built.addRenderer((Graphics2D g, Material m) -> {
        g = (Graphics2D) g.create(m.getX(), m.getY(), m.getWidth(), m.getHeight());
        int maxY      = 0;
        int maxHeight = 0;
        for (String compName : componentNames) {
          Material mat = MaterialContainerBuilder.this.parent.get(compName);
          Material.draw(mat, g);

          if(mat instanceof ContainerComponentMaterial) {
            ContainerComponentMaterial ccm = (ContainerComponentMaterial) mat;
            maxY = Math.max(maxY, ccm.getY() + ccm.getHeight());
          }
          maxHeight = Math.max(maxHeight, mat.getHeight());
        }

        if(maxY + (2 * maxHeight) < m.getHeight()) {
          compSize.setSize(Math.min(originalSize.getWidth(), compSize.getWidth() + 1),
              Math.min(originalSize.getHeight(), compSize.getHeight() + 1));
        }

        g.dispose();
      });
      if(resizeComponents) {
        Material m = this.parent.get(componentNames.iterator().next());
        rebuildComponentsWithResizing(this.built, compSize);
      }
    }
    return built;
  }

  private void rebuildComponentsWithResizing(final Material built2, Dimension componentSize) {
    System.out.println("How many times do we rebuild");
    String last = null;
    this.revertComponents();
    for (String name : componentNames) {
      Material m          = parent.get(name);
      Material containerM = new ContainerComponentMaterial(m, built2, name, last, componentSize);
      parent.put(name, containerM);
      last = name;
    }
  }

  private void revertComponents() {
    for (String name : componentNames) {
      Material m = parent.get(name);
      if(m instanceof ContainerComponentMaterial) {
        ContainerComponentMaterial ccm = (ContainerComponentMaterial) m;
        ccm.revert();
      }
    }
  }

  private class ContainerComponentMaterial extends Material {
    private final Material container;
    private final String   trackName;
    private Dimension      size;
    private String         name;
    private boolean        _fits;

    public ContainerComponentMaterial(Material m, Material built2, String name, String trackName, Dimension size) {
      super(m);
      this.container = built2;
      this.trackName = trackName;
      this.name = name;
      this._fits = true;
      this.size = size;
    }

    public void revert() {
      MaterialContainerBuilder.this.parent.put(name, this.parent);
    }

    public boolean fits() {
      return this._fits;
    }

    @Override
    public int getX() {
      Material track = MaterialContainerBuilder.this.parent.get(trackName);
      if(track != null) {
        int newX = track.getX() + track.getWidth();

        if(newX < container.getWidth() - this.parent.getWidth()) {
          return newX;
        }

        return 0;
      }
      return super.getX();
    }

    @Override
    public int getY() {
      Material track = MaterialContainerBuilder.this.parent.get(trackName);
      if(track != null) {
        int newX = track.getX() + track.getWidth() + this.parent.getWidth();

        if(newX >= container.getWidth()) {
          int newY = track.getY() + track.getHeight();

          if(newY > container.getHeight()) {
            this.size.setSize(this.size.getWidth() - 1, this.size.getHeight() - 1);
            this._fits = false;
          }

          return newY;
        } else {
          this._fits = true;
        }

        return track.getY();
      }
      return super.getY();
    }

    @Override
    public int getWidth() {
      return (int) size.getWidth();
    }

    @Override
    public int getHeight() {
      return (int) size.getHeight();
    }
  }
}
