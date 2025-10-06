package com.casestudy.rag;

import com.casestudy.rag.model.ChatMessage;
import com.casestudy.rag.model.ChatSession;
import com.casestudy.rag.repository.ChatMessageRepository;
import com.casestudy.rag.repository.ChatSessionRepository;
import com.casestudy.rag.service.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MessageServiceTest {

    private ChatMessageRepository messageRepo;
    private ChatSessionRepository sessionRepo;
    private MessageService service;

    @BeforeEach
    void setUp() {
        messageRepo = mock(ChatMessageRepository.class);
        sessionRepo = mock(ChatSessionRepository.class);
        service = new MessageService(messageRepo, sessionRepo);
    }

    @Test
    void addMessage_shouldSaveMessageAndSession() {
        Long sessionId = 1L;
        String sender = "user";
        String content = "Hello";
        String context = "context";
        ChatSession session = new ChatSession();
        session.setId(sessionId);
        session.setMessages(new ArrayList<>());

        when(sessionRepo.findById(sessionId)).thenReturn(Optional.of(session));
        when(messageRepo.save(any(ChatMessage.class))).thenAnswer(i -> {
            ChatMessage m = i.getArgument(0);
            m.setId(10L);
            return m;
        });

        ChatMessage result = service.addMessage(sessionId, sender, content, context);

        assertNotNull(result);
        assertEquals(sender, result.getSender());
        assertEquals(content, result.getContent());
        assertEquals(context, result.getRetrievedContext());
        assertEquals(session, result.getSession());
        verify(messageRepo).save(any(ChatMessage.class));
        verify(sessionRepo).save(session);
        assertTrue(session.getMessages().contains(result));
    }

    @Test
    void addMessage_shouldThrowIfSessionNotFound() {
        when(sessionRepo.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.addMessage(99L, "user", "msg", "ctx"));
    }

    @Test
    void getMessages_shouldCallRepository() {
        Long sessionId = 2L;
        Pageable pageable = mock(Pageable.class);
        List<ChatMessage> messages = new ArrayList<>();
        Page<ChatMessage> page = new org.springframework.data.domain.PageImpl<>(messages);

        when(messageRepo.findBySessionIdOrderByCreatedAtAsc(sessionId, pageable)).thenReturn(page);

        Page<ChatMessage> result = service.getMessages(sessionId, pageable);

        assertEquals(page, result);
        verify(messageRepo).findBySessionIdOrderByCreatedAtAsc(sessionId, pageable);
    }

}
