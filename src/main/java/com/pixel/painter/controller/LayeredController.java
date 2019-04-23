package com.pixel.painter.controller;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import com.pixel.painter.brushes.Brush;
import com.pixel.painter.brushes.BrushChangeListener;
import com.pixel.painter.brushes.ColorBrush;
import com.pixel.painter.ui.ModifyListener;

public class LayeredController implements ImageController {

  private SortedMap<Integer, ImageController> layers = new TreeMap<>();
  private int                                 width  = 0;
  private int                                 height = 0;
  private Brush                               currentBrush;
  private ImageController                     currentCtrl;
  private Color                               fillColor;

  public LayeredController() {
  }

  public void addLayer(ImageController ctrl) {
    Dimension size = ctrl.getSize();
    layers.put(layers.size() + 1, ctrl);
    width  = Math.max(width, size.width);
    height = Math.max(height, size.height);
    if(currentCtrl == null) {
      changeLayer(layers.size());
    }
  }

  public int getLayerCount() {
    return layers.size();
  }

  public void changeLayer(int index) {
    ImageController ctrl = layers.get(index);
    if(ctrl != null) {
      currentCtrl = ctrl;
      if(currentBrush != null) {
        currentCtrl.setBrush(currentBrush);
      }
      if(fillColor != null) {
        currentCtrl.setFillColor(fillColor);
      }
    }
  }

  @Override
  public Dimension getSize() {
    return new Dimension(width, height);
  }

  @Override
  public void setBrush(Brush brush) {
    currentBrush = brush;
    currentCtrl.setBrush(brush);
  }

  @Override
  public void addBrushChangeListener(BrushChangeListener l) {
    for (ImageController ctrl : layers.values()) {
      ctrl.addBrushChangeListener(l);
    }
  }

  @Override
  public void applyBrush(int x, int y) {
    currentCtrl.applyBrush(x, y);
  }

  @Override
  public Brush createColorBrush(Color color) {
    return ColorBrush.createColorBrush(this, color);
  }

  @Override
  public void render(Graphics2D g) {
    Set<Entry<Integer, ImageController>> entries = layers.entrySet();
    for (Entry<Integer, ImageController> entry : entries) {
      entry.getValue().render(g);
    }
  }

  @Override
  public void render(Graphics2D g, int width, int height) {
    Set<Entry<Integer, ImageController>> entries = layers.entrySet();
    for (Entry<Integer, ImageController> entry : entries) {
      entry.getValue().render(g, width, height);
    }
  }

  @Override
  public void save(File selectedFile) throws IOException {
    // TODO Auto-generated method stub

  }

  @Override
  public void save(File selectedFile, String extension) throws IOException {
    // TODO Auto-generated method stub

  }

  @Override
  public Color sample(int x, int y) {
    Color                                color   = null;
    Set<Entry<Integer, ImageController>> entries = layers.entrySet();
    for (Entry<Integer, ImageController> entry : entries) {
      color = entry.getValue().sample(x, y);
      if(color != null) {
        break;
      }
    }
    return color;
  }

  @Override
  public void clearColor(int x, int y) {
    currentCtrl.clearColor(x, y);
  }

  @Override
  public void setColorAt(int x, int y, Color c) {
    currentCtrl.setColorAt(x, y, c);
  }

  @Override
  public void setColorAt(int x, int y) {
    currentCtrl.setColorAt(x, y);
  }

  @Override
  public Brush getBrush() {
    return this.currentBrush;
  }

  @Override
  public void setFillColor(Color fill) {
    this.fillColor = fill;
    this.currentCtrl.setFillColor(fill);
  }

  @Override
  public Color getFillColor() {
    return this.fillColor;
  }

  @Override
  public void addAllModifyListeners(Set<ModifyListener> ls) {
    for (ImageController ctrl : layers.values()) {
      ctrl.addAllModifyListeners(ls);
    }
  }

  @Override
  public BufferedImage getImage() {
    return currentCtrl.getImage();
  }

  @Override
  public Set<Point> getAll(int red, int green, int blue, int alpha) {
    return new HashSet<Point>();
  }

  @Override
  public void setAllColorsAt(int x, int y, Color newColor) {
    for (ImageController ctrl : layers.values()) {
      ctrl.setAllColorsAt(x, y, newColor);
    }
  }

}
