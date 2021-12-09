package com.example.sweater.controller;

import com.example.sweater.domain.Message;
import com.example.sweater.domain.User;
import com.example.sweater.domain.dto.MessageDto;
import com.example.sweater.repos.MessageRepo;
import com.example.sweater.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user-messages")
public class MessageController {

  private final MessageRepo messageRepo;

  private final MessageService messageService;

  @Value("${upload.path}")
  private String uploadPath;

  @GetMapping("/{author}")
  public String userMessages(
          @AuthenticationPrincipal User currentUser,
          @PathVariable User author,
          Model model,
          @RequestParam(required = false) Message message,
          @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable
  ) {
    Page<MessageDto> page = messageService.messageListForUse(pageable, currentUser, author);

    model.addAttribute("userChannel", author);
    model.addAttribute("subscriptionsCount", author.getSubscriptions().size());
    model.addAttribute("subscribersCount", author.getSubscribers().size());
    model.addAttribute("isSubscriber", author.getSubscribers().contains(currentUser));
    model.addAttribute("page", page);
    model.addAttribute("message", message);
    model.addAttribute("isCurrentUser", currentUser.equals(author));
    model.addAttribute("url", "/user-messages/" + author.getId());

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
