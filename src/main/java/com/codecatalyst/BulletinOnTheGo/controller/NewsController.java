package com.codecatalyst.BulletinOnTheGo.controller;

import com.codecatalyst.BulletinOnTheGo.entity.NewsArticle;
import com.codecatalyst.BulletinOnTheGo.service.NewsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/news")
@CrossOrigin(origins = "http://localhost:3000") // Allow frontend access
public class NewsController {

    private final NewsService newsService;
    public NewsController(NewsService newsService){
        this.newsService=newsService;
    }

    @GetMapping
    public List<NewsArticle> getNews() {
        return newsService.getLatestNews();
    }
}

