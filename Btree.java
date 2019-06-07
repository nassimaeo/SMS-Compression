package com.simopr.smscompress.algorithms;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;


/**
 *  Binary tree of the sorted codes and their respective 8bits characters assigned to them
 */
public class Btree {

    /**
     * to sort a list of nodes using their weights
     */
    private class NodesComparator implements Comparator<Node>{
        @Override
        public int compare(Node o1, Node o2) {
            int weight = o1.getWeight() - o2.getWeight();
            if (weight != 0)
                return weight;
            else
                return o1.getCode() - o2.getCode();
        }
    }

    // A binary tree where the leaf nodes represent all the Characters
    // found in the message.
    // Binary tree using weight as the number of times a Character appears
    private LinkedList<Node> btree;


    // the list of the codes generated for the compression and their respective
    // string values (string of 0 and 1 representing the binary code)
    private HashMap<Byte, String> codes;


    // the bits stream of the binary tree to send along with
    // the compressed sms
    private Stream transmit;

    /**
     * Create a Btree from the Bytes contained in fDict
     * fDict contains the Chars and their respective frequencies.
     *
     * @param fDict frequency of each byte return as a hash dictionary
     */
    public Btree(HashMap<Byte, Integer> fDict){

        if (fDict == null || fDict.isEmpty()) throw new NullPointerException();

        // Get the number of characters in the dico
        this.N = fDict.size();

        // initialize the btree
        this.btree = new LinkedList<Node>();

        // initialize the list of codes that will be generated for the compression
        this.codes = new HashMap<Byte, String>();

        // the stream of bits representing the message to transmit
        // the Btree along with the compressed message
        this.transmit = null;

        // create a list of leaf nodes used to start merging them
        // merge the nodes with the smallest weights first
        // it's a forest of single nodes
        for (Byte key : fDict.keySet()) {
            Node e = new Node(key, fDict.get(key), null, null);
            this.btree.add(e);
        }

        // sort the list using the weights from the smallest (first element)
        // to the last element (the biggest)
        NodesComparator comparator = new NodesComparator();
        Collections.sort(this.btree, comparator);


        // keep merging the tree in the forest until we get only one root
        while (this.length() > 1){
            this.merge();
        }

    }


    /**
     * number of trees in the forest Btree
     *
     * @return the number of trees in the forest
     */
    public int length(){
        return this.btree.size();
    }

    /**
     * merge two trees in the forest (the smallest ones)
     * remove them for the forest but insert them as leafs of the new result node,
     * than insert the result back into the forest (in the right position accoding to the weight)
     */
    private void merge(){
        // get the first two smallest elements
        Node nodeA = this.btree.removeFirst();
        Node nodeB = this.btree.removeFirst();

        // merge the two nodes into one middle node.
        // weight is the sum of the weights of the two nodes
        // the code is not important (first element).
        Node newNode = new Node((byte)0, nodeA.getWeight()+nodeB.getWeight(), nodeA, nodeB);


        //Improve this code later by doing a Binary search!

        // find the right position where to insert the new node
        int index = 0;
        for (Node node : btree) {
            if (newNode.getWeight() <= node.getWeight()){
                this.btree.add(index, newNode);
                // check this code?! bad practice?
                return;
            }
            index++;
        }
        // the biggest node, insert at the end
        this.btree.addLast(newNode);
    }



    /**
     * Get the binary representation of the tree
     * @return
     * @throws Exception
     */
    public Stream writeTrie() {

        if (this.length() != 1 ){
            System.out.println("(Wrong Trie) more than one root node");
            throw new NullPointerException();
        }

        if (this.transmit == null){
            this.transmit = new Stream();
            this.preorder(this.btree.getFirst());
        }
        return this.transmit;
    }
    /**
     * Helper function for preorder traversal of the btree
     *
     * traverse the btree in preorder, putting 0 if left
     * 1 if right, and when a leaf is reached put the Char
     *
     * @param node
     */
    private void preorder(Node node){
        if (node.isLeaf()){
            this.transmit.addBit(true);
            this.transmit.addByte(node.getCode());
            return;
        }
        this.transmit.addBit(false);
        this.preorder(node.getLeft());
        this.preorder(node.getRight());
    }


    /**
     * Reconstruct the trie after reading it as a Stream
     *
     * public void reconstructTrie
     *
     * @param stream
     */

    private Iterator<Boolean> transmitIterator;
    private int N; // number of characters in the trie (8 bits after the first 3bits)
    private int restN = 0; //used only by readTrie() to reconstitute the trie

