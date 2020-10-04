package com.pixel.painter.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.Popup;
import javax.swing.PopupFactory;

import com.pixel.painter.brushes.Brush;
import com.pixel.painter.controller.ImageController;

public class ColorPopup extends AbstractAction {

  private final JColorChooser colors;
  private final PixelPainter  pp;
  private final JToolBar      toolbar;
  private final JButton       addBrush;
  private final JButton       close;
  private Popup               popup;
  private JButton             button;

  public ColorPopup(JToolBar toolBar, PixelPainter pp) {
    super("Color");
    this.toolbar = toolBar;
    addBrush = new JButton("Add");
    close = new JButton("Close");
    colors = new JColorChooser();
    this.pp = pp;
    addBrush.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        Brush brush = ColorPopup.this.pp.getImageController().createColorBrush(colors.getColor());
        toolbar.add(brush.createAsAction(ColorPopup.this.pp));
        ColorPopup.this.pp.getImageController().setBrush(brush);
      }
    });
    close.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        popup.hide();
        button.setEnabled(true);
      }
    });
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    JButton but = (JButton) e.getSource();
    this.button = but;
    but.setEnabled(false);
    JToolBar toolBar = (JToolBar) but.getParent();
    Point pt = but.getLocation();
    Point ct = but.getLocationOnScreen();
    JPanel selectPane = new JPanel();
    selectPane.setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.LINE_START;
    c.fill = GridBagConstraints.BOTH;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.weightx = 1.0;
    c.weighty = 1.0;
    c.gridy = 0;
    c.gridx = 0;
    selectPane.add(colors, c);

    c.gridwidth = 1;
    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.LINE_END;
    c.weighty = 0.0;
    selectPane.add(new JPanel(), c);

    c.insets = new Insets(3, 5, 3, 5);
    c.weightx = 0.0;
    c.gridx++;
    c.gridy++;
    selectPane.add(addBrush, c);
    c.gridx++;
    selectPane.add(close, c);

    popup = PopupFactory.getSharedInstance().getPopup(toolBar, selectPane, ct.x, pt.y + ct.y + but.getHeight());
    popup.show();
  }

}
