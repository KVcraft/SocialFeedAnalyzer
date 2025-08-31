package org.avl;

import org.Post;

import static java.lang.Math.max;

class AVLNode {
    Post post;
    AVLNode left, right;
    int height;

    AVLNode(Post post) {
        this.post = post;
        height = 1;
    }
}

public class PostAVLTree {
    private AVLNode root;

    private int height(AVLNode node) { return (node == null) ? 0 : node.height; }
    private int getBalance(AVLNode node) { return (node == null) ? 0 : height(node.left) - height(node.right); }

    private AVLNode rotateRight(AVLNode y) {
        AVLNode x = y.left, T2 = x.right;
        x.right = y; y.left = T2;
        y.height = max(height(y.left), height(y.right)) + 1;
        x.height = max(height(x.left), height(x.right)) + 1;
        return x;
    }

    private AVLNode rotateLeft(AVLNode x) {
        AVLNode y = x.right, T2 = y.left;
        y.left = x; x.right = T2;
        x.height = max(height(x.left), height(x.right)) + 1;
        y.height = max(height(y.left), height(y.right)) + 1;
        return y;
    }

    private AVLNode insert(AVLNode node, Post post) {
        if (node == null) return new AVLNode(post);

        if (post.timestamp < node.post.timestamp) node.left = insert(node.left, post);
        else if (post.timestamp > node.post.timestamp) node.right = insert(node.right, post);
        else return node;

        node.height = 1 + max(height(node.left), height(node.right));
        int balance = getBalance(node);

        if (balance > 1 && post.timestamp < node.left.post.timestamp) return rotateRight(node);
        if (balance < -1 && post.timestamp > node.right.post.timestamp) return rotateLeft(node);
        if (balance > 1 && post.timestamp > node.left.post.timestamp) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }
        if (balance < -1 && post.timestamp < node.right.post.timestamp) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }
        return node;
    }

    public void addPost(Post post) { root = insert(root, post); }

    public void printInOrder() { printInOrder(root); }
    private void printInOrder(AVLNode node) {
        if (node != null) {
            printInOrder(node.left);
            System.out.println(node.post);
            printInOrder(node.right);
        }
    }
}

