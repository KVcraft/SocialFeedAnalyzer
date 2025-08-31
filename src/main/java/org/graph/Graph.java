package org.graph;

import java.util.*;

public class Graph {
    private Map<String, Set<String>> adjacencyList;

    public Graph() {
        adjacencyList = new HashMap<>();
    }

    public void addUser(String user) {
        adjacencyList.putIfAbsent(user, new HashSet<>());
    }

    public void addConnection(String user1, String user2) {
        adjacencyList.putIfAbsent(user1, new HashSet<>());
        adjacencyList.putIfAbsent(user2, new HashSet<>());
        adjacencyList.get(user1).add(user2);
        adjacencyList.get(user2).add(user1); // undirected
    }

    // ✅ Getter for users
    public Set<String> getUsers() {
        return adjacencyList.keySet();
    }

    // ✅ Getter for connections
    public Set<String> getConnections(String user) {
        return adjacencyList.getOrDefault(user, new HashSet<>());
    }
}
