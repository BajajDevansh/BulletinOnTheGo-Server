package com.codecatalyst.BulletinOnTheGo.entity;



import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;


@Document(collection = "news_article")
@Data
public class NewsArticle {
    @Id
    private String id; // Switch to String ID

    private String title;
    private String content; // No need for @Lob or @Column
    private String source;
    private LocalDateTime publishedDate;

    public NewsArticle(String title, String content, String source) {
        this.title = title;
        this.content = content;
        this.source = source;
        this.publishedDate = java.time.LocalDateTime.now();
    }
    public NewsArticle(){}

}
