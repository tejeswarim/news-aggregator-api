package com.newsaggregator.dto;

import lombok.Data;

@Data
public class NewsArticle {
    private String id;
    private String title;
    private String description;
    private String url;
    private String urlToImage;
    private String publishedAt;
    private String source;
    private String author;
    private String content;
    private boolean read;
    private boolean favorite;
}
