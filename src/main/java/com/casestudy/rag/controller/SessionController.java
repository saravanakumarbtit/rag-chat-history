package com.casestudy.rag.controller;

import com.casestudy.rag.dto.CreateSessionDto;
import com.casestudy.rag.model.ChatSession;
import com.casestudy.rag.service.SessionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sessions")
public class SessionController {
    private final SessionService svc;

    public SessionController(SessionService svc){ this.svc = svc; }

    @PostMapping
    public ResponseEntity<ChatSession> create(@RequestBody CreateSessionDto dto){
        var s = svc.createSession(dto.title(), dto.userId());
        return ResponseEntity.ok(s);
    }

    @GetMapping
    public ResponseEntity<Page<ChatSession>> list(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "20") int size){
        return ResponseEntity.ok(svc.listSessions(PageRequest.of(page, size)));
    }

    @PatchMapping("/{id}/rename")
    public ResponseEntity<ChatSession> rename(@PathVariable Long id, @RequestBody RenameRequest req){
        return ResponseEntity.ok(svc.rename(id, req.title));
    }

    @PostMapping("/{id}/favorite")
    public ResponseEntity<ChatSession> favorite(@PathVariable Long id, @RequestParam boolean favorite){
        return ResponseEntity.ok(svc.setFavorite(id, favorite));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        svc.delete(id);
        return ResponseEntity.noContent().build();
    }

    public static record RenameRequest(String title) { }
}
