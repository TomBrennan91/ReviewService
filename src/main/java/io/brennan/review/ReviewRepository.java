package io.brennan.review;

import org.springframework.data.repository.CrudRepository;

import javax.persistence.NonUniqueResultException;

public interface ReviewRepository extends CrudRepository<Review, Integer> {

    public Review findByTitle(String title) throws NonUniqueResultException;
}
