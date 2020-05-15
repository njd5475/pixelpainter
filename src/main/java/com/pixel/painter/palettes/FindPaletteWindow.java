package com.pixel.painter.palettes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import com.pixel.painter.model.ColorPalette;
import com.pixel.painter.ui.MaterialComponentBuilder;
import com.pixel.painter.ui.PixelPainter;
import com.pixel.painter.ui.materials.Material;
import com.pixel.painter.ui.materials.MaterialActionHandler;
import com.pixel.painter.ui.materials.MaterialBuilder;
import com.pixel.painter.ui.materials.MaterialBuilderBase;

public class FindPaletteWindow extends JDialog {

  private Thread thread;
  private JProgressBar bar;
  private ColorPalette[] palettes;
  private JPanel mainPanel = new JPanel();
  private PaletteManager manager;

  public FindPaletteWindow(JFrame parent, PaletteManager manager) {
    super(parent);
    this.manager = manager;
    this.setTitle("Find a palette");
    this.setMinimumSize(new Dimension(80, 40));
    this.setPreferredSize(new Dimension((int)(parent.getWidth()*0.8), (int)(parent.getHeight()*0.8)));
    
    this.setLayout(new BorderLayout());
    this.add(mainPanel);
    
    mainPanel.setLayout(new FlowLayout());
    bar = new JProgressBar();
    bar.setIndeterminate(true);
    bar.setMinimumSize(new Dimension((int)(parent.getWidth()*0.75f), (int)(parent.getHeight()*0.2f)));
    mainPanel.add(bar, BorderLayout.CENTER);
    
    this.thread = new Thread(() -> {
      ColorPalette[] retreive = RemotePalettes.retreive();
      javax.swing.SwingUtilities.invokeLater(() -> {
        consume(retreive);
      });
    });
    this.thread.start();
  }

  private void consume(ColorPalette[] retreive) {
    String title = String.format("Got %d palettes back from lospec", retreive.length);
    this.setTitle(title);
    this.thread = null;
    this.palettes = retreive;
    mainPanel.removeAll();
    mainPanel.remove(bar);
    mainPanel.revalidate();
    for(ColorPalette cp : this.palettes) {
      JButton label = new JButton(cp.getName());
      label.setLayout(new BorderLayout());
      Dimension butSize = new Dimension((int)(mainPanel.getWidth() * 0.95f), 45);
      label.setSize(butSize);
      label.setMinimumSize(butSize);
      label.setPreferredSize(butSize);
      label.add(buildPaletteComponent(label, cp));
      label.addActionListener((e) -> {
        manager.addPalette(cp.getName(), cp);
      });
      mainPanel.add(label);
      System.out.println(cp.getName());
    }
    this.pack();
    this.repaint();
  }
  
  private JComponent buildPaletteComponent(JComponent comp, ColorPalette cp) {

    MaterialComponentBuilder builder    = new MaterialComponentBuilder(comp);
    MaterialBuilder          matBuilder = new MaterialBuilderBase(comp);

    Color[]               colors = cp.getColors();
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
    return builder.wrap(matColors);
  }
  
}
