package com.simopr.smscompress.algorithms;

/**
 * A node in a Btree used to construct the optimal prefix codes
 * data structure for a node in the btree
 *
 */

public class Node implements Comparable<Node> {
    private byte code;
    private int weight;
    private Node left, right;

    public Node(byte code, int weight, Node nodeA, Node nodeB) {
        this.code = code;
        this.weight = weight;
        this.left = nodeA;
        this.right = nodeB;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public boolean isLeaf() {
        return left == null && right == null;
    }

    public byte getCode() {
        return this.code;
    }

    public Node getLeft() {
        return this.left;
    }

    public Node getRight() {
        return this.right;
    }

    @Override
    public int compareTo(Node that) {
        return this.getWeight() - that.getWeight();
    }
}