package com.pixel.painter.model;

import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import com.pixel.painter.ui.PixelPainter.FILE_CHOOSER;

public final class ApplicationSettings {

  private static ApplicationSettings instance;
  private final File                 settingsDirectory;
  private File                       settingsFile;
  private final Properties           properties;

  protected ApplicationSettings() {
    properties = new Properties();

    // load defaults.
    loadDefaults();

    settingsDirectory = new File(System.getProperty("user.home"),
        ".PixelPainter/");
    if (!settingsDirectory.exists()) {
      if (!settingsDirectory.mkdir()) {
        System.err.println("Could not create application settings directory!");
      }
    }

    if (settingsDirectory.exists()) {
      settingsFile = new File(settingsDirectory, "settings.properties");
      if (!settingsFile.exists()) {
        try {
          if (!settingsFile.createNewFile()) {
            System.err.println("Could not create settings file!");
          }
        } catch (IOException e) {
          e.printStackTrace();
        }

        // save the defaults to a new file.
        saveProperties();
      } else {
        try {
          properties.loadFromXML(new FileInputStream(settingsFile));
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        ApplicationSettings.instance.saveProperties();
      }
    });
  }

  private void saveProperties() {
    if (settingsFile.exists()) {
      stringifyAllProps();
      try {
        properties.storeToXML(new FileOutputStream(settingsFile),
            "Application Settings last updated on "
                + System.currentTimeMillis());
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private void stringifyAllProps() {
    for (Object key : properties.keySet()) {
      properties.put(key.toString(), properties.get(key).toString());
    }
  }

  private void loadDefaults() {
    properties.put("WIDTH", "640");
    properties.put("HEIGHT", "480");
    properties.put("DEFAULT_IMAGE_SIZE_SET", false);
    properties.put(FILE_CHOOSER.OPEN_IMAGE.name(), new File("."));
    properties.put(FILE_CHOOSER.SAVE_IMAGE.name(), new File("."));
  }

  public int getWidth() {
    return getInt("WIDTH");
  }

  public int getHeight() {
    return getInt("HEIGHT");
  }

  public boolean isDefaultImageSizeSet() {
    return getBool("DEFAULT_IMAGE_SIZE_SET");
  }

  private boolean getBool(String key) {
    Object o = properties.get(key);
    if (o != null) {
      if (o instanceof Boolean) {
        return (boolean) o;
      } else if (o instanceof String) {
        return Boolean.parseBoolean(o.toString());
      }
    }
    return false;
  }

  public File getFileChooserDirectory(FILE_CHOOSER id) {
    return loadFileProperty(id.name());
  }

  public void setFileChooserDirectory(FILE_CHOOSER id, File file) {
    properties.put(id.name(), file);
  }

  private int getInt(String key) {
    Object o = properties.get(key);
    if (o instanceof Integer) {
      return (Integer) o;
    } else if (o instanceof String) {
      String val = (String) o;
      return Integer.parseInt(val);
    }
    return -1;
  }

  public File getDirectory() {
    return settingsDirectory;
  }

  public File getSettingsFile() {
    return settingsFile;
  }

  public static ApplicationSettings getInstance() {
    if (instance == null) {
      instance = new ApplicationSettings();
      Runtime.getRuntime().addShutdownHook(new Thread() {
        @Override
        public void run() {
          instance.saveProperties();
        }
      });
    }

    return instance;
  }

  private File loadFileProperty(String key) {
    Object o = properties.get(key);
    if (o instanceof File) {
      return (File) o;
    } else if (o instanceof String) {
      return new File((String) o);
    }
    return null;
  }

  public void setDefaultImageSize(int width, int height) {
    properties.put("DEFAULT_IMAGE_SIZE_SET", true);
    properties.put("DEFAULT_IMAGE_WIDTH", width);
    properties.put("DEFAULT_IMAGE_HEIGHT", height);
  }
  
  public Dimension getDefaultImageSize() {
    Dimension d = new Dimension(getInt("DEFAULT_IMAGE_WIDTH"),getInt("DEFAULT_IMAGE_HEIGHT"));
    if(d.width <= 0 || d.height <= 0) {
      setDefaultImageSize(32, 32);
      return new Dimension(32,32);
    }
    return d;
  }

  // Unit Test don't delete
  // public static void main(String... args) {
  // final ApplicationSettings settings = ApplicationSettings.getInstance();
  // if (!settings.getDirectory().exists()) {
  // System.err.println("No settings directory!");
  // }
  // if (!settings.getSettingsFile().exists()) {
  // System.err.println("No settings file!");
  // }
  // if(settings.getWidth() != 640) {
  // System.err.println("Width is wrong");
  // }
  // if(settings.getHeight() != 480) {
  // System.err.println("Height is wrong");
  // }
  // System.out.println("Test Finished");
  // }
}
