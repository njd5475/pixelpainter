package com.pixel.painter.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Dimension;
import java.awt.Window;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.pixel.painter.model.ColorPalette;
import com.pixel.painter.palettes.PaletteChangeListener;
import com.pixel.painter.palettes.PaletteManager;


public class PaletteEditor extends JPanel {

  private final PaletteManager manager;
  private final ColorPalette   palette;
  private final PalettePanel   panel;
  private final JLabel         lblNameField;
  private final String         name;
  private final JButton        addColorFromChooser;

  public PaletteEditor(PaletteManager paletteManager, String name, ColorPalette palette) {
    super(new BorderLayout());

    this.manager = paletteManager;
    this.palette = palette;
    this.name = name;
    lblNameField = new JLabel(name);
    addColorFromChooser = new JButton("Add Color from Chooser");
    addColorFromChooser.addActionListener((a) -> {
      Color newColor = JColorChooser.showDialog((Component)PaletteEditor.this.getTopLevelAncestor(), "Pick a new color", Color.WHITE);
      palette.addColor(newColor);
    });

    add(lblNameField, BorderLayout.NORTH);
    add(panel = new PalettePanel(palette), BorderLayout.CENTER);
    add(addColorFromChooser, BorderLayout.SOUTH);
    palette.addChangeListener(panel);
  }

  public static ColorPalette editPalette(Window w, PaletteManager paletteManager, String name) {
    ColorPalette cp = paletteManager.get(name);
    if (cp == null) {
      cp = new ColorPalette(name);
    }
    JDialog dlg = new JDialog(w, "Editing " + name, ModalityType.APPLICATION_MODAL);
    PaletteEditor pe = new PaletteEditor(paletteManager, name, cp);
    dlg.setLayout(new BorderLayout());

    JButton close = new JButton("Close");
    close.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        dlg.setVisible(false);
        dlg.dispose();
      }
    });
    cp.addChangeListener(new PaletteChangeListener() {
      
      @Override
      public void colorRemoved(ColorPalette palette, Color color) {
        dlg.pack();
      }
      
      @Override
      public void colorAdded(ColorPalette palette, Color color) {
        dlg.pack();
      }
    });
    dlg.setLayout(new BorderLayout());
    dlg.add(pe);
    dlg.add(close, BorderLayout.SOUTH);
    dlg.pack();
    dlg.setLocationRelativeTo(null);

    dlg.setVisible(true);

    cp.removeChangeListener(pe.panel);
    paletteManager.addPalette(name, cp);
    return cp;
  }

}
