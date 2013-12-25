/*************************************************************************
*
 *  Customized string symbol table for ASCII strings, implemented using a 26-way trie.
 *  Works only for strings, which contain only 26 big A-Z letters.
 *  Additional operation: isPrefix(str);
 *
 *************************************************************************/

public class BoggleTrieST<Value> {
    private static final int R = 26;        //  A-Z letters
    private static final int OFFSET = 65;      // Offset of letter A in ASCII table

    private Node root = new Node();

    private static class Node {
        private Object val;
        private Node[] next = new Node[R];
    }
    
    public enum NodeType {
        PREFIX, MATCH, NON_MATCH
    }

   /****************************************************
    * Is the key in the symbol table?
    ****************************************************/
    public boolean contains(String key) {
        return get(key) != null;
    }

    public Value get(String key) {
        Node x = get(root, key, 0);
        if (x == null) return null;
        return (Value) x.val;
    }

    private Node get(Node x, String key, int d) {
        if (x == null) return null;
        if (d == key.length()) return x;
        char c = key.charAt(d);
        return get(x.next[c - OFFSET], key, d+1);
    }

   /****************************************************
    * Insert key-value pair into the symbol table.
    ****************************************************/
    public void put(String key, Value val) {
        root = put(root, key, val, 0);
    }

    private Node put(Node x, String key, Value val, int d) {
        if (x == null) x = new Node();
        if (d == key.length()) {
            x.val = val;
            return x;
        }
        char c = key.charAt(d);
        x.next[c - OFFSET] = put(x.next[c - OFFSET], key, val, d+1);
        return x;
    }

    // find the key that is the longest prefix of s
    public String longestPrefixOf(String query) {
        int length = longestPrefixOf(root, query, 0, 0);
        return query.substring(0, length);
    }

    // find the key in the subtrie rooted at x that is the longest
    // prefix of the query string, starting at the dth character
    private int longestPrefixOf(Node x, String query, int d, int length) {
        if (x == null) return length;
        if (x.val != null) length = d;
        if (d == query.length()) return length;
        char c = query.charAt(d);
        return longestPrefixOf(x.next[c - OFFSET], query, d+1, length);
    }


    public Iterable<String> keys() {
        return keysWithPrefix("");
    }

    public Iterable<String> keysWithPrefix(String prefix) {
        Queue<String> queue = new Queue<String>();
        Node x = get(root, prefix, 0);
        collect(x, prefix, queue);
        return queue;
    }

    public boolean isPrefix(String prefix) {
        return get(root, prefix, 0) != null;
    }
    
    public NodeType getNodeType(String key) {
        Node x = get(root, key, 0);
        if (x == null) return NodeType.NON_MATCH;
        else if(x.val == null) return NodeType.PREFIX;
        else return NodeType.MATCH;
    }

    private void collect(Node x, String key, Queue<String> queue) {
        if (x == null) return;
        if (x.val != null) queue.enqueue(key);
        for (int c = 0; c < R; c++)
            collect(x.next[c - OFFSET], key + (char) c, queue);
    }


    public Iterable<String> keysThatMatch(String pat) {
        Queue<String> q = new Queue<String>();
        collect(root, "", pat, q);
        return q;
    }
 
    public void collect(Node x, String prefix, String pat, Queue<String> q) {
        if (x == null) return;
        if (prefix.length() == pat.length() && x.val != null) q.enqueue(prefix);
        if (prefix.length() == pat.length()) return;
        char next = pat.charAt(prefix.length());
        for (int c = 0; c < R; c++)
            if (next == '.' || next == c)
                collect(x.next[c - OFFSET], prefix + (char) c, pat, q);
    }

    public void delete(String key) {
        root = delete(root, key, 0);
    }

    private Node delete(Node x, String key, int d) {
        if (x == null) return null;
        if (d == key.length()) x.val = null;
        else {
            char c = key.charAt(d);
            x.next[c - OFFSET] = delete(x.next[c - OFFSET], key, d+1);
        }
        if (x.val != null) return x;
        for (int c = 0; c < R; c++)
            if (x.next[c - OFFSET] != null)
                return x;
        return null;
    }


    // test client
    public static void main(String[] args) {

        // build symbol table from standard input
        BoggleTrieST<Integer> st = new BoggleTrieST<Integer>();
        for (int i = 0; !StdIn.isEmpty(); i++) {
            String key = StdIn.readString();
            st.put(key, i);
        }

        // print results
        for (String key : st.keys()) {
            StdOut.println(key + " " + st.get(key));
        }
    }
}