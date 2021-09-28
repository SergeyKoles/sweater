package com.example.sweater.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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

  public Message(String text, String tags) {
    this.text = text;
    this.tags = tags;
  }
}
