package com.pixel.painter.palettes;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.pixel.painter.model.ColorPalette;
import com.pixel.painter.rest.RestCalls;

public class RemotePalettes {

  public static enum COLOR_NUMBER_FILTER_TYPE { ANY, MIN, MAX, EXACT };
  public static enum SORTING_TYPES { ALPHABETICAL, DOWNLOADS, DEFAULT, NEWEST };
  
	
  public static ColorPalette[] retreive(int numOfColors, COLOR_NUMBER_FILTER_TYPE colorNumFilter, SORTING_TYPES sortFilter, int page) {
    String tag = "";
    String numberFilter = colorNumFilter.name().toLowerCase();
    String sortingType = sortFilter.name().toLowerCase();
    String params = String.format("colorNumberFilterType=%s&colorNumber=%d&page=%d&tag=%s&sortingType=%s", numberFilter, numOfColors, page, tag, sortingType);
    String makeCall = String.format("https://lospec.com/palette-list/load?%s", params);
   
    Map<String, Object> response = RestCalls.makeRestCall(makeCall);
    List<Map<String, Object>> palettesObj = (List<Map<String, Object>>) response.get("palettes");
    List<ColorPalette> palettes = new LinkedList<>();

    if (palettesObj != null) {
      for (Map<String, Object> pal : palettesObj) {
        String name = pal.get("title").toString();
        if (pal.get("name") != null && name == null) {
          name = pal.get("name").toString();
        }
        List<String> colorArray = (List<String>) pal.get("colorsArray");
        List<Color> colors = new LinkedList<>();

        for (String hex : colorArray) {
          try {
            if(hex.startsWith("0x")) {
              colors.add(Color.decode(hex));
            }else {
              colors.add(Color.decode("0x" + hex));
            }
          }catch(NumberFormatException nfe) {
            System.out.format("Could not parse color %s\n", hex);
          }
        }

        ColorPalette cp = new ColorPalette(name);
        cp.addColors(colors);
        palettes.add(cp);
      }

      return palettes.toArray(new ColorPalette[palettes.size()]);
    }

    return new ColorPalette[0];
  }
}
