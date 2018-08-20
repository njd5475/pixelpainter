package com.pixel.painter.palettes;

import java.awt.Color;

import com.pixel.painter.model.ColorPalette;

public interface PaletteChangeListener {

  public void colorAdded(ColorPalette palette, Color color);
  
  public void colorRemoved(ColorPalette palette, Color color);
  
}
