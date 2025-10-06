package com.casestudy.rag.service;

import com.casestudy.rag.model.ChatSession;
import com.casestudy.rag.repository.ChatSessionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SessionService {
    private final ChatSessionRepository sessionRepo;

    public SessionService(ChatSessionRepository sessionRepo){
        this.sessionRepo = sessionRepo;
    }

    @Transactional
    public ChatSession createSession(String title, String userId){
        ChatSession s = new ChatSession();
        s.setTitle(title);
        s.setUserId(userId);
        return sessionRepo.save(s);
    }

    public Page<ChatSession> listSessions(Pageable pageable){
        return sessionRepo.findAll(pageable);
    }

    public ChatSession findById(Long id){
        return sessionRepo.findById(id).orElseThrow(() -> new RuntimeException("Session not found: " + id));
    }

    @Transactional
    public ChatSession rename(Long id, String title){
        var s = findById(id);
        s.setTitle(title);
        return sessionRepo.save(s);
    }

    @Transactional
    public ChatSession setFavorite(Long id, boolean favorite){
        var s = findById(id);
        s.setFavorite(favorite);
        return sessionRepo.save(s);
    }

    @Transactional
    public void delete(Long id){
        sessionRepo.deleteById(id);
    }
}
