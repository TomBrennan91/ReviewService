package io.brennan.review;

import org.springframework.data.repository.CrudRepository;

public interface ReviewRepository extends CrudRepository<Review, String> {

    public Review findByTitle(String title);
}
