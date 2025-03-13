package com.example.controller;

import com.fasterxml.jackson.annotation.JsonAlias;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageDTO {

    private String content;



    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

}