package io.brennan.review;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.brennan.Application;
import io.brennan.Utilities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Review {
    @Id
    private Integer id;

    @Transient
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
    @Transient
    private List<Rating> ratings;
    private String rottenTomatoesRating;
    private String production;
    private String language;
    private String country;
    private String poster;
    private String website;
    private String rated;
    private String totalSeasons;
    private Boolean onList;



    public static Review getReviewFromTitle(String title, String year) throws IOException {
        String jsonReview;
        if (year.equals("now")){
            Integer thisYear = LocalDateTime.now().getYear();
            year = thisYear.toString();
            jsonReview  = Utilities.getHTML("http://www.omdbapi.com/?apikey=" + Application.getAPIKey() + "&t=" + URLEncoder.encode(title, "UTF-8") + "&y=" + year);
        } else {
            jsonReview  = Utilities.getHTML("http://www.omdbapi.com/?apikey=" + Application.getAPIKey() + "&t=" + URLEncoder.encode(title, "UTF-8"));
        }
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        Review review = objectMapper.readValue(jsonReview, Review.class);
        review.extractID();
        review.extractRottenTomatoesRating();
        return review;
    }

    public Integer safeGetYear() {
        return Integer.parseInt(year.split("–")[0]);
    }
    public Integer safeGetRuntime() {
        return Integer.parseInt(runtime.replace(" min", "").replace("N/A","0"));
    }
    public Integer safeGetImdbRating() {
        return Integer.parseInt(imdbRating.replace(".","").replace("N/A",""));
    }
    public Integer safeGetImdbVotes() {
        return Integer.parseInt(imdbVotes.replace(",","").replace("N/A",""));
    }
    public Integer safeGetMetascore() {
        return Integer.parseInt(metascore.replace("N/A","0"));
    }
    public Integer safeGetRT() {
        return Integer.parseInt(rottenTomatoesRating.replace("%","").replace("N/A","0"));
    }

    private void extractRottenTomatoesRating(){
        if (ratings == null || ratings.size() == 0) {
            this.rottenTomatoesRating = "N/A";
        } else {
            try {
                Optional<Rating> RTRating = ratings.stream()
                                                   .filter(rating -> rating.source.equalsIgnoreCase("Rotten Tomatoes"))
                                                   .findAny();
                if (RTRating.isPresent()) {
                    this.rottenTomatoesRating = RTRating.get().value.replace("%", "");
                }
            } catch (NullPointerException e){
                e.printStackTrace();
            }
        }
    }

    private void extractID(){
        if (imdbID != null  && imdbID.length() > 2){
            try {
                id = Integer.parseInt(imdbID.substring(2));
            } catch (NumberFormatException e){
                e.printStackTrace();
            }
        }
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
    public String getRuntime() {
        return runtime;
    }
    public String getImdbRating() {
        return imdbRating;
    }
    public String getImdbVotes() {
        return imdbVotes;
    }
    public String getMetascore() {
        return metascore;
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
    public String getDirector(){
        return director;
    }
    public String getProduction() {
        return production;
    }
    public String getLanguage() {
        return language;
    }
    public String getCountry() {
        return country;
    }
    public String getPoster() {
        return poster;
    }
    public String getWebsite() {
        return website;
    }
    public String getRated() {
        return rated;
    }
    public String getRottenTomatoesRating(){
        return rottenTomatoesRating;
    }
    public String getTotalSeasons() {
        return totalSeasons;
    }
    public List<Rating> getRatings() {
        return ratings;
    }
    public int getId() {
        return id;
    }
    public Boolean getOnList() {
        return onList;
    }
    public void setOnList(Boolean onList) {
        this.onList = onList;
    }

    @Override
    public String toString() {
        return "{" +
                "imdbID='" + imdbID + '\'' +
                ", title='" + title + '\'' +
                ", year='" + year + '\'' +
                ", type='" + type + '\'' +
                ", poster='" + poster + '\'' +
                '}';
    }
}
