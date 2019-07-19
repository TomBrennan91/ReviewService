package review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    public void getAll(){
        reviewRepository.findAll().forEach(review -> System.out.println(review.toString()));
    }

    public Review getReview(String id){
        return  reviewRepository.findById(id).get();
    }

    public void addReview(Review review){
        reviewRepository.save(review);
    }
}
