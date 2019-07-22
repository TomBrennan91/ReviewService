package io.brennan.review;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    private AtomicLong counter = new AtomicLong();

    private AtomicLong DBcounter = new AtomicLong();

    private final LocalDate startDate = LocalDate.now();

    @CrossOrigin
    @GetMapping("/getall")
    public Iterable<Review> getAll(@RequestParam(value = "sort", defaultValue = "") String sorting,
                                   @RequestParam(value = "filter", defaultValue = "") String filter){
        System.out.println("getting all " + "sorting=" + sorting + ",filter=" + filter);

        ArrayList <Review> reviews = new ArrayList<>();
        reviewService.getAll().forEach(review -> reviews.add(review));

        try {
            filterReviews(reviews, filter);
            sortReviews(reviews, sorting);
        } catch (NumberFormatException e){
            System.err.println(e.getMessage());
        }

        counter.addAndGet(reviews.size());
        DBcounter.addAndGet(reviews.size());

        return reviews;
    }

    @GetMapping("search/{id}")
    public Review findbyTitle(@PathVariable String title){
        System.out.println("getting io.brennan.review " + title);
        return reviewService.getByTitle(title);
    }

    @CrossOrigin
    @GetMapping("/reviewsServiced")
    public Object getReviewsServiced() throws JsonProcessingException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        ObjectMapper mapper = new ObjectMapper();
        String reviewsServiced = counter.get() + " Reviews serviced since " + formatter.format(startDate) + " (of which " + DBcounter.get() + " were cached)";
        return  mapper.writeValueAsString(reviewsServiced);
    }

    @CrossOrigin
    @PostMapping("/review")
    public ArrayList<Review> reviews(@RequestBody String input,
                                     @RequestParam(value = "sort", defaultValue = "") String sorting,
                                     @RequestParam(value = "filter", defaultValue = "") String filter){
        System.out.println(input);
        System.out.println("sorting=" + sorting + ",filter=" + filter);
        input = input.replace("\"", "");
        String titles[] = input.split("~");

        ArrayList<Review> reviews = new ArrayList<>();
        for (String title : titles){
            Review reviewFromDB = reviewService.getByTitle(title);
            if (reviewFromDB == null) {
                try {
                    System.out.println("review not cached");
                    Review review = Review.getReviewFromTitle(title);
                    if (review.getImdbID() == null){
                        System.err.println("failed to find '" + title + "'");
                    } else {
                        reviewService.addReview(review);
                        reviews.add(review);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("review cached");
                reviews.add(reviewFromDB);
                DBcounter.incrementAndGet();
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
                if (gt) reviews.removeIf(review -> review.safeGetMetascore() < Integer.parseInt(value.replace("N/A","")));
                else    reviews.removeIf(review -> review.safeGetMetascore() > Integer.parseInt(value.replace("N/A","")));
                break;
            case "rottentomatoes":
                if (gt) reviews.removeIf(review -> review.safeGetRT() < Integer.parseInt(value.replace("N/A","")));
                else    reviews.removeIf(review -> review.safeGetRT() > Integer.parseInt(value.replace("N/A","")));
                break;
        }
    }

}

