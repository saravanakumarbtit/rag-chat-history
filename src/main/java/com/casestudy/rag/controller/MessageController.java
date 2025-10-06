package com.casestudy.rag.controller;

import com.casestudy.rag.dto.CreateMessageDto;
import com.casestudy.rag.model.ChatMessage;
import com.casestudy.rag.service.MessageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sessions/{sessionId}/messages")
public class MessageController {
    private final MessageService svc;
    public MessageController(MessageService svc){ this.svc = svc; }

    @PostMapping
    public ResponseEntity<ChatMessage> add(@PathVariable Long sessionId, @RequestBody CreateMessageDto dto){
        var m = svc.addMessage(sessionId, dto.sender(), dto.content(), dto.retrievedContext());
        return ResponseEntity.ok(m);
    }

    @GetMapping
    public ResponseEntity<Page<ChatMessage>> list(@PathVariable Long sessionId,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "50") int size){
        return ResponseEntity.ok(svc.getMessages(sessionId, PageRequest.of(page, size)));
    }
}
