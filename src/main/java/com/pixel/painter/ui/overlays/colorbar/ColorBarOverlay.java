package com.pixel.painter.ui.overlays.colorbar;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JToolBar;

import com.pixel.painter.brushes.Brush;
import com.pixel.painter.controller.ImageController;
import com.pixel.painter.model.ColorPalette;
import com.pixel.painter.ui.PixelPainter;
import com.pixel.painter.ui.overlays.Overlay;

public class ColorBarOverlay extends Overlay {

  private static final int              FROM_RIGHT_SIDE   = 3;
  private static final int              OVERLAY_WIDTH     = 38;
  private static final int              BUTTON_SEPARATION = 2;
  private Color                         highlightedColor;
  private Color                         selected;
  private ColorButton                   buttonHighlighted;
  private final Set<Color>              colors;
  private Map<Color, List<ColorButton>> selectedVariants  = new HashMap<>();

  public ColorBarOverlay(JToolBar toolbar, ImageController ctrl, ColorPalette palette) {
    super(toolbar, ctrl);
    colors = new LinkedHashSet<>(Arrays.asList(palette.getColors()));
    buildColorButtons();
  }

  @Override
  public int getWidth() {
    return OVERLAY_WIDTH;
  }

  @Override
  public int getHeight() {
    return getScreenHeight() - getY();
  }

  private void buildColorButtons() {
    int numVariants = 10;
    int y           = 0;
    int slideWidth  = BUTTON_SEPARATION + numVariants * (getButtonWidth() + BUTTON_SEPARATION);

    for (Color c : colors) {
      List<ColorButton> buts = buildColorButtonRibbon(-slideWidth, y, c, numVariants);
      selectedVariants.put(c, buts);
      y += 35;
    }
  }

  private List<ColorButton> buildColorButtonRibbon(int x, int y, Color c, int numVariants) {
    List<ColorButton> buttons = new LinkedList<>();
    float[]           hsb     = new float[3];
    Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsb);

    float hue     = hsb[0];
    float sat     = hsb[1];
    float bri     = hsb[2];
    float percent = 1.0f;
    Color color   = null;

    // initial offset
    x += 3;

    for (int i = 0; i < numVariants; ++i) {
      percent -= 0.10f;
      float p = 1.0f - percent;

      color = Color.getHSBColor(hue, sat, bri * p);
      buttons.add(new ColorButton(x, y, color, this));

      // step function
      x += this.getButtonWidth() + BUTTON_SEPARATION;
    }
    return buttons;
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    super.mouseMoved(e);
    if(selectedVariants.containsKey(highlightedColor)) {
      for (ColorButton b : selectedVariants.get(highlightedColor)) {
        if(b.contains(e.getX(), e.getY())) {
          if(this.buttonHighlighted != null) {
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
    if(selectedVariants.containsKey(highlightedColor)) {
      if(this.buttonHighlighted != null && this.buttonHighlighted.contains(e.getX(), e.getY())) {
        this.buttonHighlighted.apply();
        e.consume();
      }
    }
  }

  @Override
  public void mousePressed(MouseEvent e) {
    super.mouseReleased(e);
    if(selectedVariants.containsKey(highlightedColor)) {
      if(this.buttonHighlighted != null && this.buttonHighlighted.contains(e.getX(), e.getY())) {
        e.consume();
      }
    }
  }

  public void addSelectedBrush() {
    addSelectedBrush(selected);
  }

  public void addSelectedBrush(Color color) {
    Brush    brush   = ctrl.createColorBrush(color);
    JToolBar toolbar = getToolBar();
    boolean  isDup   = false;
    for (Component c : toolbar.getComponents()) {
      if(c instanceof JButton) {
        JButton but = (JButton) c;
        if(but.getAction() instanceof Brush) {
          Brush b = (Brush) but.getAction();
          isDup = (b == brush);
        }
      }
    }
    if(!isDup) {
      JButton but = toolbar.add(brush);
      but.setPreferredSize(PixelPainter.toolButtonSize);
    }
    toolbar.invalidate();
    toolbar.repaint();

    ctrl.setBrush(brush);
    ctrl.setFillColor(color);
  }

  @Override
  public void render(Graphics2D init, int width, int height) {

    this.screenHeight = height;
    this.screenWidth  = width;

    drawColorBar(init, width, height);
  }

  private void drawColorBar(Graphics2D init, int width, int height) {
    Graphics2D g = (Graphics2D) init.create();
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    int bW          = getButtonWidth();
    int y           = 0;
    int barBorder   = 3;
    int slideBorder = 8;
    int numVariants = 10;
    int slideWidth  = numVariants * (getButtonWidth() + BUTTON_SEPARATION);

    g.setColor(background);
    g.translate(getX(), getY());
    g.fillRoundRect(-barBorder, -barBorder, getWidth(), getHeight(), 10, 10);

    for (Color c : colors) {
      boolean highlight = (new Rectangle(getX(), y + getY(), bW, bW)).contains(new Point(mouseX, mouseY));

      if(highlightedColor != null && !highlight && highlightedColor.hashCode() == c.hashCode()) {
        highlight = (new Rectangle(getX() - slideWidth, getY() + y - slideBorder / 2,
            slideWidth + OVERLAY_WIDTH + bW + slideBorder / 2, bW + slideBorder)).contains(new Point(mouseX, mouseY));
        if(!highlight) {
          highlightedColor = null;
        }
      }

      // draw horizontal selection
      if(highlight) {
        g.setColor(SLIDEMENU_BACKGROUND);
        g.fillRoundRect(-slideWidth - slideBorder/2, y - slideBorder/2, slideWidth + bW + slideBorder / 2, bW + slideBorder, 10, 10);

        // draw the actual color buttons
        List<ColorButton> buttons = this.selectedVariants.get(c);

        for (ColorButton but : buttons) {
          Graphics2D butG = (Graphics2D) g.create();
          but.draw(butG);
          butG.dispose();
        }
        // drawColorVariants(g, width - slideWidth, y, c, numVariants);

        highlightedColor = c;
      }

      g.setColor((highlightedColor == c && selected != null) ? selected : c);
      g.fillRoundRect(slideBorder / 2, y, bW, bW, 8, 8);
      if(highlight) {
        g.setColor(Color.yellow);
      } else {
        g.setColor(Color.white);
      }
      g.drawRoundRect(slideBorder / 2, y, bW, bW, 8, 8);

      y += 35;
    }

    drawTrashButton(g, new Rectangle2D.Double(0, y, bW, bW));

    g.dispose();
  }

  private void drawTrashButton(Graphics2D g, Double double1) {
    g.setFont(PixelPainter.getFontAwesome());
    String trash = "\uf2ed";

    float strWidth = g.getFontMetrics().stringWidth(trash);
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
    return getScreenWidth() - OVERLAY_WIDTH - FROM_RIGHT_SIDE;
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

}
