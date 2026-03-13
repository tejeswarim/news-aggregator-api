package com.newsaggregator.repository;

import com.newsaggregator.model.UserArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserArticleRepository extends JpaRepository<UserArticle, Long> {
    Optional<UserArticle> findByUserIdAndArticleId(Long userId, String articleId);
    List<UserArticle> findByUserIdAndReadTrue(Long userId);
    List<UserArticle> findByUserIdAndFavoriteTrue(Long userId);
}
