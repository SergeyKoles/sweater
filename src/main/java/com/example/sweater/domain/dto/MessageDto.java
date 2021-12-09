package com.example.sweater.domain.dto;

import com.example.sweater.domain.Message;
import com.example.sweater.domain.User;
import com.example.sweater.domain.util.MessageHelper;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(exclude = {"text", "tags", "filename"})
public class MessageDto {
  private Long id;
  private String text;
  private String tags;
  private User author;
  private String filename;
  private Long likes;
  private Boolean meLiked;

  public MessageDto(Message message, Long likes, Boolean meLiked) {
    this.id = message.getId();
    this.tags = message.getTags();
    this.text = message.getText();
    this.author = message.getAuthor();
    this.filename = message.getFilename();
    this.likes = likes;
    this.meLiked = meLiked;
  }

  public String getAuthorName() {
    return MessageHelper.getAuthorName(author);
  }
}
