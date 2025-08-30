package org.avl;


import static java.lang.Math.max;

class AVLNode{
    heap.Post post;
    AVLNode left, right;
    int height;

    AVLNode (heap.Post post){
        this.post=post;
        height=1;
    }
}

public class PostAVLTree {

    private AVLNode root;

    //Get the height of the node
    private int height(AVLNode node){
        if (node==null){
           return 0;
        }
        return node.height;
    }

    //Get balance factor
    private int getBalance(AVLNode node){
        if (node==null){
            return 0;
        }
        return height(node.left) - height(node.right);
    }

    //Right rotate subtree rooted with node
    private AVLNode rotateRight(AVLNode node){
        AVLNode leftChild = node.left;
        AVLNode temp = leftChild.right;

        //Perform rotation
        leftChild.right = node;
        node.left = temp;

        //Update heights
        node.height = max(height(node.left), height(node.right)) + 1;
        leftChild.height = max(height(leftChild.left), height(leftChild.right)) + 1;

        return leftChild;
    }

    //Left rotate subtree rooted with node
    private AVLNode rotateLeft(AVLNode node){
        AVLNode rightChild = node.right;
        AVLNode temp = rightChild.left;

        //Perform rotation
        rightChild.left = node;
        node.right = temp;

        //Update heights
        node.height = max(height(node.left), height(node.right)) + 1;
        rightChild.height = max(height(rightChild.left), height(rightChild.right)) + 1;

        return rightChild;
    }

    //Insert a post into AVL tree
    private  AVLNode insertPost(AVLNode root, heap.Post post){
        if (root == null){
            return new AVLNode(post);
        }

        //Sort by timestamp
        if(post.timestamp < root.post.timestamp){
            root.left = insertPost(root.left, post);
        } else if(post.timestamp >root.post.timestamp){
            root.right = insertPost(root.right, post);
        } else{
            return root;
        }

        //Update height of root
        root.height = 1 + max(height(root.left), height(root.right));

        //Get balance factor
        int balance = getBalance(root);

        //LL case
        if(balance > 1 && post.timestamp < root.left.post.timestamp){
            return rotateRight(root);
        }

        //RR case
        if(balance < -1 && post.timestamp < root.right.post.timestamp){
            return rotateLeft(root);
        }

        //LR case
        if (balance > 1 && post.timestamp > root.left.post.timestamp) {
            root.left = rotateLeft(root.left);
            return rotateRight(root);
        }

        //RL case
        if (balance < -1 && post.timestamp > root.right.post.timestamp) {
            root.left = rotateRight(root.left);
            return rotateLeft(root);
        }

        return  root;
    }

    //Print tree inOrder
    public void printInOrder(AVLNode node){
        if(node != null){
            printInOrder(node.left);
            System.out.print(node.post.content+ " (likes :" +node.post.likes+ ")");
            printInOrder(node.right);
        }
    }

}
