package stack;

import graph.Graph;
import model.Post;

import java.util.ArrayDeque;
import java.util.Deque;

public class ActionStack {
    private final Deque<Action> stack = new ArrayDeque<>();

    public void push(Action a) { stack.push(a); }

    public boolean undo(Graph graph) {
        if (stack.isEmpty()) return false;
        Action a = stack.pop();
        switch (a.type) {
            case ADD_USER:
                graph.removeUser(a.user1); // soft-remove (removes edges & posts)
                return true;
            case ADD_FOLLOW:
                graph.removeConnection(a.user1, a.user2);
                return true;
            case ADD_POST:
                graph.removePost(a.user1, a.post.getId());
                return true;
        }
        return false;
    }

    public boolean isEmpty() { return stack.isEmpty(); }
}
