package com.pixel.painter.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;

import com.pixel.painter.model.ColorPalette;
import com.pixel.painter.palettes.PaletteChangeListener;

public class PalettePanel extends JPanel implements MouseListener, MouseMotionListener, PaletteChangeListener {

  public static final String     COLOR_SELECTED = "ColorSelected";
  private final int              columns        = 10;
  private final int              rows           = 10;
  private final ColorPalette     palette;
  private JButton                next;
  private int                    index;
  private JButton                back;
  private JButton                remove;
  private JComponent             selected;
  private Map<JComponent, Color> colorComps;

  public PalettePanel(ColorPalette palette) {
    super(new GridBagLayout());
    this.setMinimumSize(new Dimension(100,100));
    this.palette = palette;

    showColors(index = 0);
  }

  private void showColors(int startIndex) {
    if (startIndex >= palette.size()) {
      return;
    }

    this.removeAll();
    this.colorComps = new HashMap<>();
    this.selected = null;
    
    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.LINE_START;
    c.insets = new Insets(3, 3, 3, 3);
    c.fill = GridBagConstraints.NONE;
    c.gridx = 0;
    c.gridy = 0;
    Color colors[] = palette.getColors();
    JComponent jcomp;
    Arrays.sort(colors, 0, colors.length, new Comparator<Color>() {
      @Override
      public int compare(Color o1, Color o2) {
        return o1.getRGB() - o2.getRGB();
      }
    });
    for(int i = startIndex; i < colors.length || i < rows * columns; ++i) {
      if (c.gridx % columns == 0) {
        c.gridx = 0;
        c.gridy++;
        if (c.gridy > rows) {
          break;
        }
      }
      if (i >= colors.length) {
        // add empty component
        jcomp = new JLabel();
      } else {
        final Color color = colors[i];
        jcomp = new JComponent() {
          {
            // setPreferredSize(new Dimension(16, 16));
            setToolTipText(String.format("R %d, G %d, B %d, A %d", color.getRed(), color.getGreen(), color.getBlue(),
                color.getAlpha()));
            setOpaque(true);
            setBackground(color);
          }

          @Override
          protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
          }
        };
        colorComps.put(jcomp, color);
        jcomp.addMouseListener(this);
        jcomp.addMouseMotionListener(this);
      }
      c.gridx++;
      jcomp.setPreferredSize(new Dimension(16, 16));
      add(jcomp, c);
    }

    // add control buttons and invalidate panel
    if (next == null) {
      next = new JButton("Next");
      next.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          if (index + rows * columns < palette.size()) {
            index += rows * columns;
            System.out.println(index);
            SwingUtilities.invokeLater(new Runnable() {
              @Override
              public void run() {
                showColors(index);
              }
            });
          }
        }
      });
    }
    if (back == null) {
      back = new JButton("Back");
      back.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          if (index == 0) {
            return; // don't need to do anything already at the
            // beginning
          }

          if (index - rows * columns < 0) {
            index = 0;
          } else {
            index -= rows * columns;
          }
          SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
              showColors(index);
            }
          });
        }
      });
    }

    if (remove == null) {
      remove = new JButton("Remove");
      remove.addActionListener((a) -> {
        palette.removeColor(colorComps.get(selected));
      });
      remove.setEnabled(false);
    }
    c.gridwidth = columns / 2;
    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.LINE_START;
    c.gridx = 0;
    c.gridy++;
    add(back, c);

    c.gridwidth = columns / 2;
    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.CENTER;
    c.gridx = columns / 2 - 1;
    add(remove, c);

    c.gridwidth = GridBagConstraints.REMAINDER;
    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.LINE_END;
    c.gridx = columns / 2;
    add(next, c);
    this.validate();
    this.repaint();
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    final JComponent comp = (JComponent) e.getSource();
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        firePropertyChange(COLOR_SELECTED, null, comp.getBackground());
      }
    });
  }

  @Override
  public void mousePressed(MouseEvent e) {

  }

  @Override
  public void mouseReleased(MouseEvent e) {
    JComponent comp = (JComponent) e.getSource();
    comp.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
    if(selected != null && comp != selected) {
      selected.setBorder(null);
    }
    this.selected = comp;
    this.remove.setEnabled(true);
  }

  @Override
  public void mouseEntered(MouseEvent e) {
    JComponent comp = (JComponent) e.getSource();
    comp.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
  }

  @Override
  public void mouseExited(MouseEvent e) {
    JComponent comp = (JComponent) e.getSource();
    if(this.selected != comp) {
      comp.setBorder(null);
    }
  }

  @Override
  public void mouseDragged(MouseEvent e) {

  }

  @Override
  public void mouseMoved(MouseEvent e) {

  }

  @Override
  public void colorAdded(ColorPalette palette, Color color) {
    this.showColors(index);
  }

  @Override
  public void colorRemoved(ColorPalette palette, Color color) {
    this.showColors(index);
  }
}
