package review;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Review {
    @Id
    private String imdbID;
    private String title;
    private String year;
    private String runtime;
    private String imdbRating;
    private String metascore;
    private String imdbVotes;
    private String type;
    private String genre;
    private String plot;
    private String boxOffice;
    private String director;

    @Override
    public String toString() {
        return  "" + title + "{" +
                ", year='" + year + '\'' +
                ", runtime='" + runtime + '\'' +
                ", imdbRating='" + imdbRating + '\'' +
                ", metascore='" + metascore + '\'' +
                ", imdbVotes='" + imdbVotes + '\'' +
                ", type='" + type + '\'' +
                ", genre='" + genre + '\'' +
                ", plot='" + plot + '\'' +
                '}';
    }

    public void setImdbID(String imdbID) {
        this.imdbID = imdbID;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private static String getHTML(String urlToRead) throws Exception {
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
        String jsonReview  = getHTML("http://www.omdbapi.com/?apikey=" + Application.getAPIKey() + "&t=" + URLEncoder.encode(title, "UTF-8"));
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        return objectMapper.readValue(jsonReview, Review.class);
    }


    public String getGenre() {
        return genre;
    }

    public String getPlot() {
        return plot;
    }

    public String getTitle() {
        return title;
    }

    public String getYear() {
        return year;
    }

    public Integer safeGetYear() {
        return Integer.parseInt(year.split("–")[0]);
    }

    public String getRuntime() {
        return runtime;
    }

    public Integer safeGetRuntime() {
        return Integer.parseInt(runtime.replace(" min", "").replace("N/A","0"));
    }

    public String getImdbRating() {
        return imdbRating;
    }

    public Integer safeGetImdbRating() {
        return Integer.parseInt(imdbRating.replace(".","").replace("N/A",""));
    }

    public String getImdbVotes() {
        return imdbVotes;
    }

    public Integer safeGetImdbVotes() {
        return Integer.parseInt(imdbVotes.replace(",","").replace("N/A",""));
    }

    public String getMetascore() {
        return metascore;
    }

    public Integer safeGetMetascore() {
        return Integer.parseInt(metascore.replace("N/A","0"));
    }

    public String getType() {
        return type;
    }

    public String getImdbID() {
        return imdbID;
    }

    public String getBoxOffice() {
        return boxOffice;
    }

    public String getDirector() {
        return director;
    }
}
