package io.brennan.review;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.brennan.Utilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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
    @GetMapping("/boxofficetop5")
    public String getBoxOfficeTop5() throws IOException {
        String rawMojo = Utilities.getHTML("https://www.boxofficemojo.com/data/js/wknd5.php");
        String[] splitMojo = rawMojo.split("<td class=mojo_row>");
        StringBuilder formattedMojo = new StringBuilder();
        for (int i = 1 ; i <= 5 ; i++){
            String[] before = splitMojo[i].split("</td>",2);
            formattedMojo.append(before[0].substring(3) + "\r\n");
        }
        ObjectMapper mapper = new ObjectMapper();
        String output = mapper.writeValueAsString(formattedMojo);
        return output.replace("&amp;","&");
    }

    @CrossOrigin
    @GetMapping("/getall")
    public ReviewResponse getAll(@RequestParam(value = "sort", defaultValue = "") String sorting,
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

        ArrayList<Review> movies = new ArrayList<>();
        ArrayList<Review> series = new ArrayList<>();

        for (Review review : reviews){
            if (review.getType().equalsIgnoreCase("movie")){
                movies.add(review);
            } else {
                series.add(review);
            }
        }

        return new ReviewResponse(series, movies);
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
    public ReviewResponse getReviews(@RequestBody String input,
                                     @RequestParam(value = "sort", defaultValue = "") String sorting,
                                     @RequestParam(value = "filter", defaultValue = "") String filter,
                                     @RequestParam(value = "year", defaultValue = "") String year){
        System.out.println(input);
        System.out.println("sorting=" + sorting + ",filter=" + filter);
        input = input.replace("\"", "");
        String titles[] = input.split("~");


        ArrayList<Review> movies = new ArrayList<>();
        ArrayList<Review> series = new ArrayList<>();

        for (String title : titles){
            Review reviewFromDB = reviewService.getByTitle(title);
            if (reviewFromDB == null) {
                try {
                    Review review = Review.getReviewFromTitle(title, year);
                    if (review.getImdbID() == null || review.getPoster().equals("N/A")){
                        System.err.println("failed to find '" + title + "'");
                    } else {
                        reviewService.addReview(review);
                        if (review.getType().equalsIgnoreCase("movie")){
                            movies.add(review);
                        } else {
                            series.add(review);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if (reviewFromDB.getType().equalsIgnoreCase("movie")){
                    movies.add(reviewFromDB);
                } else {
                    series.add(reviewFromDB);
                }
                DBcounter.incrementAndGet();
            }
        }

        System.out.println("request # " + (counter.addAndGet(movies.size() + series.size())));

        try {
            filterReviews(series, filter);
            filterReviews(movies, filter);
            sortReviews(series, sorting);
            sortReviews(movies, sorting);
        } catch (NumberFormatException e){
            System.err.println(e.getMessage());
        }

        System.out.println(titles.length + " -> " + series.size() + "+" + movies.size());

        return new ReviewResponse(series, movies);
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

