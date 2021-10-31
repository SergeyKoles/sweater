package com.example.sweater.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class Message {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @NotBlank(message = "Please fill the message")
  @Length(max = 2048, message = "Message too long (more then 2kB)")
  private String text;
  @Length(max = 255, message = "Message too long (more then 255)")
  private String tags;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id")
  private User author;

  private String filename;

  public Message(String text, String tags, User author) {
    this.text = text;
    this.tags = tags;
    this.author = author;
  }

  public String getAuthorName() {
    return author != null ? author.getUsername() : "<none>";
  }
}
