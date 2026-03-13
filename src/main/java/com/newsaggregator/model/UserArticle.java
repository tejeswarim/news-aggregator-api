package com.newsaggregator.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "user_articles")
@Data
public class UserArticle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private String articleId;
    
    private boolean read;
    private boolean favorite;
}
