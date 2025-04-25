package com.codecatalyst.BulletinOnTheGo.repositories;

import com.codecatalyst.BulletinOnTheGo.entity.NewsArticle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NewsArticleRepository extends JpaRepository<NewsArticle, Long> {
    List<NewsArticle> findByOrderByPublishedDateDesc();
}
