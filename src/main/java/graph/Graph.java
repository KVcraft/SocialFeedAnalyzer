package graph;

import heap.TrendingHeap;
import model.Post;
import tree.AVLTree;

import java.util.*;

public class Graph {
    private final Map<String, Set<String>> adjacencyList = new HashMap<>();
    private final Map<String, AVLTree> userPosts = new HashMap<>();
    private final TrendingHeap trending = new TrendingHeap(12); // keep Top-12 trending posts

    // --- Users ---
    public void addUser(String userId) {
        adjacencyList.putIfAbsent(userId, new HashSet<>());
        userPosts.putIfAbsent(userId, new AVLTree());
    }

    public void removeUser(String userId) {
        // Remove edges pointing to/from user
        adjacencyList.remove(userId);
        for (Set<String> follows : adjacencyList.values()) follows.remove(userId);
        // Remove posts tree
        userPosts.remove(userId);
        // Rebuild trending (simple approach)
        rebuildTrending();
    }

    // --- Follows (directional) ---
    public void addConnection(String follower, String followee, boolean bidirectional) {
        if (Objects.equals(follower, followee)) return;
        addUser(follower); addUser(followee);
        adjacencyList.get(follower).add(followee);
        if (bidirectional) adjacencyList.get(followee).add(follower);
    }

    public void removeConnection(String follower, String followee) {
        Set<String> set = adjacencyList.get(follower);
        if (set != null) set.remove(followee);
    }

    public Set<String> getConnections(String userId) { // who userId follows
        return adjacencyList.getOrDefault(userId, Collections.emptySet());
    }

    public Set<String> getFollowers(String userId) { // who follows userId
        Set<String> r = new HashSet<>();
        for (Map.Entry<String, Set<String>> e : adjacencyList.entrySet()) {
            if (e.getValue().contains(userId)) r.add(e.getKey());
        }
        return r;
    }

    public Set<String> getAllUsers() { return adjacencyList.keySet(); }

    // --- Posts ---
    public void addPost(String userId, Post post) {
        addUser(userId);
        userPosts.get(userId).insert(post);
        trending.add(post);
    }

    public boolean removePost(String userId, int postId) {
        // Lightweight removal: rebuild user’s tree without that post
        var tree = userPosts.get(userId);
        if (tree == null) return false;
        var all = tree.getAllPosts();
        boolean removed = all.removeIf(p -> p.getId() == postId);
        if (removed) {
            // rebuild tree
            AVLTree newTree = new AVLTree();
            for (Post p : all) newTree.insert(p);
            userPosts.put(userId, newTree);
            rebuildTrending();
        }
        return removed;
    }

    public List<Post> getUserPostsChronological(String userId) {
        var tree = userPosts.get(userId);
        if (tree == null) return List.of();
        var asc = tree.getAllPosts();      // oldest -> newest
        Collections.reverse(asc);          // newest -> oldest for UI
        return asc;
    }

    public List<Post> getAllPosts() {
        List<Post> all = new ArrayList<>();
        for (AVLTree t : userPosts.values()) all.addAll(t.getAllPosts());
        return all;
    }

    public List<Post> getTrendingTopK() { return trending.topK(); }

    private void rebuildTrending() {
        trending.clear();
        for (Post p : getAllPosts()) trending.add(p);
    }
}
