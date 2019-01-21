package review;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class ReviewController {

    private final AtomicLong counter = new AtomicLong();

    @CrossOrigin
    @PostMapping("/review")
    public ArrayList<Review> reviews(@RequestBody String input, @RequestParam(value = "sort", defaultValue = "")String sorting){
        System.out.println("request: " + counter.incrementAndGet());
        System.out.println("sorting = " + sorting);
        String titles[] = input.split("~");
        System.out.println(titles[titles.length -1]);

        ArrayList<Review> reviews = new ArrayList<>();

        for (String title : titles){
            try {
                Review review = Review.getReviewFromTitle(title);
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
        switch (sorting){
            case "":
                break;
            case "year":
                reviews.sort((a,b) -> b.getYear().compareToIgnoreCase(a.getYear()));
                break;
            case "score":
                reviews.sort((a,b) -> b.getImdbRating().compareToIgnoreCase(a.getImdbRating()));
                break;
            case "name":
                reviews.sort((a,b) -> a.getTitle().compareToIgnoreCase(b.getTitle()));
                break;
            case "votes":
                reviews.sort((a,b) -> b.getImdbVotes().compareToIgnoreCase(a.getImdbVotes()));
                break;
            case "runtime":
                reviews.sort((a,b) -> a.getRuntime().compareToIgnoreCase(b.getRuntime()));
        }

        reviews.forEach(System.out::println);
        return reviews;
    }

}

