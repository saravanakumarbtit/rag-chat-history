package com.casestudy.rag.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "chat_messages")
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    @JsonBackReference
    private ChatSession session;

    private String sender;

    @Column(columnDefinition = "text")
    private String content;

    @Column(columnDefinition = "text")
    private String retrievedContext;

    private Instant createdAt = Instant.now();

    // Getters and setters
    public Long getId(){return id;}
    public void setId(Long id){this.id=id;}
    public ChatSession getSession(){return session;}
    public void setSession(ChatSession session){this.session=session;}
    public String getSender(){return sender;}
    public void setSender(String sender){this.sender=sender;}
    public String getContent(){return content;}
    public void setContent(String content){this.content=content;}
    public String getRetrievedContext(){return retrievedContext;}
    public void setRetrievedContext(String retrievedContext){this.retrievedContext=retrievedContext;}
    public Instant getCreatedAt(){return createdAt;}
    public void setCreatedAt(Instant createdAt){this.createdAt=createdAt;}
}
