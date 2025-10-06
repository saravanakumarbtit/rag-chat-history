package com.casestudy.rag.dto;

public record CreateMessageDto(String sender, String content, String retrievedContext) { }
