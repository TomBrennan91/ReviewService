package io.brennan.unogs;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.brennan.Utilities;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;

public class Unogs {

  private String title;
  private String image;
  private String imdbId;
  private String type;
  private String poster;
  private String netflixId;



  public Unogs[] getExpiring() throws IOException {
    HttpClient client = HttpClients.createDefault();
    HttpGet query = new HttpGet("");
    HttpResponse response = client.execute(query);
    String responseString = Utilities.transformResponseToString(response);
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readValue(responseString, Unogs[].class);
  }

}

