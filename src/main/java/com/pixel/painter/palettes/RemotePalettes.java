package com.pixel.painter.palettes;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.pixel.painter.model.ColorPalette;
import com.pixel.painter.rest.RestCalls;

public class RemotePalettes {

  public static ColorPalette[] retreive() {
    Map<String, Object>       response    = RestCalls.makeRestCall(
        "https://lospec.com/palette-list/load?colorNumberFilterType=any&colorNumber=14&page=0&tag=&sortingType=default");
    List<Map<String, Object>> palettesObj = (List<Map<String, Object>>) response.get("palettes");
    List<ColorPalette>        palettes    = new LinkedList<>();

    for (Map<String, Object> pal : palettesObj) {
    	String name = pal.get("title").toString();
    	if(pal.get("name") != null && name == null) {
    	    name = pal.get("name").toString();
    	}
      List<String> colorArray = (List<String>) pal.get("colorsArray");
      List<Color>  colors     = new LinkedList<>();

      for (String hex : colorArray) {
        colors.add(Color.decode(hex));
      }

      ColorPalette cp = new ColorPalette(name);
      cp.addColors(colors);
      palettes.add(cp);
    }

    return palettes.toArray(new ColorPalette[palettes.size()]);
  }
}
