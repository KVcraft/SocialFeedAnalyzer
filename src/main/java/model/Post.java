package model;

public class Post {
    private final int id;
    private final String content;
    private final String userId;
    private final long timestamp; // epoch millis
    private int likes;

    public Post(int id, String content, String userId, long timestamp, int likes) {
        this.id = id;
        this.content = content;
        this.userId = userId;
        this.timestamp = timestamp;
        this.likes = likes;
    }

    public int getId() { return id; }
    public String getContent() { return content; }
    public String getUserId() { return userId; }
    public long getTimestamp() { return timestamp; }
    public int getLikes() { return likes; }
    public void setLikes(int likes) { this.likes = likes; }

    @Override
    public String toString() {
        return "[" + userId + "] " + content + " | Likes: " + likes + " | t=" + timestamp;
    }
}
