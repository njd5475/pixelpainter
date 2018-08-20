package com.pixel.painter.palettes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.pixel.painter.model.ColorPalette;

public class PaletteManager {

  private final Set<PaletteListener>      listeners;
  private final Map<String, ColorPalette> palettes;

  public PaletteManager() {
    palettes = new HashMap<String, ColorPalette>();
    listeners = new HashSet<PaletteListener>();
  }

  public void addPaletteListener(PaletteListener l) {
    listeners.add(l);
  }

  public void addPalette(String name, ColorPalette palette) {
    palettes.put(name, palette);
    fireOnPaletteAdded(name, palette);
  }

  private void fireOnPaletteAdded(String name, ColorPalette palette) {
    for(PaletteListener l : listeners) {
      l.paletteAdded(this, name, palette);
    }
  }

  public ColorPalette get(String name) {
    return palettes.get(name);
  }

  public boolean hasPalettes() {
    return !palettes.isEmpty();
  }

  public Set<Map.Entry<String, ColorPalette>> getPalettes() {
    return palettes.entrySet();
  }

}
