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
        System.out.println("request: " + counter.incrementAndGet());
        System.out.println("sorting = " + sorting);
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
        System.out.println(titles.length + " -> " + reviews.size());
        sortReviews(reviews, sorting);
        reviews.forEach(System.out::println);
        filterReviews(reviews, ratingFilter);

        return reviews;
    }

    private void sortReviews( ArrayList<Review> reviews, String sorting){
        switch (sorting){
            case "":
                break;
            case "year":
                reviews.sort((a,b) -> Integer.parseInt(b.getYear().replace("–",""))
                                    - Integer.parseInt(a.getYear().replace("–","")));
                break;
            case "rating":
                reviews.sort((a,b) -> Integer.parseInt(b.getImdbRating().replace(".","").replace("N/A","0"))
                                    - Integer.parseInt(a.getImdbRating().replace(".","").replace("N/A","0")));
                break;
            case "name":
                reviews.sort((a,b) -> a.getTitle().compareToIgnoreCase(b.getTitle()));
                break;
            case "votes":
                reviews.sort((a,b) -> Integer.parseInt(b.getImdbVotes().replace(",", "").replace("N/A","0"))
                                     -Integer.parseInt(a.getImdbVotes().replace(",", "").replace("N/A","0")));
                break;
            case "runtime":
                reviews.sort((a,b) -> Integer.parseInt(b.getRuntime().replace(" min", "").replace("N/A","0"))
                                     -Integer.parseInt(a.getRuntime().replace(" min", "").replace("N/A","0")));
                break;
            case "metascore":
                reviews.sort((a,b) -> Integer.parseInt(b.getMetascore().replace("N/A","0"))
                                     -Integer.parseInt(a.getMetascore().replace("N/A","0")));
                break;
            case "type":
                reviews.sort((a,b) -> a.getType().compareToIgnoreCase(b.getType()));
        }
    }


    private void filterReviews(ArrayList<Review> reviews, String ratingFilter){
        if (!ratingFilter.equalsIgnoreCase("")){
            String[] splitFilter = ratingFilter.split(":");
            if (splitFilter.length < 2 ) return;
            if (splitFilter[0].equalsIgnoreCase("lt")){
                reviews.removeIf(review -> Integer.parseInt(review.getImdbRating().replace(".","")) < Integer.parseInt(splitFilter[1].replace(".","")));
            }
            if (splitFilter[0].equalsIgnoreCase("gt")){
                reviews.removeIf(review -> Integer.parseInt(review.getImdbRating().replace(".","")) > Integer.parseInt(splitFilter[1].replace(".","")));
            }
        }
    }

}