    public Btree(Stream stream){

        if (stream == null || stream.numberOfBits() < 8)
            throw new NullPointerException();

        // save the trie in the object
        this.transmit = stream;
        this.transmitIterator = this.transmit.iterator();


        // read number of characters in the trie (8bits)
        this.N = 0;
        for (int i = 0; i < 8; i++) {
            if (transmitIterator.next())
                N = N | (0b10000000 >>> i);
        }

        // (there is no empty trie) so we use 0 to represent the last byte
        // 2^8 = 256 (cases)
        if (N==0) N = 256;

        //System.out.println("Number Of char in the trie:" + N);

        this.restN = N;

        // initialize the root for the Btrie
        this.btree = new LinkedList<Node>();
        this.btree.add(this.readTrie());

        // wrong trie
        if (this.restN != 0) throw new NullPointerException();

        //this.printbtree();
    }


    public Node readTrie() {

        if (restN == 0) return null;

        if (transmitIterator.hasNext()) {
            if (transmitIterator.next()) {
                Byte current = new Byte((byte) 0);
                for (int i = 0; i < 8; i++){

                    if (!transmitIterator.hasNext()) throw new NullPointerException();

                    if (transmitIterator.next())
                        current = (byte) (current | (0b10000000 >>> i));
                    else
                        current = (byte) (current & ~(0b10000000 >>> i));
                }
                this.restN--;
                return new Node(current, 0, null, null);
            }
            Node left = this.readTrie();
            Node right = this.readTrie();
            return new Node((byte)0, 0, left, right);
        } else {
            //System.out.println("Error! Wrong trie");
            throw new NullPointerException();
        }
    }

    /**
     * it takes a compressed message, with a btree already installed
     * and return the expanded msg
     * @param compressedMsg
     * @return
     */
    public Stream expand(Stream compressedMsg){
        // the bits of N and Btrie in the transmitIterator already traversed
        // N retrived and btrie built in the the constructor

        // empty message content is an error
        if(!this.transmitIterator.hasNext()) throw new NullPointerException();

        //traverse the btrie from the root
        Node root = this.btree.getFirst();

        // Decompressed message
        Stream decompressedMsg = new Stream();

        while(this.transmitIterator.hasNext()) {

            Node x = root;
            //if (x == null)  throw new NullPointerException();

            // one node btrie
            if (x.isLeaf()){
                // one element in the trie (message equal "0" not "1")
                if (this.transmitIterator.next()) throw new NullPointerException();
            } else {
                while(!x.isLeaf()) {

                    if (!this.transmitIterator.hasNext()) throw new NullPointerException();

                    if (this.transmitIterator.next())
                        x = x.getRight();
                    else
                        x = x.getLeft();

                }
            }
            // read the character at the leaf and append it to decompressedMsg
            byte c = x.getCode();
            decompressedMsg.addByte(c);
        }

        // return the message after decompression
        return decompressedMsg;
    }

    /**
     * Print the forest. Each tree in the forest is printed in an indented manner
     * Showing the weight of each tree.
     * (useful for debugging during the construction of the Trie)
     */
    public void printbtree(){
        // print the list
        System.out.println("List:");
        for (Node node : btree) {
            this.printFromNode(node, 0);
        }
        System.out.println("End");
    }
    /**
     * Helper function for printbtree
     * @param node
     * @param indent
     */
    private void printFromNode(Node node, int indent){
        StringBuilder indentation = new StringBuilder("");
        for (int i = 0; i < indent; i++){
            indentation.append("  ");
        }

        if (node != null){
            System.out.println(indentation +" "+ node.getWeight() +" "+ node.getCode());
            this.printFromNode(node.getLeft(), indent+1);
            this.printFromNode(node.getRight(), indent+1);
        }
    }

    //use it for debugging

    /**
     * return the list of codes generated for each value
     * using Strings to represent the bits
     *
     * @return HashMap<Char, String>
     */
    public HashMap<Byte, String> getCodes(){
        if (this.length() != 1){
            System.out.println("Error! there is no single root");
            throw new NullPointerException();
        }else{
            //this.codes = {};
            if (this.N == 1){
                this.codes.put(this.btree.getFirst().getCode(), "0");
            }else{
                this.getCode(this.btree.getFirst(),"");
            }
            return this.codes;
        }
    }
    /** helper function for getCodes
     *
     * recursively explore the btree to build the codes.
     * If left, append 0 if right append 1, if leaf a code is reached.
     *
     * @param node
     * @param chain
     */
    public void getCode(Node node, String chain){
        if (node.isLeaf()){
            this.codes.put(node.getCode(), chain);
        }else{
            this.getCode(node.getLeft(), chain+"0");
            this.getCode(node.getRight(), chain+"1");
        }
    }

}


