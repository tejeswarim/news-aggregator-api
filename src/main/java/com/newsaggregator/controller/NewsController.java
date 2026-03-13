package com.newsaggregator.controller;

import com.newsaggregator.dto.NewsArticle;
import com.newsaggregator.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController {
    private final NewsService newsService;
    
    @GetMapping
    public ResponseEntity<List<NewsArticle>> getNews(Authentication authentication) {
        return ResponseEntity.ok(newsService.getNewsForUser(authentication.getName()));
    }
    
    @PostMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable String id, Authentication authentication) {
        newsService.markAsRead(authentication.getName(), id);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{id}/favorite")
    public ResponseEntity<Void> markAsFavorite(@PathVariable String id, Authentication authentication) {
        newsService.markAsFavorite(authentication.getName(), id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/read")
    public ResponseEntity<List<NewsArticle>> getReadArticles(Authentication authentication) {
        return ResponseEntity.ok(newsService.getReadArticles(authentication.getName()));
    }
    
    @GetMapping("/favorites")
    public ResponseEntity<List<NewsArticle>> getFavoriteArticles(Authentication authentication) {
        return ResponseEntity.ok(newsService.getFavoriteArticles(authentication.getName()));
    }
    
    @GetMapping("/search/{keyword}")
    public ResponseEntity<List<NewsArticle>> searchNews(@PathVariable String keyword) {
        return ResponseEntity.ok(newsService.searchNews(keyword));
    }
}
