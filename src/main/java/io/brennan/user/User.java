package io.brennan.user;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class User {

  @Id
  private String email;
  private String password;


}
