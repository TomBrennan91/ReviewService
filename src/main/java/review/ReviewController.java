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
        sortReviews(reviews, sorting);
        reviews.forEach(System.out::println);
        return reviews;
    }

    private void sortReviews( ArrayList<Review> reviews, String sorting){
        switch (sorting){
            case "":
                break;
            case "year":
                reviews.sort((a,b) -> Integer.parseInt(b.getYear()) - Integer.parseInt(a.getYear()));
                break;
            case "rating":
                reviews.sort((a,b) -> Integer.parseInt(b.getImdbRating().replace(".","")) - Integer.parseInt(a.getImdbRating().replace(".","")));
                break;
            case "name":
                reviews.sort((a,b) -> a.getTitle().compareToIgnoreCase(b.getTitle()));
                break;
            case "votes":
                reviews.sort((a,b) -> Integer.parseInt(b.getImdbVotes().replace(",", "")) -Integer.parseInt(a.getImdbVotes().replace(",", "")));
                break;
            case "runtime":
                reviews.sort((a,b) -> Integer.parseInt(b.getRuntime().replace(" min", "")) -Integer.parseInt(a.getRuntime().replace(" min", "")));
        }
    }

}

