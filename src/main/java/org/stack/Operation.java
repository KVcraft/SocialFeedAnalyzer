package org.stack;

import org.avl.Post;

public class Operation {
    public enum Type { ADD, REMOVE, UPDATE }

    public Type type;
    public Post post;
    public int oldLikes;
    public int newLikes;

    // For ADD/REMOVE
    public Operation(Type type, Post post) {
        this.type = type;
        this.post = post;
    }

    // For UPDATE
    public Operation(Type type, Post post, int oldLikes, int newLikes) {
        this.type = type;
        this.post = post;
        this.oldLikes = oldLikes;
        this.newLikes = newLikes;
    }
}