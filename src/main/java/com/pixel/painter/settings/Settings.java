package com.pixel.painter.settings;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Settings {

  private static Settings instance;
  private File            settingsDir;
  private File            configFile;
  private Json.JsonObject configs;

  private Settings() {
    load();
  }

  private void load() {
    configs = Json.parseFileObject(configFile(), "{}");
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
    try(FileWriter fw = new FileWriter(configFile())) {
      fw.write(configs.toJson());
    } catch (IOException e) {
      throw new SettingsException("Error writing to config file", e);
    }
    
  }

  private File configFile() {
    if (configFile == null) {
      configFile = new File(settingsDir(), "config.json");
      if (!configFile.exists()) {
        try {
          if (!configFile.createNewFile()) {
            throw new SettingsException(
                "Could not create new config file: " + configFile);
          }
        } catch (IOException ioe) {
          throw new SettingsException(
              "Could not create new config file: " + configFile, ioe);
        }
      }
    }
    
    return configFile;
  }

  public File settingsDir() {
    if (settingsDir == null) {
      settingsDir = new File(
          System.getProperty("user.home") + "/.PixelPainter");
      if (!settingsDir.exists()) {
        if (settingsDir.mkdirs()) {
          throw new SettingsException(
              "Could not make directories for settings files: "
                  + settingsDir.getAbsolutePath());
        }
      }
    }

    return settingsDir;
  }

}
