package com.pixel.painter.ui.overlays.colorbar;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JToolBar;

import com.pixel.painter.brushes.Brush;
import com.pixel.painter.brushes.Brush.BrushAction;
import com.pixel.painter.controller.ImageController;
import com.pixel.painter.model.ColorPalette;
import com.pixel.painter.ui.PixelPainter;
import com.pixel.painter.ui.materials.Material;
import com.pixel.painter.ui.overlays.Overlay;

public class ColorBarOverlay extends Overlay {

  private static final int              FROM_RIGHT_SIDE          = 3;
  private static final int              OVERLAY_WIDTH            = 38;
  private static final int              BUTTON_SEPARATION_X      = 2;
  private static final int              BUTTON_SEPARATION_COLUMN = 10;
  private Color                         highlightedColor;
  private Color                         selected;
  private ColorButton                   buttonHighlighted;
  private final Set<Color>              colors;
  private Map<Color, List<ColorButton>> selectedVariants         = new HashMap<>();
  
  public ColorBarOverlay(JToolBar toolbar, PixelPainter pp, ColorPalette palette) {
    super(toolbar, pp);
    colors = new LinkedHashSet<>(Arrays.asList(palette.getColors()));
    buildColorButtons();
  }

  @Override
  public int getWidth() {
    return (OVERLAY_WIDTH) * getNumberOfColumns();
  }

  @Override
  public int getHeight() {
    return getScreenHeight() - getY();
  }

  private void buildColorButtons() {
    int numVariants = 10;
    int y           = 0;
    int slideWidth  = BUTTON_SEPARATION_X + (numVariants - 1) * (getButtonWidth() + BUTTON_SEPARATION_X);
    int x           = FROM_RIGHT_SIDE;

    for (Color c : colors) {
      if (y + 35 >= getHeight()) {
        y = 0;
        x = FROM_RIGHT_SIDE;
      }

      List<ColorButton> buts = buildColorButtonRibbon(x, y, c, numVariants);
      selectedVariants.put(c, buts);
      y += 35;
    }
  }

  private List<ColorButton> buildColorButtonRibbon(int x, int y, Color c, int numVariants) {
    List<ColorButton> buttons = new LinkedList<>();
    buttons.add(new ColorButton(x, y, c, this));
    return buttons;
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    super.mouseMoved(e);
    if (selectedVariants.containsKey(highlightedColor)) {
      for (ColorButton b : selectedVariants.get(highlightedColor)) {
        if (b.contains(e.getX(), e.getY())) {
          if (this.buttonHighlighted != null) {
            this.buttonHighlighted.unhighlight();
          }
          b.highlight();
          this.buttonHighlighted = b;
        }
      }
    }
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    super.mouseReleased(e);
    if (selectedVariants.containsKey(highlightedColor)) {
      if (this.buttonHighlighted != null && this.buttonHighlighted.contains(e.getX(), e.getY())) {
        this.buttonHighlighted.apply();
        e.consume();
      }
    }
  }

  @Override
  public void mousePressed(MouseEvent e) {
    super.mouseReleased(e);
    if (selectedVariants.containsKey(highlightedColor)) {
      if (this.buttonHighlighted != null && this.buttonHighlighted.contains(e.getX(), e.getY())) {
        e.consume();
      }
    }
  }

  public void addSelectedBrush() {
    addSelectedBrush(selected);
  }

  public void addSelectedBrush(Color color) {
    ImageController ctrl = pp.getImageController();
    Brush      brush   = ctrl.createColorBrush(color);
    JToolBar   toolbar = getToolBar();
    Set<Brush> brushes = new HashSet<Brush>();
    for (Component c : toolbar.getComponents()) {
      if (c instanceof JButton) {
        JButton but = (JButton) c;
        if (but.getAction() instanceof BrushAction) {
          BrushAction b = (BrushAction) but.getAction();
          brushes.add(b.getBrush());
        }
      }
    }
    if (!brushes.contains(brush)) {
      JButton but = toolbar.add(brush.createAsAction(pp));
      but.setPreferredSize(PixelPainter.toolButtonSize);
    }
    toolbar.invalidate();
    toolbar.repaint();

    ctrl.setBrush(brush);
    ctrl.setFillColor(color);
  }

  @Override
  public void render(Graphics2D init, int width, int height) {
    if (this.screenWidth != width || this.screenHeight != height) {
      buildColorButtons();
    }

    this.screenHeight = height;
    this.screenWidth = width;

    drawColorBar(init, width, height);
    
  }

  private void drawColorBar(Graphics2D init, int width, int height) {
    Graphics2D g = (Graphics2D) init.create();
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    int buttonWidth = this.getButtonWidth();
    int y = 0;

    g.translate(this.getX(), this.getY());
    
    g.setColor(new Color(200,200,200,100));
    g.fillRect(0, 0, buttonWidth + BUTTON_SEPARATION_COLUMN, this.getHeight());
    
    drawTrashButton(g, new Rectangle2D.Double(0, y, buttonWidth, buttonWidth));

    g.dispose();
  }

  private void drawTrashButton(Graphics2D g, Double double1) {
    g.setFont(PixelPainter.getFontAwesome());
    String trash    = "\uf2ed";

    float  strWidth = g.getFontMetrics().stringWidth(trash);
    g.setColor(Color.white);
    g.drawString(trash, (float) (double1.getX() + double1.getWidth() / 2 - strWidth / 2), (float) (double1.getMaxY()));
  }

  protected void setSelectedColor(Color color) {
    this.selected = color;
  }

  protected void drawRGBHint(Graphics2D g2d, int x, int y, Color color) {
    // determine if we are at the edge and if so calculate the start x,
    // such that the hint does not go beyond the edge of the window.
    String rgb  = String.format("R(%d) G(%d) B(%d)", color.getRed(), color.getGreen(), color.getBlue());
    int    strW = g2d.getFontMetrics().stringWidth(rgb);
    x = Math.min(x, width - strW);

    g2d.setColor(SLIDEMENU_BACKGROUND);
    g2d.fillRect(x, y - 20, strW, 20);
    g2d.setColor(Color.white);
    g2d.drawString(rgb, x, y - 5);
  }

  public int getX() {
    return getScreenWidth() - (getNumberOfColumns() * OVERLAY_WIDTH) - FROM_RIGHT_SIDE;
  }

  public int getY() {
    return 55;
  }

  public int getButtonWidth() {
    return 25;
  }

  public int getButtonHeight() {
    return 25;
  }

  public int getNumberOfColumns() {
    int overlayWidth = (colors.size() * (getButtonHeight() + BUTTON_SEPARATION_COLUMN));
    return Math.max(1, (int) Math.ceil(overlayWidth / (double) getHeight()));
  }

}
