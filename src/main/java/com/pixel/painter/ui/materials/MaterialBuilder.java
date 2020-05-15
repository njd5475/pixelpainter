package com.pixel.painter.ui.materials;

import java.awt.Color;

public interface MaterialBuilder {

  Material build(String name);

  MaterialBuilder origin();

  MaterialBuilder onState(String state);

  MaterialBuilder text(String text, Color black);
  
  MaterialBuilder background(Color background);

  MaterialBuilder subtractBorder(int north, int south, int east, int west);

  MaterialBuilder right(float percentage);
  
  MaterialBuilder left(float percentage);
  
  MaterialBuilder snapToRight();

  MaterialBuilder fixedSize(int width, int height);

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

}