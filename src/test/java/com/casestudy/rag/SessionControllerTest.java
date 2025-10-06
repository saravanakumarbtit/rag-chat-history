package com.casestudy.rag;

import com.casestudy.rag.controller.SessionController;
import com.casestudy.rag.dto.CreateSessionDto;
import com.casestudy.rag.model.ChatSession;
import com.casestudy.rag.service.SessionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SessionController.class)
class SessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SessionService sessionService;

    @Autowired
    private ObjectMapper objectMapper;

    private ChatSession session;

    @BeforeEach
    void setup() {
        session = new ChatSession();
        session.setId(1L);
        session.setTitle("Test Session");
        session.setUserId("user1");
        session.setFavorite(false);
    }

    @Test
    void testCreateSession() throws Exception {
        CreateSessionDto dto = new CreateSessionDto("Test Session", "user1");

        Mockito.when(sessionService.createSession(dto.title(), dto.userId()))
                .thenReturn(session);

        mockMvc.perform(post("/api/v1/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(session.getId()))
                .andExpect(jsonPath("$.title").value(session.getTitle()));
    }

    @Test
    void testListSessions() throws Exception {
        Mockito.when(sessionService.listSessions(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(session)));

        mockMvc.perform(get("/api/v1/sessions?page=0&size=20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(session.getId()))
                .andExpect(jsonPath("$.content[0].title").value(session.getTitle()));
    }

    @Test
    void testRenameSession() throws Exception {
        SessionController.RenameRequest req = new SessionController.RenameRequest("New Title");

        Mockito.when(sessionService.rename(eq(1L), eq("New Title")))
                .thenReturn(session);

        mockMvc.perform(patch("/api/v1/sessions/1/rename")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(session.getId()));
    }

    @Test
    void testSetFavorite() throws Exception {
        Mockito.when(sessionService.setFavorite(1L, true))
                .thenReturn(session);

        mockMvc.perform(post("/api/v1/sessions/1/favorite?favorite=true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(session.getId()));
    }

    @Test
    void testDeleteSession() throws Exception {
        Mockito.doNothing().when(sessionService).delete(1L);

        mockMvc.perform(delete("/api/v1/sessions/1"))
                .andExpect(status().isNoContent());
    }
}
