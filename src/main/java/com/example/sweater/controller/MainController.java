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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class MainController {

  private final MessageRepo messageRepo;

  private final MessageService messageService;

  @Value("${upload.path}")
  private String uploadPath;

  @GetMapping("/")
  public String greeting(Map<String, Object> model) {
    return "greeting";
  }

  @GetMapping("/main")
  public String main(
          @RequestParam(required = false, defaultValue = "") String filter,
          Model model,
          @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable,
          @AuthenticationPrincipal User user) {
    Page<MessageDto> page = messageService.messageList(pageable, filter, user);

    model.addAttribute("page", page);
    model.addAttribute("url", "/main");
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

  @GetMapping("messages/{message}/like")
  public String like(
          @AuthenticationPrincipal User user,
          @PathVariable Message message,
          RedirectAttributes redirectAttributes,
          @RequestHeader(required = false) String referer
  ) {
    Set<User> likes = message.getLikes();

    if (likes.contains(user)) {
      likes.remove(user);
    } else {
      likes.add(user);
    }
    UriComponents components = UriComponentsBuilder.fromHttpUrl(referer).build();
    components.getQueryParams()
            .entrySet()
            .forEach(pair -> redirectAttributes.addAttribute(pair.getKey(), pair.getValue()));

    return "redirect:" + components.getPath();
  }
}