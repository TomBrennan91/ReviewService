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
            Review latestReview  = null;
            while (reviews.hasNext()){
                 latestReview = reviews.next();
            }
            //todo: make this return the latest review;
            return latestReview;
        }
        return null;
    }

    public void saveReview(Review review){
        reviewRepository.save(review);
    }
}
