package com.pixel.painter.palettes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import com.pixel.painter.model.ColorPalette;
import com.pixel.painter.settings.Settings;

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
      
      label.setForeground(Color.white);
      label.addActionListener((e) -> {
        manager.addPalette(cp.getName(), cp);
      });
      mainPanel.add(label);
      System.out.println(cp.getName());
    }
    this.pack();
    this.repaint();
  }
  
  
}
