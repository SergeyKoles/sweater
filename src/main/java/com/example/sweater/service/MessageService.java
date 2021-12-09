package com.example.sweater.service;

import com.example.sweater.domain.User;
import com.example.sweater.domain.dto.MessageDto;
import com.example.sweater.repos.MessageRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageService {

  private final MessageRepo messageRepo;

  public Page<MessageDto> messageList(Pageable pageable, String filter, User user) {
    if (filter != null && !filter.isEmpty()) {
      return messageRepo.findByTags(filter, pageable, user);
    } else {
      return messageRepo.findAll(pageable, user);
    }
  }

  public Page<MessageDto> messageListForUse(Pageable pageable, User currentUser, User author) {
    return messageRepo.findByUser(pageable, author, currentUser);

  }
}
