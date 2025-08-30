package org.example;

public class Post {

    int postId;
    String content;
    String author;
    long timestamp;
    int likes;

    public Post(int postId, String content, String author, long timestamp, int likes){
        this.postId=postId;
        this.content=content;
        this.author=author;
        this.timestamp=timestamp;
        this.likes=likes;
    }

}
