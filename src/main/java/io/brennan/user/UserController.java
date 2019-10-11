package io.brennan.user;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.brennan.review.Review;
import io.brennan.review.ReviewResponse;
import io.brennan.review.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("user")
public class UserController {

  @Autowired
  private UserService userService;

  @Autowired
  private ReviewService reviewService;

  @CrossOrigin
  @GetMapping("new")
  public String createUser(@RequestHeader(value = "email", defaultValue = "") String email,
                                   @RequestHeader(value = "password", defaultValue = "") String password) throws JsonProcessingException{

    System.out.println("create user request " + email + password);
    if (email != null && email.contains("@")){
      if (password != null  && password.length() >=6){
        Optional<User> existingUser = userService.findByEmail(email);
        if (existingUser.isPresent()){
          System.out.println("user " + email + " already exists");
        } else {
          User user = new User(email, password);
          System.out.println("created new user: " + user.getEmail());
          userService.save(user);
        }
        return new ObjectMapper().writeValueAsString("OK");
      } else {
        return new ObjectMapper().writeValueAsString("Invalid Password");
      }
    } else {
      return new ObjectMapper().writeValueAsString("Invalid Email");
    }
  }

  @CrossOrigin
  @GetMapping(value = "login", produces = MediaType.APPLICATION_JSON_VALUE)
  public String login(@RequestHeader(value = "email", defaultValue = "") String email,
                              @RequestHeader(value = "password", defaultValue = "") String password) throws JsonProcessingException {
    User user = new User(email, password);
    if (authenticateUser(user)){
      return new ObjectMapper().writeValueAsString("OK"); //new ResponseEntity("thumbs up from server" , HttpStatus.OK);
    } else {
      return new ObjectMapper().writeValueAsString("Invalid email or password"); //new ResponseEntity("Invalid email or password", HttpStatus.BAD_REQUEST);
    }
  }

  @CrossOrigin
  @PostMapping("reset")
  public ResponseEntity resetPassword(@RequestBody User user){
    Optional<User> existingUser = userService.findByEmail(user.getEmail());
    if (existingUser.isPresent()){
      System.out.println("send reset password email to " + user.getEmail());
      //todo: make reset password code
    }
    return new ResponseEntity(HttpStatus.OK);
  }

  public boolean authenticateUser(User user){
    Optional<User> existingUser = userService.findByEmail(user.getEmail());
    return (existingUser.isPresent() && existingUser.get().getPassword().equals(user.getPassword()));
  }

  @CrossOrigin
  @GetMapping("retrieve")
  public ReviewResponse getForUser(@RequestHeader(value = "email", defaultValue = "") String email,
                                   @RequestHeader(value = "password", defaultValue = "") String password) throws IOException {
    User user = new User(email, password);
    if (authenticateUser(user)){
      System.out.println("Retrieving reviews for " + email);
      ObjectMapper mapper = new ObjectMapper();
      Set<Review> answer = userService.findByEmail(user.getEmail()).get().getReviews();
      answer.forEach(review -> review.setOnList(true));
      System.out.println(mapper.writeValueAsString(answer));
      ArrayList<Review> reviews = new ArrayList<>();
      reviews.addAll(answer);

      return new ReviewResponse(null, reviews);
    } else {
      return null;
    }
  }



  @CrossOrigin
  @PostMapping(path = "addreview", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity addReviewToUser(@RequestBody String userJson) throws IOException{
    System.out.println("adding reviews to " + userJson);
    ObjectMapper mapper = new ObjectMapper();
    User user = mapper.readValue(userJson, User.class);
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

  @CrossOrigin
  @PostMapping(path = "removereview", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity removeReviewFromUser(@RequestBody String userJson) throws IOException{
    System.out.println("removing reviews from " + userJson);
    ObjectMapper mapper = new ObjectMapper();
    User user = mapper.readValue(userJson, User.class);
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
