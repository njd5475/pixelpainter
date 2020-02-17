package com.pixel.painter.rest;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import com.pixel.painter.settings.Json;

public class RestCalls {

  public static Map<String, Object> makeRestCall(String urlString) {
    try {
      URL           url            = new URL(urlString);
      URLConnection openConnection = url.openConnection();
      openConnection.setRequestProperty("Content-Type", "application/json");
      openConnection.setRequestProperty("User-Agent",
          "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.87 Safari/537.36");
      InputStream         inputStream = openConnection.getInputStream();
      Map<String, Object> parse       = Json.parse(inputStream);
      return parse;
    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public Map<String, Object> dig(String keys, Map<String, Object> obj) {
    String[]      allKeys = keys.split(".");
    int    i   = 0;
    Object cur = obj.get(allKeys[i++]);
    while (i < allKeys.length) {
      if(cur instanceof Map) {

        Map<String, Object> tmpObj = (Map<String, Object>) cur;
        if(tmpObj.containsKey(allKeys[i])) {
          cur = tmpObj.get(allKeys[i++]);
        }else {
          throw new IllegalArgumentException("Could not find " + keys + " stopped at " + allKeys[i]);
        }
      }
    }
    return (Map<String, Object>) cur;
  }

  public static void main(String... args) {
    Map<String, Object> response = RestCalls.makeRestCall(
        "https://lospec.com/palette-list/load?colorNumberFilterType=any&colorNumber=14&page=0&tag=&sortingType=default");
    List<Map<String, Object>>  palettes = (List<Map<String, Object>>) response.get("palettes");
    System.out.println(palettes);
  }
}
