package com.example.zacharycolburn.programmingnews;

/**
 * Created by Zachary Colburn on 8/5/2017.
 */

class Article {
    public String title;
    public String link;
    public String source;
    public String articleText;
    public Integer fav;
    public Long date;
    public String sdate;

    // Constructor
    public Article(
        String title,
        String link,
        String source,
        String articleText,
        Integer fav,
        Long date,
        String sdate
    ) {
        this.title = title;
        this.link = link;
        this.source = source;
        this.articleText = articleText;
        this.fav = fav;
        this.date = date;
        this.sdate = sdate;
    }
}
