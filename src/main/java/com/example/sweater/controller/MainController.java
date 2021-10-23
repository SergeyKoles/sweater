package com.example.sweater.controller;

import com.example.sweater.domain.Message;
import com.example.sweater.domain.User;
import com.example.sweater.repos.MessageRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class MainController {

  private final MessageRepo messageRepo;

  @GetMapping("/")
  public String greeting(Map<String, Object> model) {
    return "greeting";
  }

  @GetMapping("/main")
  public String main(
          @RequestParam(required = false, defaultValue = "") String filter,
          Model model) {
    Iterable<Message> messages;
    if (filter != null && !filter.isEmpty()) {
      messages = messageRepo.findByTags(filter);
    } else {
      messages = messageRepo.findAll();
    }
    model.addAttribute("messages", messages);
    model.addAttribute("filter", filter);
    return "main";
  }

  @PostMapping("/main")
  public String add(
          @AuthenticationPrincipal User user,
          @RequestParam String text,
          @RequestParam String tags,
          Map<String, Object> model) {
    Message message = new Message(text, tags, user);
    messageRepo.save(message);
    Iterable<Message> messages = messageRepo.findAll();
    model.put("messages", messages);
    return "main";
  }
}