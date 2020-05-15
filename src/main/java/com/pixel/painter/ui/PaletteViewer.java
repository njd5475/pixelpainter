package com.pixel.painter.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFrame;

import com.pixel.painter.model.ColorPalette;
import com.pixel.painter.palettes.PaletteManager;
import com.pixel.painter.ui.materials.Material;
import com.pixel.painter.ui.materials.MaterialActionHandler;
import com.pixel.painter.ui.materials.MaterialBuilder;
import com.pixel.painter.ui.materials.MaterialBuilderBase;

public class PaletteViewer {

  public static void main(String... args) {
    JFrame    frame = new JFrame();
    Dimension size  = new Dimension(800, 600);
    frame.setLayout(new FlowLayout());
    PaletteManager pm = PaletteManager.loadSavedPalettes();
    for (Entry<String, ColorPalette> cp : pm.getPalettes()) {
      JButton jbut = new JButton(cp.getKey());
      jbut.setLayout(new BorderLayout());
      Dimension butSize = new Dimension(780, 40);
      jbut.setSize(butSize);
      jbut.setMinimumSize(butSize);
      jbut.setPreferredSize(butSize);
      MaterialComponentBuilder builder    = new MaterialComponentBuilder(jbut);
      MaterialBuilder          matBuilder = new MaterialBuilderBase(jbut);

      Color[]               colors = cp.getValue().getColors();
      MaterialActionHandler noop   = (m, s) -> {};
      
      Set<String>           group  = new HashSet<>();
      for (Color color : colors) {
        String name = color.toString();
        PixelPainter.getColorMaterial(matBuilder, name, color, noop, noop, noop);
        group.add(name);
      }

      String   names[]   = group.toArray(new String[group.size()]);
      matBuilder.push();
      Material matColors = matBuilder.left(1.0f).container(names).resizableComponents().build("PaletteMaterial");
      jbut.add(builder.wrap(matColors));
      frame.add(jbut);
    }
    frame.setMinimumSize(size);
    frame.setPreferredSize(size);
    frame.setLocationRelativeTo(null);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
  }
}
