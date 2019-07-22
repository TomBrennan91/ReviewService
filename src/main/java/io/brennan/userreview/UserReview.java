package io.brennan.userreview;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class UserReview {
  @Id
  private Integer id;
  private String email;
  private String imdbID;
}
