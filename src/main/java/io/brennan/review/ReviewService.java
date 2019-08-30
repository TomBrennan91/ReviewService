package io.brennan.review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public Review getByTitle(String title){
        return reviewRepository.findByTitle(title);
    }

    public void saveReview(Review review){
        reviewRepository.save(review);
    }
}
