package com.example.sweater.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class Message {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  private String text;
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
