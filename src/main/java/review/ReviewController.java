package review;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    private AtomicLong counter = new AtomicLong();

    private final LocalDate startDate = LocalDate.now();

    @GetMapping("/getall")
    public Iterable<Review> getAll(){
        System.out.println("getting all ");
        return reviewService.getAll();
    }

    @GetMapping("search/{id}")
    public Review findbyTitle(@PathVariable String title){
        System.out.println("getting review " + title);
        return reviewService.getByTitle(title);
    }

    @CrossOrigin
    @GetMapping("/reviewsServiced")
    public Object getReviewsServiced() throws JsonProcessingException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        ObjectMapper mapper = new ObjectMapper();
        String reviewsServiced = counter.get() + " Reviews serviced since " + formatter.format(startDate);
        return  mapper.writeValueAsString(reviewsServiced);
    }

    @CrossOrigin
    @PostMapping("/review")
    public ArrayList<Review> reviews(@RequestBody String input,
                                     @RequestParam(value = "sort", defaultValue = "") String sorting,
                                     @RequestParam(value = "filter", defaultValue = "") String filter){

        System.out.println("sorting=" + sorting + ",filter=" + filter);
        String titles[] = input.split("~");

        ArrayList<Review> reviews = new ArrayList<>();
        for (String title : titles){
            Review reviewFromDB = reviewService.getByTitle(title);
            if (reviewFromDB == null) {
                try {
                    Review review = Review.getReviewFromTitle(title);
                    if (review.getImdbRating() != null) {
                        reviewService.addReview(review);
                        reviews.add(review);
                    } else {
                        System.err.println(title);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                reviews.add(reviewFromDB);
            }
        }

        System.out.println("request # " + (counter.addAndGet(reviews.size())));

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

