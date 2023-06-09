package com.example.miraihellp.service;

import java.util.*;

/**
 * @Author indinner
 * @Date 2023/6/8 21:29
 * @Version 1.0
 * @Doc:
 */
public class SensitiveWordsFilter {
    private Trie trie = new Trie();

    public SensitiveWordsFilter(List<String> words) {
        for (String word : words) {
            trie.insert(word);
        }
        trie.buildFailLink();
    }

    public boolean containsSensitiveWords(String str) {
        TrieNode current = trie.getRoot();
        for (char c : str.toCharArray()) {
            while (current != null && !current.getChildren().containsKey(c)) {
                current = current.getFailNode();
            }
            if (current == null) {
                current = trie.getRoot();
            } else {
                current = current.getChildren().get(c);
            }
            if (current.isEndOfWord()) {
                return true;
            }
        }
        return false;
    }
}

class TrieNode {
    private Map<Character, TrieNode> children = new HashMap<>();
    private boolean isEndOfWord = false;
    private TrieNode failNode = null;

    public Map<Character, TrieNode> getChildren() {
        return children;
    }

    public boolean isEndOfWord() {
        return isEndOfWord;
    }

    public void setEndOfWord(boolean endOfWord) {
        isEndOfWord = endOfWord;
    }

    public TrieNode getFailNode() {
        return failNode;
    }

    public void setFailNode(TrieNode failNode) {
        this.failNode = failNode;
    }
}

class Trie {
    private TrieNode root = new TrieNode();

    public void insert(String word) {
        TrieNode current = root;
        for (char c : word.toCharArray()) {
            current.getChildren().putIfAbsent(c, new TrieNode());
            current = current.getChildren().get(c);
        }
        current.setEndOfWord(true);
    }

    public void buildFailLink() {
        Queue<TrieNode> queue = new LinkedList<>();
        root.getChildren().values().forEach(queue::offer);
        while (!queue.isEmpty()) {
            TrieNode node = queue.poll();
            for (Map.Entry<Character, TrieNode> entry : node.getChildren().entrySet()) {
                char c = entry.getKey();
                TrieNode child = entry.getValue();
                queue.offer(child);
                TrieNode failNode = node.getFailNode();
                while (failNode != null && !failNode.getChildren().containsKey(c)) {
                    failNode = failNode.getFailNode();
                }
                child.setFailNode(failNode != null ? failNode.getChildren().get(c) : root);
            }
        }
    }

    public TrieNode getRoot() {
        return root;
    }
}

