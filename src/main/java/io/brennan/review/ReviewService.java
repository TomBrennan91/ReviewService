package io.brennan.review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.NonUniqueResultException;
import java.util.Iterator;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    public Iterable<Review> getAll(){
        return reviewRepository.findAll();
    }

    public Review getReview(Integer id){
        return  reviewRepository.findById(id).get();
    }

    public Review getByTitle(String title) throws NonUniqueResultException {
        Iterator<Review> reviews = reviewRepository.findAllByTitle(title).iterator();
        if (reviews.hasNext()){
            Review latestReview  = reviews.next();
            while (reviews.hasNext()){
                 Review currentReview = reviews.next();
                 if (latestReview.getIntYear() != null) {
                     if ((currentReview.getIntYear() == null) || currentReview.getIntYear() > latestReview.getIntYear()) {
                         latestReview = currentReview;
                     }
                 }
            }
            return latestReview;
        }
        return null;
    }

    public void saveReview(Review review){
        reviewRepository.save(review);
    }
}
