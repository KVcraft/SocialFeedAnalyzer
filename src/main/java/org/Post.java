package org;

public class Post {
    public int id;
    public String content;
    public int likes;
    public long timestamp; // to use in AVL

    public Post(int id, String content, int likes, long timestamp) {
        this.id = id;
        this.content = content;
        this.likes = likes;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Post{id=" + id + ", likes=" + likes + ", time=" + timestamp + ", content='" + content + "'}";
    }
}
