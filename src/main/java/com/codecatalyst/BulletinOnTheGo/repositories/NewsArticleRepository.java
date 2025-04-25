package com.codecatalyst.BulletinOnTheGo.repositories;

import com.codecatalyst.BulletinOnTheGo.entity.NewsArticle;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NewsArticleRepository extends MongoRepository<NewsArticle, String> {

    List<NewsArticle> findByOrderByPublishedDateDesc();
}
