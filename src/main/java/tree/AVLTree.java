package tree;

import model.Post;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AVLTree {
    private static class Node {
        Post post;
        Node left, right;
        int height;
        Node(Post p) { post = p; height = 1; }
    }

    private Node root;

    public void insert(Post post) { root = insertRec(root, post); }

    private Node insertRec(Node node, Post post) {
        if (node == null) return new Node(post);

        if (post.getTimestamp() < node.post.getTimestamp())
            node.left = insertRec(node.left, post);
        else
            node.right = insertRec(node.right, post);

        updateHeight(node);
        return rebalance(node);
    }

    // In-order traversal (ascending by timestamp). For UI feed we’ll reverse later (latest first).
    public List<Post> getAllPosts() {
        List<Post> list = new ArrayList<>();
        inOrder(root, list);

        // Sort by timestamp (newest first)
        list.sort(Comparator.comparing(Post::getTimestamp).reversed());

        return list;
    }


    private void inOrder(Node n, List<Post> list) {
        if (n == null) return;
        inOrder(n.left, list);
        list.add(n.post);
        inOrder(n.right, list);
    }

    // ---- AVL helpers ----
    private void updateHeight(Node n) { n.height = 1 + Math.max(h(n.left), h(n.right)); }
    private int h(Node n) { return n == null ? 0 : n.height; }
    private int balance(Node n) { return n == null ? 0 : h(n.left) - h(n.right); }

    private Node rebalance(Node n) {
        int b = balance(n);
        if (b > 1) {
            if (balance(n.left) < 0) n.left = rotateLeft(n.left);
            return rotateRight(n);
        }
        if (b < -1) {
            if (balance(n.right) > 0) n.right = rotateRight(n.right);
            return rotateLeft(n);
        }
        return n;
    }

    private Node rotateRight(Node y) {
        Node x = y.left, t2 = x.right;
        x.right = y; y.left = t2;
        updateHeight(y); updateHeight(x);
        return x;
    }

    private Node rotateLeft(Node x) {
        Node y = x.right, t2 = y.left;
        y.left = x; x.right = t2;
        updateHeight(x); updateHeight(y);
        return y;
    }
}
