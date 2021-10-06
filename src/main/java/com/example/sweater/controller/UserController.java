package com.example.sweater.controller;

import com.example.sweater.repos.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

  private final UserRepo userRepo;

  @GetMapping
  public String userList(Model model) {
    model.addAttribute("users", userRepo.findAll());
    return "userList";
  }
}
