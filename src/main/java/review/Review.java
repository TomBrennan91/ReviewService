package review;



import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Comparator;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Review {

    private String title;
    private String year;
    private String runtime;
    private String imdbRating;
    private String metascore;
    private String imdbVotes;
    private String type;

    @Override
    public String toString() {
        return "Review{" +
                "title='" + title + '\'' +
                ", year='" + year + '\'' +
                ", runtime='" + runtime + '\'' +
                ", imdbRating='" + imdbRating + '\'' +
                ", metascore='" + metascore + '\'' +
                ", imdbVotes='" + imdbVotes + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public String getYear() {
        return year;
    }

    public String getRuntime() {
        return runtime;
    }

    public String getImdbRating() {
        return imdbRating;
    }

    public String getImdbVotes() {
        return imdbVotes;
    }

    public static String getHTML(String urlToRead) throws Exception {
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

    public static Review getReviewFromTitle(String title) throws Exception{
        String jsonReview  = getHTML("http://www.omdbapi.com/?apikey=714ddca5&t=" + URLEncoder.encode(title, "UTF-8"));
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        return objectMapper.readValue(jsonReview, Review.class);
    }

}
