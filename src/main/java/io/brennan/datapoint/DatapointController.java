package io.brennan.datapoint;

import io.brennan.Application;
import io.brennan.Utilities;
import io.brennan.review.Review;
import io.brennan.review.ReviewController;
import io.brennan.review.ReviewResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.util.ArrayList;

@RestController
public class DatapointController {

  @Autowired
  private ReviewController reviewController;

  private static ArrayList<Datapoint> getAllDatapoints(){
    ArrayList<Datapoint> datapoints = new ArrayList<>();
    BufferedReader reader = Utilities.fromFile("/movieBigList.txt", Application.class);
    reader.lines().forEach(line -> datapoints.add(Datapoint.getDatapointFromLine(line)));
    datapoints.removeIf(datapoint -> datapoint == null);
    System.out.println(datapoints.size() + " datapoints");
    datapoints.sort((a,b) -> b.getYear() - a.getYear());
    return datapoints;
  }

  @PatchMapping("loadTheBigExcelFile")
  private void runThroughDatapoints() throws InterruptedException {
    ArrayList<Datapoint> datapoints = getAllDatapoints();
    for (Datapoint datapoint : datapoints){
      String title = datapoint.getTitle();
      String year = datapoint.getYear().toString();
      ReviewResponse reviewResponse = reviewController.getReviews(title,"", "", year,"", "");
      Review review = null;
      if (reviewResponse.getSeries().size() > 0) {
        review = reviewResponse.getSeries().get(0);
      } else if (reviewResponse.getMovies().size() > 0) {
        review = reviewResponse.getMovies().get(0);
      }
      if (review != null) {
        System.out.println(review.getTitle() + "  " + review.getImdbVotes() + "  " + review.getImdbRating());
      }
      Thread.sleep(3000);
    }
  }

}
