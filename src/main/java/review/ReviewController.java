package review;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class ReviewController {

    private final AtomicLong counter = new AtomicLong();

    @CrossOrigin
    @PostMapping("/review")
    public ArrayList<Review> reviews(@RequestBody String input,
                                     @RequestParam(value = "sort", defaultValue = "") String sorting,
                                     @RequestParam(value = "ratingFilter", defaultValue = "") String ratingFilter){
        System.out.println("request=" + counter.incrementAndGet());
        System.out.println("sorting=" + sorting);
        System.out.println("rating=" + ratingFilter);
        String titles[] = input.split("~");

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
        sortReviews(reviews, sorting);
        //reviews.forEach(System.out::println);
        filterReviews(reviews, ratingFilter);
        System.out.println(titles.length + " -> " + reviews.size());
        return reviews;
    }

    private void sortReviews( ArrayList<Review> reviews, String sorting){
        switch (sorting){
            case "":
                break;
            case "year":
                reviews.sort((a,b) -> b.safeGetYear() - a.safeGetYear());
                break;
            case "rating":
                reviews.sort((a,b) -> b.safeGetImdbRating() - a.safeGetImdbRating());
                break;
            case "name":
                reviews.sort((a,b) -> a.getTitle().compareToIgnoreCase(b.getTitle()));
                break;
            case "votes":
                reviews.sort((a,b) -> b.safeGetImdbVotes() - a.safeGetImdbVotes());
                break;
            case "runtime":
                reviews.sort((a,b) -> b.safeGetRuntime() - a.safeGetRuntime());
                break;
            case "metascore":
                reviews.sort((a,b) -> b.safeGetMetascore() - a.safeGetMetascore());
                break;
            case "type":
                reviews.sort((a,b) -> a.getType().compareToIgnoreCase(b.getType()));
        }
    }


    private void filterReviews(ArrayList<Review> reviews, String ratingFilter){
        if (!ratingFilter.equalsIgnoreCase("")){
            String[] splitFilter = ratingFilter.split(":");
            if (splitFilter.length < 2 ) return;
            if (splitFilter[0].equalsIgnoreCase("gt")){
                reviews.removeIf(review -> review.safeGetImdbRating() < Integer.parseInt(splitFilter[1].replace(".","")));
            }
            if (splitFilter[0].equalsIgnoreCase("lt")){
                reviews.removeIf(review -> review.safeGetImdbRating() > Integer.parseInt(splitFilter[1].replace(".","")));
            }
        }
    }

}

