package com.pixel.painter.palettes;

import java.awt.Color;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.pixel.painter.model.ColorPalette;
import com.pixel.painter.settings.Json;
import com.pixel.painter.settings.Settings;

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

  public static PaletteManager loadSavedPalettes(PaletteManager pm) {
    if(pm == null) {
      pm = new PaletteManager();
    }
    File paletteFile = new File(Settings.getInstance().settingsDir(), "palettes.json");

    if(paletteFile.exists()) {
      try {
        Json.JsonObject obj = Json.parseFileObject(paletteFile);

        for (String paletteName : obj) {
          ColorPalette cp     = new ColorPalette(paletteName);
          String[]     colors = obj.getObject(paletteName).getStringArray("colors");
          for (String clStr : colors) {
            Color c = Color.decode(clStr);
            cp.addColor(c);
          }
          pm.addPalette(paletteName, cp);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return pm;
  }

  public static PaletteManager loadSavedPalettes() {
    return loadSavedPalettes(null);
  }
}
