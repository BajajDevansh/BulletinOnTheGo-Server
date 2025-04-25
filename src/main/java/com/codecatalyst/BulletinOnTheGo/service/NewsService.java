package com.codecatalyst.BulletinOnTheGo.service;

import com.codecatalyst.BulletinOnTheGo.entity.NewsArticle;
import com.codecatalyst.BulletinOnTheGo.repositories.NewsArticleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsService {

    private final NewsArticleRepository newsArticleRepository;
    public NewsService(NewsArticleRepository newsArticleRepository){
        this.newsArticleRepository=newsArticleRepository;
    }

    public List<NewsArticle> getLatestNews() {
        return newsArticleRepository.findByOrderByPublishedDateDesc();
    }
    @PostConstruct
    public void initDummyData() {
        if (newsArticleRepository.count() == 0) {
            newsArticleRepository.save(new NewsArticle("Local Weather Update", "Expect sunshine and moderate temperatures throughout the week.", "Local News"));
            newsArticleRepository.save(new NewsArticle("Tech Stocks Rally", "Major technology shares saw significant gains in early trading today.", "Finance Today"));
            newsArticleRepository.save(new NewsArticle("New City Park Opens", "The downtown revitalization project includes a new green space for residents.", "City Gazette"));
        }
    }
}

