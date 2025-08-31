package graph;

import java.util.*;

/**
 * Memory-Efficient Social Graph
 * -----------------------------
 * This class models a social network graph using an adjacency list.
 * Optimized with HashSet to prevent duplicate connections and reduce lookup time.
 */
public class Graph {
    // Map of user -> set of connected users (who they follow)
    private final Map<String, Set<String>> adjacencyList;

    public Graph() {
        adjacencyList = new HashMap<>();
    }

    /** Add new user (node) */
    public void addUser(String userId) {
        adjacencyList.putIfAbsent(userId, new HashSet<>());
    }

    /**
     * Add connection (edge) between two users.
     * By default, this is directional: user1 follows user2.
     * If bidirectional is true, both follow each other.
     */
    public void addConnection(String user1, String user2, boolean bidirectional) {
        if (user1.equals(user2)) {
            System.out.println("❌ A user cannot follow themselves: " + user1);
            return;
        }

        addUser(user1);
        addUser(user2);

        adjacencyList.get(user1).add(user2);

        if (bidirectional) {
            adjacencyList.get(user2).add(user1);
        }
    }

    /** Get all connections (who this user follows) */
    public Set<String> getConnections(String userId) {
        return adjacencyList.getOrDefault(userId, Collections.emptySet());
    }

    /** Get all users in the graph */
    public Set<String> getAllUsers() {
        return adjacencyList.keySet();
    }

    /** Get number of connections (out-degree) for a user */
    public int getFollowCount(String userId) {
        return adjacencyList.getOrDefault(userId, Collections.emptySet()).size();
    }

    /** Get followers (who follows this user) */
    public Set<String> getFollowers(String userId) {
        Set<String> followers = new HashSet<>();
        for (Map.Entry<String, Set<String>> entry : adjacencyList.entrySet()) {
            if (entry.getValue().contains(userId)) {
                followers.add(entry.getKey());
            }
        }
        return followers;
    }

    /** Find mutual connections (common friends) */
    public Set<String> getMutualConnections(String user1, String user2) {
        Set<String> connections1 = new HashSet<>(getConnections(user1));
        connections1.retainAll(getConnections(user2));
        return connections1;
    }

    /** Suggest friends: friends of friends (excluding existing ones) */
    public Set<String> suggestFriends(String userId) {
        Set<String> suggestions = new HashSet<>();
        for (String friend : getConnections(userId)) {
            for (String fof : getConnections(friend)) {
                if (!fof.equals(userId) &&
                        !getConnections(userId).contains(fof)) {
                    suggestions.add(fof);
                }
            }
        }
        return suggestions;
    }

    /** Debugging: print full graph */
    public void printGraph() {
        for (String user : adjacencyList.keySet()) {
            System.out.println(user + " → " + adjacencyList.get(user));
        }
    }
}
