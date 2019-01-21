package review;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicLong;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;

@RestController
public class ReviewController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @CrossOrigin
    @PostMapping("/review")
    public ArrayList<Review> reviews(@RequestBody String input, @RequestParam(value = "sort", defaultValue = "")String sorting){

        System.out.println("sorting = " + sorting);
        System.out.println(input);
        String titles[] = input.split("~");

        System.out.println(titles[titles.length -1]);

        ArrayList<Review> reviews = new ArrayList<>();

        for (String title : titles){
            try {
                Review review = Review.getReviewFromTitle(title);
                //System.out.println(review.toString());
                if (review.getImdbRating() != null) {
                    reviews.add(review);
                } else {
                    System.err.println(title);
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        System.out.println(titles.length + " -> " + reviews.size());
        Collections.sort(reviews , (a,b) -> b.getImdbRating().compareToIgnoreCase(a.getImdbRating()));
        reviews.forEach(System.out::println);
        return reviews;
    }





}

