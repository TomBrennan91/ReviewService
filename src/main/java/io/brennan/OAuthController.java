package io.brennan;

import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
public class OAuthController extends WebSecurityConfigurerAdapter {

  @GetMapping("/user")
  public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
    System.out.println(principal.getAttributes().keySet());
    System.out.println(principal.getAttributes().values());
    return Collections.singletonMap("name", principal.getAttributes().get("name"));
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    // @formatter:off
    http
      .authorizeRequests(a -> a
        .antMatchers("/", "/error", "/webjars/**").permitAll()
        .anyRequest().authenticated()
      )
      .exceptionHandling(e -> e
        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
      )
      .csrf(c -> c
        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
      )
      .logout(l -> l
        .logoutSuccessUrl("/").permitAll()
      )
      .oauth2Login();
    // @formatter:on
  }
}
