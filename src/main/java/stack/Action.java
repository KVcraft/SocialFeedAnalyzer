package stack;

import model.Post;

public class Action {
    public enum Type { ADD_USER, ADD_FOLLOW, ADD_POST }

    public final Type type;
    public final String user1;
    public final String user2; // used for follow
    public final Post post;    // used for add post

    private Action(Type t, String u1, String u2, Post p) {
        this.type = t; this.user1 = u1; this.user2 = u2; this.post = p;
    }

    public static Action addUser(String user)      { return new Action(Type.ADD_USER, user, null, null); }
    public static Action addFollow(String u1, String u2) { return new Action(Type.ADD_FOLLOW, u1, u2, null); }
    public static Action addPost(String user, Post p)    { return new Action(Type.ADD_POST, user, null, p); }
}
