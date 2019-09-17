package io.brennan.user;

import io.brennan.review.Review;
import io.brennan.review.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("user")
public class UserController {

  @Autowired
  private UserService userService;

  @Autowired
  private ReviewService reviewService;

  @PostMapping("new")
  public ResponseEntity createUser(@RequestBody User user){
    if (user != null && user.getEmail() != null && user.getEmail().contains("@")){
      if (user.getPassword() != null  && user.getPassword().length() >=6){
        userService.save(user);
        return new ResponseEntity(HttpStatus.OK);
      } else {
        return new ResponseEntity("Invalid Password", HttpStatus.BAD_REQUEST);
      }
    } else {
      return new ResponseEntity("Invalid Email", HttpStatus.BAD_REQUEST);
    }
  }

  @PostMapping("login")
  public ResponseEntity login(@RequestBody User user){
    if (authenticateUser(user)){
      return new ResponseEntity(HttpStatus.OK);
    } else {
      return new ResponseEntity("Invalid email or password", HttpStatus.BAD_REQUEST);
    }
  }

  @PostMapping("reset")
  public ResponseEntity resetPassword(@RequestBody User user){
    Optional<User> existingUser = userService.findByEmail(user.getEmail());
    if (existingUser.isPresent()){
      System.out.println("send reset password email to " + user.getEmail());
      //todo: make reset password code
    }
    return new ResponseEntity(HttpStatus.OK);
  }

  public boolean authenticateUser(@RequestBody User user){
    Optional<User> existingUser = userService.findByEmail(user.getEmail());
    return (existingUser.isPresent() && existingUser.get().getPassword().equals(user.getPassword()));
  }

  @PostMapping("retrieve")
  public Set<Review> getForUser(@RequestBody User user){
    if (authenticateUser(user)){
      return userService.findByEmail(user.getEmail()).get().getReviews();
    } else {
      return null;
    }
  }

  @PostMapping("addreview")
  public ResponseEntity addReviewToUser(@RequestBody User user){
    if (authenticateUser(user)){
      if (user.getNewReviewId() != null){
        Review existingReview = reviewService.getReview(user.getNewReviewId());
        if (existingReview != null){
          User existingUser = userService.findByEmail(user.getEmail()).get();
          existingUser.getReviews().add(existingReview);
          userService.save(existingUser);
          return new ResponseEntity(HttpStatus.OK);
        }
        return new ResponseEntity("Review " + user.getNewReviewId() + " does not exist", HttpStatus.BAD_REQUEST);
      }
      return new ResponseEntity("null new review ID", HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity("Invalid email or password", HttpStatus.BAD_REQUEST);
  }

  @PostMapping("removereview")
  public ResponseEntity removeReviewFromUser(@RequestBody User user){
    if (authenticateUser(user)){
      if (user.getNewReviewId() != null){
        User existingUser = userService.findByEmail(user.getEmail()).get();
        existingUser.getReviews().removeIf(review -> review.getId() == user.getNewReviewId());
        userService.save(existingUser);
        return new ResponseEntity(HttpStatus.OK);
      }
      return new ResponseEntity("null new review ID", HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity("Invalid email or password", HttpStatus.BAD_REQUEST);
  }

}
