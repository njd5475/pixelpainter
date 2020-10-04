package com.pixel.painter.ui.overlays;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JToolBar;

import com.pixel.painter.controller.ImageController;
import com.pixel.painter.ui.PixelPainter;

public class Overlay {

  private static final int     menuAlpha            = 225;
  protected static final Color SLIDEMENU_BACKGROUND = new Color(50, 50, 50, menuAlpha);
  private final JToolBar       toolbar;
  protected final Color        background;
  protected int                mouseX;
  protected int                mouseY;
  protected boolean            performMouseOp;
  protected PixelPainter       pp;
  protected int                width;
  private int                  height;
  protected int                screenWidth;
  protected int                screenHeight;

  public Overlay(JToolBar toolbar, PixelPainter pp) {
    background = new Color(0, 0, 0, 100);

    this.toolbar = toolbar;
    this.pp = pp;
  }

  protected final JToolBar getToolBar() {
    return toolbar;
  }
  
  public void render(Graphics2D init, int width, int height) {
    // save off the width and height for other functions
    this.width = width;
    this.height = height;
    this.screenHeight = height;
    this.screenWidth = width;

    Graphics2D g = (Graphics2D) init.create();

    g.dispose();
  }

  protected void drawPlusButton(Graphics2D g, Rectangle2D.Double rect) {
    // also draw a box for adding new colors
    boolean highlight = rect.contains(new Point2D.Double(mouseX, mouseY));
    g.setColor(Color.lightGray);
    Graphics2D tmpG = (Graphics2D) g.create();
    String plus = "\uf0fe";
    tmpG.setFont(PixelPainter.getFontAwesome());
    if (highlight) {
      tmpG.setColor(Color.LIGHT_GRAY.brighter());
    }
    tmpG.drawString(plus, (float) (rect.getX()), (float) (rect.getMaxY() - rect.height / 2));
    tmpG.dispose();
  }

  public void mouseMoved(MouseEvent e) {
    mouseX = e.getX();
    mouseY = e.getY();
  }

  public void mouseReleased(MouseEvent e) {
    performMouseOp = true;
  }

  public void mousePressed(MouseEvent e) {

  }

  public void keyPressed(KeyEvent e) {

  }

  public void keyReleased(KeyEvent e) {

  }

  public void keyTyped(KeyEvent e) {

  }

  public void mouseWheel(MouseWheelEvent mwe) {

  }

  public boolean isInside(Point point) {

    return false;
  }

  public int getScreenWidth() {
    return screenWidth;
  }

  public int getScreenHeight() {
    return screenHeight;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }
}
