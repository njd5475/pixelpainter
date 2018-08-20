package com.pixel.painter.model;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import com.pixel.painter.palettes.PaletteChangeListener;
import com.pixel.painter.ui.PalettePanel;

public class ColorPalette {

  private final Set<Color>                 colors;
  private final Set<PaletteChangeListener> listeners;

  public ColorPalette() {
    colors = new LinkedHashSet<Color>();
    listeners = new HashSet<>();
  }

  public Color[] getColors() {
    return colors.toArray(new Color[colors.size()]);
  }

  public void addColor(Color color) {
    colors.add(color);
    for(PaletteChangeListener l : listeners) {
      l.colorAdded(this, color);
    }
  }

  public void removeColor(Color color) {
    boolean hadColor = colors.remove(color);
    if (hadColor) {
      for(PaletteChangeListener l : listeners) {
        l.colorRemoved(this, color);
      }
    }
  }

  public static ColorPalette createFromImage(BufferedImage image) {
    ColorPalette palette = new ColorPalette();
    // find all unique colors
    int width = image.getWidth(), y;
    for(int i = 0; i < width * image.getHeight(); ++i) {
      y = i / width;
      palette.addColor(new Color(image.getRGB(i - y * width, y), true));
    }
    return palette;
  }

  public int size() {
    return colors.size();
  }

  public static ColorPalette createFrom(Color... newColors) {
    ColorPalette cp = new ColorPalette();
    cp.colors.addAll(Arrays.asList(newColors));
    return cp;
  }

  public void addChangeListener(PaletteChangeListener l) {
    listeners.add(l);
  }
  
  public void removeChangeListener(PaletteChangeListener l) {
    listeners.remove(l);
  }
}
