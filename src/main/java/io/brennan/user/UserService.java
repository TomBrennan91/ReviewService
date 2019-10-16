package io.brennan.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Service
public class UserService {

  @Autowired
  private UserRepository userRepository;

  public User save(User user){
    return userRepository.save(user);
  }

  public Optional<User> findByEmail(String email){
    return userRepository.findByEmail(email);
  }



}
