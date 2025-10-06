package com.casestudy.rag.service;

import com.casestudy.rag.model.ChatMessage;
import com.casestudy.rag.repository.ChatMessageRepository;
import com.casestudy.rag.repository.ChatSessionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MessageService {
    private final ChatMessageRepository messageRepo;
    private final ChatSessionRepository sessionRepo;

    public MessageService(ChatMessageRepository messageRepo, ChatSessionRepository sessionRepo){
        this.messageRepo = messageRepo;
        this.sessionRepo = sessionRepo;
    }

    @Transactional
    public ChatMessage addMessage(Long sessionId, String sender, String content, String retrievedContext){
        var session = sessionRepo.findById(sessionId).orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));
        ChatMessage m = new ChatMessage();
        m.setSession(session);
        m.setSender(sender);
        m.setContent(content);
        m.setRetrievedContext(retrievedContext);
        var saved = messageRepo.save(m);
        session.getMessages().add(saved);
        sessionRepo.save(session);
        return saved;
    }

    public Page<ChatMessage> getMessages(Long sessionId, Pageable pageable){
        return messageRepo.findBySessionIdOrderByCreatedAtAsc(sessionId, pageable);
    }
}
