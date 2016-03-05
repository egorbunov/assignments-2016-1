package ru.spbau.mit;

/**
 * Created by Egor Gorbunov on 25.02.16.
 * email: egor-mailbox@ya.ru
 */
class TrieNode {
    private static final short ALPHABET_LEN = 'z' - 'a' + 1;
    private static final short LETTER_NUM = 2 * ALPHABET_LEN;

    private int wordsInSubtree = 0; // number of words in subtree, including this node
    private boolean isWord = false; // true, if word ends on that node
    private final TrieNode[] map = new TrieNode[LETTER_NUM];

    public TrieNode get(char c) {
        return map[idx(c)];
    }

    public void set(char c, TrieNode child) {
        map[idx(c)] = child;
    }

    public boolean isWord() {
        return isWord;
    }

    public void setWord(boolean word) {
        isWord = word;
    }

    public void incWordsInSubtree() {
        wordsInSubtree++;
    }

    public void decWordsInSubtree() {
        wordsInSubtree--;
    }

    public int getWordsInSubtree() {
        return wordsInSubtree;
    }

    private int idx(char c) {
        if (c <= 'z' && c >= 'a') {
            return c - 'a';
        } else if (c <= 'Z' && c >= 'A') {
            return c - 'A' + ALPHABET_LEN;
        } else {
            throw new IllegalArgumentException("Not alphabetic characters not supported!");
        }
    }
}
