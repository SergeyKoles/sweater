package com.example.sweater.controller;

import com.example.sweater.domain.User;
import com.example.sweater.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class RegistrationController {

  private final UserService userService;

  @GetMapping("/registration")
  public String registration() {
    return "registration";
  }

  @PostMapping("/registration")
  public String addUser(User user, Map<String, Object> model) {
    if (!userService.addUser(user)) {
      model.put("message", "User exists!");
      return "registration";
    }
    return "redirect:/login";
  }

  @GetMapping("/activate/{code}")
  public String activate(Model model, @PathVariable String code) {
    boolean isActivate = userService.activateUser(code);
    if (!isActivate) {
      model.addAttribute("message", "User success fully activate");
    } else {
      model.addAttribute("message", "Activation code is not found!");
    }
    return "login";
  }
}
