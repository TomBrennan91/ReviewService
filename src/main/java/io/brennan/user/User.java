package io.brennan.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.brennan.review.Review;

import javax.persistence.*;
import java.util.Set;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    @Id
    @GeneratedValue
    private Integer id;
    private String email;
    private String password;
    @Transient
    private Integer newReviewId;

    @ManyToMany
    private Set<Review> reviews;

    public Integer getId() {
    return id;
    }

    public String getEmail() {
    return email;
    }

    public String getPassword() {
    return password;
    }

    public Set<Review> getReviews() {
    return reviews;
    }

    public Integer getNewReviewId() {
    return newReviewId;
    }

    public User() {
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
