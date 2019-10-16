package io.brennan.review;

import javax.persistence.Entity;
import java.util.List;

public class ReviewResponse {

  private List<Review> series;
  private List<Review> movies;

  public ReviewResponse(List<Review> series, List<Review> movies) {
    this.series = series;
    this.movies = movies;
  }

  public List<Review> getSeries() {
    return series;
  }

  public List<Review> getMovies() {
    return movies;
  }
}
