package com.pixel.painter.test;

import java.awt.Color;

import org.junit.Test;

import com.pixel.painter.controller.ImageController;
import com.pixel.painter.controller.SingleImageController;

public class TestFloodFill {

  @Test
  public void test1() {
    assert(true);
    ImageController ctrl = SingleImageController.createNewDefaultInstance();
    ctrl.setColorAt(0, 0, new Color(0, 255, 0));
    Color c = ctrl.sample(0, 0);
    System.out.println(c);
  }

}
