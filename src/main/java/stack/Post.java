package stack;

public class Post {
    int id;
    String content;
    int likes;

    public Post(int id, String content, int likes) {
        this.id = id;
        this.content = content;
        this.likes = likes;
    }

    @Override
    public String toString() {
        return "Post{id=" + id + ", likes=" + likes + ", content='" + content + "'}";
    }
}