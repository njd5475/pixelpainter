package com.pixel.painter.rest;

import java.io.ByteArrayInputStream;
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
      StringBuilder sb = new StringBuilder();
      byte buf[] = new byte[1024];
      while(inputStream.available() > 0) {
        int readCount = inputStream.read(buf);
        sb.append(new String(buf, 0, readCount));
      }
      System.out.println(sb.toString());
      Map<String, Object> parse       = Json.parse(new ByteArrayInputStream(sb.toString().getBytes()));
      return parse;
    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public Map<String, Object> dig(String keys, Map<String, Object> obj) {
    String[] allKeys = keys.split(".");
    int      i       = 0;
    Object   cur     = obj.get(allKeys[i++]);
    while (i < allKeys.length) {
      if(cur instanceof Map) {

        Map<String, Object> tmpObj = (Map<String, Object>) cur;
        if(tmpObj.containsKey(allKeys[i])) {
          cur = tmpObj.get(allKeys[i++]);
        } else {
          throw new IllegalArgumentException("Could not find " + keys + " stopped at " + allKeys[i]);
        }
      }
    }
    return (Map<String, Object>) cur;
  }

  public static void main(String... args) {
//    Map<String, Object>       response = RestCalls.makeRestCall(
//        "https://lospec.com/palette-list/load?colorNumberFilterType=any&colorNumber=14&page=0&tag=&sortingType=default");
//    List<Map<String, Object>> palettes = (List<Map<String, Object>>) response.get("palettes");
//    System.out.println(palettes);
    String test = "{\n" + 
        "  \"shopad\": \"<a class=\\\"promo-box\\\" href=\\\"/shop/lethal-lava-land\\\" target=\\\"_blank\\\" content=\\\"nofollow\\\">\\n  \\n  <div class=\\\"label\\\">Lospec Shop Product</div>\\n  \\n  <div class=\\\"title\\\">Lethal Lava Land by SnugBoat</div>\\n  \\n  <div class=\\\"buttons\\\">\\n    <div class=\\\"info\\\"><svg width=\\\"16\\\" height=\\\"16\\\" viewBox=\\\"0 0 1792 1792\\\"><path d=\\\"M1024 1376v-192q0-14-9-23t-23-9h-192q-14 0-23 9t-9 23v192q0 14 9 23t23 9h192q14 0 23-9t9-23zm256-672q0-88-55.5-163t-138.5-116-170-41q-243 0-371 213-15 24 8 42l132 100q7 6 19 6 16 0 25-12 53-68 86-92 34-24 86-24 48 0 85.5 26t37.5 59q0 38-20 61t-68 45q-63 28-115.5 86.5t-52.5 125.5v36q0 14 9 23t23 9h192q14 0 23-9t9-23q0-19 21.5-49.5t54.5-49.5q32-18 49-28.5t46-35 44.5-48 28-60.5 12.5-81zm384 192q0 209-103 385.5t-279.5 279.5-385.5 103-385.5-103-279.5-279.5-103-385.5 103-385.5 279.5-279.5 385.5-103 385.5 103 279.5 279.5 103 385.5z\\\" fill=\\\"#fff\\\"/></svg></div><div class=\\\"close\\\"><svg width=\\\"16\\\" height=\\\"16\\\" viewBox=\\\"0 0 1792 1792\\\">X<path d=\\\"M1490 1322q0 40-28 68l-136 136q-28 28-68 28t-68-28l-294-294-294 294q-28 28-68 28t-68-28l-136-136q-28-28-28-68t28-68l294-294-294-294q-28-28-28-68t28-68l136-136q28-28 68-28t68 28l294 294 294-294q28-28 68-28t68 28l136 136q28 28 28 68t-28 68l-294 294 294 294q28 28 28 68z\\\" fill=\\\"#fff\\\"/></svg></div>\\n  </div>\\n  \\n  <img src=\\\"/shop/lethal-lava-land/thumbnail.png\\\" />\\n\\n  <div class=\\\"call-to-action\\\">\\n    <div>Buy on the Lospec Shop <svg width=\\\"1792\\\" height=\\\"1792\\\" viewBox=\\\"788 460 200 1000\\\"><path d=\\\"M1171 960q0 13-10 23l-466 466q-10 10-23 10t-23-10l-50-50q-10-10-10-23t10-23l393-393-393-393q-10-10-10-23t10-23l50-50q10-10 23-10t23 10l466 466q10 10 10 23z\\\" fill=\\\"#fff\\\"/></svg></div>\\n  </div>\\n  \\n</a>\"\n" + 
        "}";
    Map<String, Object> parse = Json.parse(test);
  }
}
