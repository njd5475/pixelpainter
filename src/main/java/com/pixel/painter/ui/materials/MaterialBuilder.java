package com.pixel.painter.ui.materials;

import java.awt.Color;

import com.pixel.painter.ui.materials.MaterialBuilder.AlignMode;

public interface MaterialBuilder {

  public enum AlignMode {
    INSIDE, OUTSIDE
  }

  Material build(String name);

  MaterialBuilder origin();

  MaterialBuilder onState(String state);

  MaterialBuilder text(String text, Color black);

  MaterialBuilder background(Color background);

  MaterialBuilder subtractBorder(int north, int south, int east, int west);

  MaterialBuilder above(String name);

  MaterialBuilder right(float percentage);

  MaterialBuilder left(float percentage);
  
  MaterialBuilder leftOf(String name, AlignMode mode);

  MaterialBuilder snapToRight();

  MaterialBuilder percentage(int outOf100);
  
  MaterialBuilder fixedSize(int width, int height);

  MaterialBuilder center(String name);

  MaterialBuilder handleMouseUp(MaterialActionHandler handler);

  MaterialBuilder handleMouseMove(MaterialActionHandler handler);

  MaterialBuilder handleMouseOut(MaterialActionHandler handler);

  MaterialBuilder push();

  MaterialBuilder roundedClip(int arcW, int arcH);

  MaterialContainerBuilder container(String... names);

  Material get(String name);

  MaterialBuilder set(String name);

  void put(String name, Material containerM);

  MaterialBuilder minimumSize(int width, int height);

  Material getRoot();

  MaterialBuilder addColorProperty(String string, Color color);

  MaterialBuilder image(MaterialBuilderDrawFunc object);
  
  MaterialBuilder shrinkToText(String str);

}