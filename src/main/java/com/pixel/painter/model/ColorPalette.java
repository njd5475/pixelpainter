package com.pixel.painter.model;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pixel.painter.palettes.PaletteChangeListener;

public class ColorPalette {

  private final Set<Color>                 colors;
  private final Set<PaletteChangeListener> listeners;

  public ColorPalette() {
    colors    = new LinkedHashSet<Color>();
    listeners = new HashSet<>();
  }

  public Color[] getColors() {
    return colors.toArray(new Color[colors.size()]);
  }

  public void addColor(Color color) {
    colors.add(color);
    for (PaletteChangeListener l : listeners) {
      l.colorAdded(this, color);
    }
  }

  public void removeColor(Color color) {
    boolean hadColor = colors.remove(color);
    if(hadColor) {
      for (PaletteChangeListener l : listeners) {
        l.colorRemoved(this, color);
      }
    }
  }

  public static ColorPalette createFromImage(BufferedImage image) {
    ColorPalette palette = new ColorPalette();
    // find all unique colors
    int width = image.getWidth(), y;
    for (int i = 0; i < width * image.getHeight(); ++i) {
      y = i / width;
      palette.addColor(new Color(image.getRGB(i - y * width, y), true));
    }
    return palette;
  }

  public int size() {
    return colors.size();
  }

  public static ColorPalette createFrom(Color... newColors) {
    ColorPalette cp = new ColorPalette();
    cp.colors.addAll(Arrays.asList(newColors));
    return cp;
  }

  private static final Pattern PAL = Pattern.compile("\\s*(\\d+)\\s+(\\d+)\\s+(\\d+)(\\s+(\\d+))?\\s*");

  public static ColorPalette createFromPal(InputStream stream) throws IOException {
    ColorPalette   palette = new ColorPalette();
    BufferedReader br      = new BufferedReader(new InputStreamReader(stream));
    String         format  = br.readLine();
    String         version = br.readLine();
    String         count   = br.readLine();
    // should be of the format digit sequences
    String  line = null;
    Matcher m;
    while ((line = br.readLine()) != null) {
      m = PAL.matcher(line);
      if(m.matches()) {
        int r, g, b, a=255;
        r = Integer.parseInt(m.group(1));
        g = Integer.parseInt(m.group(2));
        b = Integer.parseInt(m.group(3));
        if(m.groupCount() > 3) {
          String alpha = m.group(4);
          if(alpha != null && alpha.trim().length() > 0) {
            try {
              a = Integer.parseInt(alpha.trim());
            } catch (NumberFormatException nfe) {
              a = 255;
            }
          }
        } else {
          a = 255;
        }
        palette.addColor(new Color(r, g, b, a));
      }
    }
    return palette;
  }

  public void addChangeListener(PaletteChangeListener l) {
    listeners.add(l);
  }

  public void removeChangeListener(PaletteChangeListener l) {
    listeners.remove(l);
  }
}
