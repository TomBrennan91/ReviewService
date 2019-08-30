package io.brennan;

import io.brennan.review.Review;
import io.brennan.review.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

@SpringBootApplication
@EnableScheduling
public class Application {

    @Autowired ReviewService reviewService;

    public static void main(String[] args) throws IOException{
        setAPIKey();
        SpringApplication.run(Application.class, args);
        System.out.println("*** APPLICATION STARTED ***");
    }

    public static String getAPIKey() {
        return APIKey;
    }

    private static String APIKey;

    @Scheduled(fixedRate = (24 * 60 * 60 * 1000))
    public void refreshDatabase() {
        System.out.println("refreshing this year's reviews");
        Iterator<Review> reviews = reviewService.getAll().iterator();
        while (reviews.hasNext()){
            Review review = reviews.next();
            Integer year = null;
            try {
                year = Integer.parseInt(review.getYear());
            } catch (NumberFormatException e){

            }
            if (year != null && year == 2019) {
                System.out.println("updating " +review.getTitle());
                try {
                    Review updatedReview = Review.getReviewFromTitle(review.getTitle(), "2019");
                    reviewService.saveReview(updatedReview);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void setAPIKey() throws IOException{
        String key = System.getenv("EXTERNAL_API_KEY");
        System.out.println(key);
        if (key == null){
            BufferedReader in = new BufferedReader(new FileReader("apiKey.env"));
            key = in.lines().findFirst().get();
        }

        APIKey = key;
        System.out.println("API Key = " + APIKey);
    }
}