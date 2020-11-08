package com.pixel.painter.controller;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.swing.undo.UndoManager;

import com.pixel.painter.brushes.Brush;
import com.pixel.painter.brushes.BrushChangeListener;
import com.pixel.painter.brushes.ColorBrush;
import com.pixel.painter.ui.ModifyListener;

public class LayeredController implements ImageController {

  private SortedMap<Integer, ImageController> layers          = new TreeMap<>();
  private Set<Integer>                        invisibleLayers = new HashSet<Integer>();
  private int                                 width           = 0;
  private int                                 height          = 0;
  private Brush                               currentBrush;
  private ImageController                     currentCtrl;
  private Color                               fillColor;
  private Set<ModifyListener>                 listeners;
  private ModifyListener                      oneListener;
  private Integer                             currentIndex;

  public LayeredController() {
    listeners = new HashSet<>();
    oneListener = new ModifyListener() {

      @Override
      public void modified(ImageController imgCtrl) {
        for (ModifyListener l : listeners) {
          l.modified(imgCtrl);
        }
      }
    };
  }

  public void addLayer(ImageController ctrl) {
    Dimension size = ctrl.getSize();
    layers.put(layers.size() + 1, ctrl);
    width = Math.max(width, size.width);
    height = Math.max(height, size.height);
    if (currentCtrl == null) {
      changeLayer(layers.size());
    }
    ctrl.addModifyListener(oneListener);
  }

  public boolean toggleLayer(int layerIndex) {
    if (this.invisibleLayers.contains(layerIndex)) {
      this.invisibleLayers.remove(layerIndex);
    } else {
      this.invisibleLayers.add(layerIndex);
    }
    return this.isVisible(layerIndex);
  }

  public int getLayerCount() {
    return layers.size();
  }

  public void changeLayer(int index) {
    ImageController ctrl = layers.get(index);
    if (ctrl != null) {
      if (currentCtrl != ctrl) {
        currentCtrl = ctrl;
        if (currentBrush != null) {
          currentCtrl.setBrush(currentBrush);
        }
        if (fillColor != null) {
          currentCtrl.setFillColor(fillColor);
        }
      } else {
        this.toggleLayer(index);
      }
      this.currentIndex = index;
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

  public boolean isVisible(int layer) {
    return !this.invisibleLayers.contains(layer);
  }

  @Override
  public void render(Graphics2D g) {
    Set<Entry<Integer, ImageController>> entries = layers.entrySet();
    for (Entry<Integer, ImageController> entry : entries) {
      if (isVisible(entry.getKey())) {
        entry.getValue().render(g);
      }
    }
  }

  @Override
  public void render(Graphics2D g, int width, int height) {
    Set<Entry<Integer, ImageController>> entries = layers.entrySet();
    for (Entry<Integer, ImageController> entry : entries) {
      if (isVisible(entry.getKey())) {
        entry.getValue().render(g, width, height);
      }
    }
  }

  @Override
  public void save(File selectedFile) throws IOException {
    this.save(selectedFile, "png");
  }

  @Override
  public void save(File selectedFile, String extension) throws IOException {
    System.out.println("Saving with extensions " + extension);
    ImageIO.write(getVisibleImage(), extension, selectedFile);
  }

  private RenderedImage getVisibleImage() {
    BufferedImage bImg = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = bImg.createGraphics();
    this.render(g);
    g.dispose();
    return bImg;
  }

  @Override
  public Color sample(int x, int y) {
    Color color = null;
    Set<Entry<Integer, ImageController>> entries = layers.entrySet();
    for (Entry<Integer, ImageController> entry : entries) {
      color = entry.getValue().sample(x, y);
      if (color != null) {
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
    System.out.println("Add all modify listeners to LayeredController");
    listeners.addAll(ls);
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

  @Override
  public void addModifyListener(ModifyListener l) {
    listeners.add(l);
  }

  @Override
  public int[] samplePixels(Rectangle r) {
    return null;
  }

  @Override
  public UndoManager getUndoManager() {
    return this.currentCtrl.getUndoManager();
  }

  @Override
  public void startRecording() {
    this.currentCtrl.startRecording();
  }

  @Override
  public void endRecording() {
    this.currentCtrl.endRecording();
  }

  public Integer getCurrentLayer() {
    return currentIndex;
  }

  public void putLayer(ImageController ctrl, int i) {
    Dimension size = ctrl.getSize();
    layers.put(i, ctrl);
    width = Math.max(width, size.width);
    height = Math.max(height, size.height);
    if (currentCtrl == null) {
      changeLayer(i);
    }
    ctrl.addModifyListener(oneListener);
  }

  public boolean hasLayer(int layer) {
    return this.layers.containsKey(layer);
  }

}
