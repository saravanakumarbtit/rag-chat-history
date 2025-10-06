package com.casestudy.rag.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chat_sessions")
public class ChatSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String userId;
    private boolean favorite = false;
    private Instant createdAt = Instant.now();

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt ASC")
    @JsonManagedReference
    private List<ChatMessage> messages = new ArrayList<>();

    // Getters and setters
    public Long getId(){return id;}
    public void setId(Long id){this.id=id;}
    public String getTitle(){return title;}
    public void setTitle(String title){this.title=title;}
    public String getUserId(){return userId;}
    public void setUserId(String userId){this.userId=userId;}
    public boolean isFavorite(){return favorite;}
    public void setFavorite(boolean favorite){this.favorite=favorite;}
    public Instant getCreatedAt(){return createdAt;}
    public void setCreatedAt(Instant createdAt){this.createdAt=createdAt;}
    public List<ChatMessage> getMessages(){return messages;}
    public void setMessages(List<ChatMessage> messages){this.messages=messages;}
}
