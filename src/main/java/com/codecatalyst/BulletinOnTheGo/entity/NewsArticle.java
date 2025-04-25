package com.codecatalyst.BulletinOnTheGo.entity;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
public class NewsArticle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    @Lob // Use Lob for potentially long text
    @Column(columnDefinition = "TEXT")
    private String content;
    private String source;
    private java.time.LocalDateTime publishedDate;

    public NewsArticle(String title, String content, String source) {
        this.title = title;
        this.content = content;
        this.source = source;
        this.publishedDate = java.time.LocalDateTime.now();
    }
    public NewsArticle(){}

}
