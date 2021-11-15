package com.example.sweater.controller;

import com.example.sweater.domain.Message;
import com.example.sweater.domain.User;
import com.example.sweater.repos.MessageRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class MainController {

  private final MessageRepo messageRepo;

  @Value("${upload.path}")
  private String uploadPath;

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
          @Valid Message message,
          BindingResult bindingResult,
          Model model,
          @RequestParam("file") MultipartFile file) throws IOException {
    message.setAuthor(user);

    if (bindingResult.hasErrors()) {
      Map<String, String> errorsMap = ControllerUtils.getErrors(bindingResult);
      model.mergeAttributes(errorsMap);
      model.addAttribute("message", message);
    } else {
      ControllerUtils.saveFile(message, file, uploadPath);
      model.addAttribute("message", null);
      messageRepo.save(message);
    }
    Iterable<Message> messages = messageRepo.findAll();
    model.addAttribute("messages", messages);
    return "main";
  }
}