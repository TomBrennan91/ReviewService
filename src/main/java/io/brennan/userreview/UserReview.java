package io.brennan.userreview;

import javax.persistence.Id;

public class UserReview {
  @Id
  private Integer id;
  private String email;
  private String imdbID;
}
