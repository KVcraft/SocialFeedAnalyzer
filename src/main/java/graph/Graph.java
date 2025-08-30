package graph;

import java.util.*;

public class Graph {
    private Map<String, List<String>> adjacencyList;

    public Graph() {
        adjacencyList = new HashMap<>();
    }

    // Add new user (node)
    public void addUser(String userId) {
        adjacencyList.putIfAbsent(userId, new ArrayList<>());
    }

    // Add connection (edge)
    public void addConnection(String user1, String user2) {
        adjacencyList.putIfAbsent(user1, new ArrayList<>());
        adjacencyList.putIfAbsent(user2, new ArrayList<>());

        adjacencyList.get(user1).add(user2);  // user1 follows user2
    }

    // Get all connections of a user
    public List<String> getConnections(String userId) {
        return adjacencyList.getOrDefault(userId, new ArrayList<>());
    }

    // Get all users
    public Set<String> getAllUsers() {
        return adjacencyList.keySet();
    }
}
