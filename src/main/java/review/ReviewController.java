package review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    private final AtomicLong counter = new AtomicLong();

    @GetMapping("/hello")
    public String greet(){
        System.out.println("hello world");
        return "hello world";
    }

    @GetMapping("/getall")
    public void getAll(){
        reviewService.getAll();
    }

    @GetMapping("get/{id}")
    public Review getOne(@PathVariable String id){
        return reviewService.getReview(id);
    }

    @GetMapping("put/{id}")
    public Review put(@PathVariable String id){
        return reviews(id,"", "").get(0);
    }

    @CrossOrigin
    @PostMapping("/review")
    public ArrayList<Review> reviews(@RequestBody String input,
                                     @RequestParam(value = "sort", defaultValue = "") String sorting,
                                     @RequestParam(value = "filter", defaultValue = "") String filter){
        System.out.println("request# " + counter.incrementAndGet());
        System.out.println("sorting=" + sorting + ",filter=" + filter);
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

        try {
            filterReviews(reviews, filter);
            sortReviews(reviews, sorting);
        } catch (NumberFormatException e){
            System.err.println(e.getMessage());
        }

        System.out.println(titles.length + " -> " + reviews.size());
        return reviews;
    }

    private void sortReviews( ArrayList<Review> reviews, String sorting) throws NumberFormatException{
        String[] splitSorting = sorting.split(":");

        switch (splitSorting[0]){
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
        if (splitSorting.length >= 2 && splitSorting[1].equalsIgnoreCase("asc")){
            Collections.reverse(reviews);
        }
    }


    private void filterReviews(ArrayList<Review> reviews, String filter) throws NumberFormatException {
        if (filter.equalsIgnoreCase("")) return;
        String[] splitFilter = filter.split(":");
        if (splitFilter.length < 3) return;
        boolean gt = splitFilter[1].equalsIgnoreCase("gt");
        String value = splitFilter[2];
        switch (splitFilter[0]) {
            case "":
                break;
            case "year":
                if(gt) reviews.removeIf(review -> review.safeGetYear() < Integer.parseInt(value));
                else   reviews.removeIf(review -> review.safeGetYear() > Integer.parseInt(value));
                break;
            case "rating":
                if (gt) reviews.removeIf(review -> review.safeGetImdbRating() < Integer.parseInt(value.replace(".","")));
                else    reviews.removeIf(review -> review.safeGetImdbRating() > Integer.parseInt(value.replace(".","")));
                break;
            case "votes":
                if (gt) reviews.removeIf(review -> review.safeGetImdbVotes() < Integer.parseInt(value.replace(",","").replace("N/A","")));
                else    reviews.removeIf(review -> review.safeGetImdbVotes() > Integer.parseInt(value.replace(",","").replace("N/A","")));
                break;
            case "runtime":
                if (gt) reviews.removeIf(review -> review.safeGetRuntime() < Integer.parseInt(value.replace(",","").replace("N/A","")));
                else    reviews.removeIf(review -> review.safeGetRuntime() > Integer.parseInt(value.replace(",","").replace("N/A","")));
                break;
            case "metascore":

                break;
        }
    }

}

