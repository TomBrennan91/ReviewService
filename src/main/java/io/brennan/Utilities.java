package io.brennan;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public interface Utilities {

  static String getHTML(String urlToRead) throws IOException {
    StringBuilder result = new StringBuilder();
    URL url = new URL(urlToRead);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("GET");
    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    String line;
    while ((line = rd.readLine()) != null) {
      result.append(line);
    }
    rd.close();
    return result.toString();
  }

  static String transformResponseToString(HttpResponse response) throws IOException {
    HttpEntity entity = response.getEntity();
    String responseString = "";
    if (entity != null) {
      try (InputStream inStream = entity.getContent()) {
        responseString = parseStream(inStream);
      }
    }
    return responseString;
  }

  static String parseStream(InputStream inputStream) throws IOException{
    StringBuilder stringBuilder = new StringBuilder();
    String line;
    try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
      while ((line = bufferedReader.readLine()) != null) {
        stringBuilder.append(line);
      }
    }
    return stringBuilder.toString();
  }

  static BufferedReader fromFile(String filePath, Class myClass){
    InputStream in = myClass.getResourceAsStream(filePath);
    return new BufferedReader(new InputStreamReader(in));
  }

}
