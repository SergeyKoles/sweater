package com.example.sweater.service;

import com.example.sweater.domain.Role;
import com.example.sweater.domain.User;
import com.example.sweater.repos.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {

  private final UserRepo userRepo;

  private final MailSender mailSender;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepo.findByUsername(username);
    if (user == null) {
      throw new UsernameNotFoundException("User not found");
    }
    return user;
  }

  public boolean addUser(User user) {
    User userFromDb = userRepo.findByUsername(user.getUsername());
    if (userFromDb != null) {
      return false;
    }
    user.setActive(true);
    user.setRoles(Collections.singleton(Role.USER));
    user.setActivationCode(UUID.randomUUID().toString());
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    userRepo.save(user);

    sendMessage(user);
    return true;
  }

  private void sendMessage(User user) {
    if (!StringUtils.isEmpty(user.getEmail())) {
      String message = String.format(
              "Hellow, %s! \n" +
                      "Welcome to Sweater. Please, visit next link: http://localhost:8080/activate/%s",
              user.getUsername(),
              user.getActivationCode()
      );
      mailSender.send(user.getEmail(), "Activation code", message);
    }
  }

  public boolean activateUser(String code) {
    User user = userRepo.findByActivationCode(code);
    if (user == null) {
      return false;
    }
    user.setActivationCode(null);
    userRepo.save(user);
    return true;
  }

  public List<User> findAll() {
    return userRepo.findAll();
  }

  public void saveUser(User user, String username, Map<String, String> form) {
    user.setUsername(username);
    Set<String> roles = Arrays.stream(Role.values())
            .map(Role::name)
            .collect(Collectors.toSet());
    user.getRoles().clear();
    form.keySet().stream()
            .filter(roles::contains)
            .forEach(key -> user.getRoles().add(Role.valueOf(key)));

    userRepo.save(user);
  }

  public void updateProfile(User user, String password, String email) {
    String userEmail = user.getEmail();

    boolean isEmailChange = (email != null && !email.equals(userEmail)) ||
            (userEmail != null && !userEmail.equals(email));
    if (isEmailChange) {
      user.setEmail(email);
      if (!StringUtils.isEmpty(email)) {
        user.setActivationCode(UUID.randomUUID().toString());
      }
    }
    if (!StringUtils.isEmpty(password)) {
      user.setPassword(password);
    }
    userRepo.save(user);

    if (isEmailChange) {
      sendMessage(user);
    }
  }

  public void subscribe(User currentUser, User user) {
    user.getSubscribers().add(currentUser);

    userRepo.save(user);
  }

  public void unsubscribe(User currentUser, User user) {
    user.getSubscribers().remove(currentUser);

    userRepo.save(user);
  }
}
