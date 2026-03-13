package com.newsaggregator.service;

import com.newsaggregator.dto.NewsArticle;
import com.newsaggregator.model.User;
import com.newsaggregator.model.UserArticle;
import com.newsaggregator.repository.UserArticleRepository;
import com.newsaggregator.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewsService {
    private final UserRepository userRepository;
    private final UserArticleRepository userArticleRepository;
    private final WebClient.Builder webClientBuilder;
    
    @Value("${news.api.key}")
    private String apiKey;
    
    @Value("${news.api.url}")
    private String apiUrl;
    
    @Cacheable(value = "news", key = "#category")
    public List<NewsArticle> fetchNewsByCategory(String category) {
        WebClient webClient = webClientBuilder.baseUrl(apiUrl).build();
        
        Map<String, Object> response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/top-headlines")
                        .queryParam("category", category)
                        .queryParam("apiKey", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .block();
        
        if (response != null && response.containsKey("articles")) {
            List<Map<String, Object>> articles = (List<Map<String, Object>>) response.get("articles");
            return articles.stream().map(this::mapToNewsArticle).collect(Collectors.toList());
        }
        
        return Collections.emptyList();
    }
    
    @Cacheable(value = "news", key = "'search-' + #keyword")
    public List<NewsArticle> searchNews(String keyword) {
        WebClient webClient = webClientBuilder.baseUrl(apiUrl).build();
        
        Map<String, Object> response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/everything")
                        .queryParam("q", keyword)
                        .queryParam("apiKey", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .block();
        
        if (response != null && response.containsKey("articles")) {
            List<Map<String, Object>> articles = (List<Map<String, Object>>) response.get("articles");
            return articles.stream().map(this::mapToNewsArticle).collect(Collectors.toList());
        }
        
        return Collections.emptyList();
    }
    
    public List<NewsArticle> getNewsForUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.getPreferences().isEmpty()) {
            return Collections.emptyList();
        }
        
        List<NewsArticle> allNews = new ArrayList<>();
        for (String preference : user.getPreferences()) {
            allNews.addAll(fetchNewsByCategory(preference));
        }
        
        Map<String, UserArticle> userArticles = userArticleRepository.findByUserIdAndReadTrue(user.getId())
                .stream().collect(Collectors.toMap(UserArticle::getArticleId, ua -> ua));
        
        userArticles.putAll(userArticleRepository.findByUserIdAndFavoriteTrue(user.getId())
                .stream().collect(Collectors.toMap(UserArticle::getArticleId, ua -> ua)));
        
        allNews.forEach(article -> {
            UserArticle ua = userArticles.get(article.getId());
            if (ua != null) {
                article.setRead(ua.isRead());
                article.setFavorite(ua.isFavorite());
            }
        });
        
        return allNews;
    }
    
    public void markAsRead(String username, String articleId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        UserArticle userArticle = userArticleRepository.findByUserIdAndArticleId(user.getId(), articleId)
                .orElseGet(() -> {
                    UserArticle ua = new UserArticle();
                    ua.setUser(user);
                    ua.setArticleId(articleId);
                    return ua;
                });
        
        userArticle.setRead(true);
        userArticleRepository.save(userArticle);
    }
    
    public void markAsFavorite(String username, String articleId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        UserArticle userArticle = userArticleRepository.findByUserIdAndArticleId(user.getId(), articleId)
                .orElseGet(() -> {
                    UserArticle ua = new UserArticle();
                    ua.setUser(user);
                    ua.setArticleId(articleId);
                    return ua;
                });
        
        userArticle.setFavorite(true);
        userArticleRepository.save(userArticle);
    }
    
    public List<NewsArticle> getReadArticles(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return userArticleRepository.findByUserIdAndReadTrue(user.getId())
                .stream()
                .map(ua -> {
                    NewsArticle article = new NewsArticle();
                    article.setId(ua.getArticleId());
                    article.setRead(true);
                    article.setFavorite(ua.isFavorite());
                    return article;
                })
                .collect(Collectors.toList());
    }
    
    public List<NewsArticle> getFavoriteArticles(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return userArticleRepository.findByUserIdAndFavoriteTrue(user.getId())
                .stream()
                .map(ua -> {
                    NewsArticle article = new NewsArticle();
                    article.setId(ua.getArticleId());
                    article.setRead(ua.isRead());
                    article.setFavorite(true);
                    return article;
                })
                .collect(Collectors.toList());
    }
    
    @Scheduled(fixedRate = 3600000)
    public void refreshCache() {
        String[] categories = {"business", "technology", "sports", "entertainment", "health", "science"};
        for (String category : categories) {
            fetchNewsByCategory(category);
        }
    }
    
    private NewsArticle mapToNewsArticle(Map<String, Object> articleData) {
        NewsArticle article = new NewsArticle();
        article.setId(UUID.randomUUID().toString());
        article.setTitle((String) articleData.get("title"));
        article.setDescription((String) articleData.get("description"));
        article.setUrl((String) articleData.get("url"));
        article.setUrlToImage((String) articleData.get("urlToImage"));
        article.setPublishedAt((String) articleData.get("publishedAt"));
        article.setAuthor((String) articleData.get("author"));
        article.setContent((String) articleData.get("content"));
        
        Map<String, Object> source = (Map<String, Object>) articleData.get("source");
        if (source != null) {
            article.setSource((String) source.get("name"));
        }
        
        return article;
    }
}
