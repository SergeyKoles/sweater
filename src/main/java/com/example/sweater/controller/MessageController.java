package com.example.sweater.controller;

import com.example.sweater.domain.Message;
import com.example.sweater.domain.User;
import com.example.sweater.repos.MessageRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user-messages")
public class MessageController {

  private final MessageRepo messageRepo;

  @Value("${upload.path}")
  private String uploadPath;

  @GetMapping("/{user}")
  public String userMessages(
          @AuthenticationPrincipal User currentUser,
          @PathVariable User user,
          Model model,
          @RequestParam(required = false) Message message
  ) {
    Set<Message> messages = user.getMessages();

    model.addAttribute("userChannel", user);
    model.addAttribute("subscriptionsCount", user.getSubscriptions().size());
    model.addAttribute("subscribersCount", user.getSubscribers().size());
    model.addAttribute("isSubscriber", user.getSubscribers().contains(currentUser));
    model.addAttribute("messages", messages);
    model.addAttribute("message", message);
    model.addAttribute("isCurrentUser", currentUser.equals(user));

    return "userMessages";
  }

  @PostMapping("/{user}")
  public String updateMessage(
          @AuthenticationPrincipal User currentUser,
          @PathVariable Long user,
          @RequestParam("id") Message message,
          @RequestParam("text") String text,
          @RequestParam("tags") String tags,
          @RequestParam("file") MultipartFile file
  ) throws IOException {
    if (message.getAuthor().equals(currentUser)) {
      if (!StringUtils.isEmpty(text)) {
        message.setText(text);
      }
      if (!StringUtils.isEmpty(tags)) {
        message.setTags(tags);
      }
      ControllerUtils.saveFile(message, file, uploadPath);
      messageRepo.save(message);
    }
    return "redirect:/user-messages/" + user;
  }
}
