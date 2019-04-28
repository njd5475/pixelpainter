package com.pixel.painter.controller;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Set;

import com.pixel.painter.brushes.Brush;
import com.pixel.painter.brushes.BrushChangeListener;
import com.pixel.painter.ui.ModifyListener;

public interface ImageController {

  public Dimension getSize();

  public void setBrush(Brush brush);

  public void addBrushChangeListener(BrushChangeListener l);

  public void applyBrush(int x, int y);

  public Brush createColorBrush(Color color);

  public void render(Graphics2D g);

  public void render(Graphics2D g, int width, int height);

  public void save(File selectedFile) throws IOException;

  public void save(File selectedFile, String extension) throws IOException;

  public Color sample(int x, int y);
  
  public int[] samplePixels(Rectangle r);

  public void clearColor(int x, int y);

  public void setColorAt(int x, int y, Color c);

  public void setColorAt(int x, int y);

  public Brush getBrush();

  public void setFillColor(Color fill);

  public Color getFillColor();

  public void addAllModifyListeners(Set<ModifyListener> ls);
  
  public void addModifyListener(ModifyListener l);

  public BufferedImage getImage();

  public Set<Point> getAll(int red, int green, int blue, int alpha);

  public void setAllColorsAt(int x, int y, Color newColor);

}
