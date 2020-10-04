package com.pixel.painter.settings;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

public class Settings {

  public void addRecentFile(File file) {
    if(!this.configs.hasKey("recents")) {
       this.configs.putStringArray("recents", new LinkedList<String>());
    }
    String[] stringArray = this.configs.getStringArray("recents");
    List<String> recents = new LinkedList<String>();
    recents.add(file.getAbsolutePath());
    for(String fileString : stringArray) {
      if(recents.size() <= MAX_RECENT_FILES && !fileString.equals(file.getAbsolutePath())) {
        recents.add(fileString);
      }
    }
    this.configs.putStringArray("recents", recents);
  }

  public File[] getRecentFiles() {
    String[] stringArray = this.configs.getStringArray("recents");
    List<File> asFiles = new LinkedList<File>();

    if (stringArray != null) {
      for (String recent : stringArray) {
        asFiles.add(new File(recent));
      }
    }

    return asFiles.toArray(new File[asFiles.size()]);
  }

  public File settingsDir() {
    if (settingsDir == null) {
      settingsDir = new File(System.getProperty("user.home") + "/.PixelPainter");
      if (!settingsDir.exists()) {
        if (settingsDir.mkdirs()) {
          throw new SettingsException(
              "Could not make directories for settings files: " + settingsDir.getAbsolutePath());
        }
      }
    }

    return settingsDir;
  }

  public static synchronized Settings getInstance() {
    if (instance == null) {
      instance = new Settings();

      Runtime.getRuntime().addShutdownHook(new Thread() {
        public void run() {
          instance.save();
        }
      });
    }
    return instance;
  }

  protected void save() {
    try (FileWriter fw = new FileWriter(configFile())) {
      fw.write(configs.toJson());
    } catch (IOException e) {
      throw new SettingsException("Error writing to config file", e);
    }

  }

  private void load() {
    configs = Json.parseFileObject(configFile(), "{}");
  }

  private File configFile() {
    if (configFile == null) {
      configFile = new File(settingsDir(), "config.json");
      if (!configFile.exists()) {
        try {
          if (!configFile.createNewFile()) {
            throw new SettingsException("Could not create new config file: " + configFile);
          }
        } catch (IOException ioe) {
          throw new SettingsException("Could not create new config file: " + configFile, ioe);
        }
      }
    }

    return configFile;
  }

  private Settings() {
    load();
  }

  private static Settings instance;
  private File            settingsDir;
  private File            configFile;
  private Json.JsonObject configs;
  
  private static final short MAX_RECENT_FILES = 10;

}
