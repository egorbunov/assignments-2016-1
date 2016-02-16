package ru.spbau.mit;

/**
 * Created by Egor Gorbunov on 16.02.16.
 * email: egor-mailbox@ya.ru
 */
public class StringSetImpl implements StringSet {
    private TrieNode root = new TrieNode();

    @Override
    public boolean add(String element) {
        if (contains(element)) {
            return false;
        }
        TrieNode node = find(element, ADD_WORD_STRATEGY);
        assert node != null;
        node.incWordsInSubtree();
        node.setWord(true);
        return true;
    }

    @Override
    public boolean contains(String element) {
        TrieNode node = find(element, SIMPLE_STRATEGY);
        return node != null && node.isWord();
    }

    @Override
    public boolean remove(String element) {
        if (!contains(element)) {
            return false;
        }
        TrieNode node = find(element, DELETE_WORD_STRATEGY);
        node.decWordsInSubtree();
        node.setWord(false);
        return true;
    }

    @Override
    public int size() {
        return root.getWordsInSubtree();
    }

    @Override
    public int howManyStartsWithPrefix(String prefix) {
        TrieNode node = find(prefix, SIMPLE_STRATEGY);
        if (node == null) {
            return 0;
        }
        return node.getWordsInSubtree();
    }

    private TrieNode find(String str, GoStrategy strategy) {
        TrieNode cur = root;
        for (int i = 0; cur != null && i < str.length(); i++) {
            cur = strategy.go(cur, str.charAt(i));
        }
        return cur;
    }

    private interface GoStrategy {
        TrieNode go(TrieNode from, char c);
    }

    private static final GoStrategy SIMPLE_STRATEGY = new GoStrategy() {
        @Override
        public TrieNode go(TrieNode from, char c) {
            return from.get(c);
        }
    };
    private static final GoStrategy ADD_WORD_STRATEGY = new GoStrategy() {
        @Override
        public TrieNode go(TrieNode from, char c) {
            from.incWordsInSubtree();
            if (from.get(c) == null) {
                from.set(c, new TrieNode());
            }
            return from.get(c);
        }
    };
    private static final GoStrategy DELETE_WORD_STRATEGY = new GoStrategy() {
        @Override
        public TrieNode go(TrieNode from, char c) {
            from.decWordsInSubtree();
            TrieNode toReturn = from.get(c);
            if (from.getWordsInSubtree() == 0) {
                // case word to delete is alone in it's branch,
                // this branch becomes free after word deletion
                from.set(c, null);
            }
            return toReturn;
        }
    };
}
