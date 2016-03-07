package ru.spbau.mit;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Egor Gorbunov on 25.02.16.
 * email: egor-mailbox@ya.ru
 */
class TrieNode implements StreamSerializable {
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
    private static final char[] SUPPORTED_CHARACTERS = (ALPHABET + ALPHABET.toUpperCase()).toCharArray();
    private static final byte ALPHABET_LEN = (byte) ALPHABET.length();
    private static final byte LETTER_NUM = (byte) SUPPORTED_CHARACTERS.length;

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

    public void reset() {
        for (char c : SUPPORTED_CHARACTERS) {
            set(c, null);
        }
    }

    @Override
    public void serialize(OutputStream out) {
        assert(Byte.MAX_VALUE >= map.length);

        byte notNullCnt = 0;
        for (TrieNode node : map) {
            if (node != null) {
                notNullCnt++;
            }
        }
        byte isWordByte = (byte) (isWord ? 1 : 0);
        try {
            out.write(notNullCnt);
            out.write(isWordByte);
            for (char c : SUPPORTED_CHARACTERS) {
                TrieNode node = get(c);
                if (node != null) {
                    out.write((byte) c);
                    node.serialize(out);
                }
            }
        } catch (IOException e) {
            throw new SerializationException();
        }
    }

    @Override
    public void deserialize(InputStream in) {
        try {
            int notNullCnt = in.read();
            assert(notNullCnt != -1);
            wordsInSubtree = in.read();
            assert(wordsInSubtree != -1);
            isWord = (wordsInSubtree == 1);
            for (int i = 0; i < notNullCnt; ++i) {
                char c = (char) in.read();
                TrieNode newNode = new TrieNode();
                newNode.deserialize(in);
                set(c, newNode);
                wordsInSubtree += newNode.wordsInSubtree;
            }
        } catch (IOException e) {
            throw new SerializationException();
        }
    }
}
