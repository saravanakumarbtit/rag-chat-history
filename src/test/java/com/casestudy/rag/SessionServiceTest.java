package com.casestudy.rag;

import com.casestudy.rag.model.ChatSession;
import com.casestudy.rag.repository.ChatSessionRepository;
import com.casestudy.rag.service.SessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SessionServiceTest {

    private ChatSessionRepository sessionRepo;
    private SessionService service;

    @BeforeEach
    void setUp() {
        sessionRepo = mock(ChatSessionRepository.class);
        service = new SessionService(sessionRepo);
    }

    @Test
    void createSession_shouldSaveSession() {
        String title = "Test";
        String userId = "user1";
        ChatSession session = new ChatSession();
        session.setTitle(title);
        session.setUserId(userId);

        when(sessionRepo.save(any(ChatSession.class))).thenReturn(session);

        ChatSession result = service.createSession(title, userId);

        assertEquals(title, result.getTitle());
        assertEquals(userId, result.getUserId());
        verify(sessionRepo).save(any(ChatSession.class));
    }

    @Test
    void listSessions_shouldReturnPage() {
        Pageable pageable = mock(Pageable.class);
        List<ChatSession> sessions = new ArrayList<>();
        Page<ChatSession> page = new PageImpl<>(sessions);

        when(sessionRepo.findAll(pageable)).thenReturn(page);

        Page<ChatSession> result = service.listSessions(pageable);

        assertEquals(page, result);
        verify(sessionRepo).findAll(pageable);
    }

    @Test
    void findById_shouldReturnSession() {
        Long id = 1L;
        ChatSession session = new ChatSession();
        session.setId(id);

        when(sessionRepo.findById(id)).thenReturn(Optional.of(session));

        ChatSession result = service.findById(id);

        assertEquals(id, result.getId());
        verify(sessionRepo).findById(id);
    }

    @Test
    void findById_shouldThrowIfNotFound() {
        when(sessionRepo.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.findById(99L));
    }

    @Test
    void rename_shouldUpdateTitle() {
        Long id = 2L;
        String newTitle = "Renamed";
        ChatSession session = new ChatSession();
        session.setId(id);

        when(sessionRepo.findById(id)).thenReturn(Optional.of(session));
        when(sessionRepo.save(session)).thenReturn(session);

        ChatSession result = service.rename(id, newTitle);

        assertEquals(newTitle, result.getTitle());
        verify(sessionRepo).save(session);
    }

    @Test
    void setFavorite_shouldUpdateFavorite() {
        Long id = 3L;
        ChatSession session = new ChatSession();
        session.setId(id);

        when(sessionRepo.findById(id)).thenReturn(Optional.of(session));
        when(sessionRepo.save(session)).thenReturn(session);

        ChatSession result = service.setFavorite(id, true);

        assertTrue(result.isFavorite());
        verify(sessionRepo).save(session);
    }

    @Test
    void delete_shouldCallRepository() {
        Long id = 4L;
        service.delete(id);
        verify(sessionRepo).deleteById(id);
    }
}
