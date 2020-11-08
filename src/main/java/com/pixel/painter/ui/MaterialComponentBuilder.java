package com.pixel.painter.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

import com.pixel.painter.ui.materials.Material;
import com.pixel.painter.ui.materials.MaterialBuilderBase;

public class MaterialComponentBuilder extends MaterialBuilderBase {

  public MaterialComponentBuilder(JComponent parent) {
    super(parent);
  }

  public JComponent wrap(Material m) {
    JComponent wrapper = new JComponent() {

      @Override
      public void paint(Graphics g) {
        super.paint(g);
        Material.draw(m, (Graphics2D) g);
      }

      @Override
      public Dimension getPreferredSize() {
        return super.getSize();
      }

      @Override
      public Dimension getMaximumSize() {
        return super.getSize();
      }

      @Override
      public Dimension getMinimumSize() {
        return super.getSize();
      }

      @Override
      public Dimension getSize(Dimension rv) {
        return super.getSize(rv);
      }

      @Override
      public int getX() {
        return m.getX();
      }

      @Override
      public int getY() {
        return m.getY();
      }

      @Override
      public int getWidth() {
        return m.getWidth();
      }

      @Override
      public int getHeight() {
        return m.getHeight();
      }

    };
    wrapper.addMouseListener(new MouseAdapter() {

      @Override
      public void mousePressed(MouseEvent e) {
        Rectangle2D r = new Rectangle2D.Float(m.getX(), m.getY(), m.getWidth(), m.getHeight());
        if (r.contains(e.getPoint())) {
          m.mouseDown(e);
          e.consume();
        }

        wrapper.repaint();
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        Rectangle2D r = new Rectangle2D.Float(m.getX(), m.getY(), m.getWidth(), m.getHeight());
        if (r.contains(e.getPoint())) {
          m.mouseUp(e);
          e.consume();
        }

        wrapper.repaint();
      }

    });

    wrapper.addMouseMotionListener(new MouseMotionListener() {

      @Override
      public void mouseDragged(MouseEvent e) {
      }

      @Override
      public void mouseMoved(MouseEvent e) {
        Rectangle2D r = new Rectangle2D.Float(m.getX(), m.getY(), m.getWidth(), m.getHeight());
        boolean contains = r.contains(e.getPoint());

        if (contains && !m.isState("mouseOver") && !m.isState("mouseIn")) {
          m.setState("mouseIn");
          m.mouseIn(e);
          e.consume();
        } else if (contains && !m.isState("mouseOver") && m.isState("mouseIn")) {
          m.setState("mouseOver");
          m.mouseOver(e);
          e.consume();
        } else if(contains && m.isState("mouseOver")) {
          m.mouseOver(e);
          e.consume();
        } else if (!contains && (m.isState("mouseOver") || m.isState("mouseIn"))) {
          m.unsetState("mouseIn");
          m.unsetState("mouseOver");
          m.setState("mouseOut");
          m.mouseOut(e);
          e.consume();
        }

        wrapper.repaint();
      }

    });
    return wrapper;
  }

  @Override
  public Material build(String name) {
    Material m = super.build(name);
    this.getRootComponent().add(wrap(m));
    return m;
  }

}
