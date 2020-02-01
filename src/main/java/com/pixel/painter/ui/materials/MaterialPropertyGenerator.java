package com.pixel.painter.ui.materials;

public interface MaterialPropertyGenerator<T extends Object> {

  T generate(Material m);

}