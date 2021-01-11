import java.util.Objects;

public class MyHashMap<K, V> {
    static final int MAX_CAPACITY = Integer.MAX_VALUE / 2;
    static final int DEFAULT_CAPACITY = 5;
    static final float DEFAULT_LOAD_FACTOR = 0.75f;


    static class Entry<K, V> {
        int hash;
        final K key;
        V value;
        Entry<K, V> next;

        public Entry(int hash, K key, V value, Entry<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        public int getHash() {
            return hash;
        }

        public void setHash(int hash) {
            this.hash = hash;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }

        public Entry<K, V> getNext() {
            return next;
        }

        public void setNext(Entry<K, V> next) {
            this.next = next;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Entry<?, ?> entry = (Entry<?, ?>) o;
            return key.equals(entry.key) &&
                    Objects.equals(value, entry.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, value);
        }
    }

    private int threshold;
    private int size;
    private final float loadFactor;

    private Entry<K, V>[] table;

    public MyHashMap() {
        size = 0;
        loadFactor = DEFAULT_LOAD_FACTOR;
        threshold = Math.round(DEFAULT_CAPACITY * loadFactor);

        table = new Entry[DEFAULT_CAPACITY];
    }

    public MyHashMap(int capacity ,float loadFactor) {
        size = 0;
        this.loadFactor = loadFactor;
        threshold = Math.round(capacity * loadFactor);

        table = new Entry[capacity];
    }

    public void put(K key, V value) {
        if (key == null) {
            putForNullKey(value);
            return;
        }

        int hash = key.hashCode();
        int index = indexFor(hash, table.length);

        Entry entry = table[index];

        while (entry != null) {
            if (entry.hash == hash && (entry.key == key || key.equals(entry.key))) {
                entry.value = value;
                return;
            }
            entry = entry.next;
        }
        if (size + 1 >= threshold) {
            resize(table.length * 2);
        }
        size++;
        addEntry(hash, key, value, index);
    }

    private void putForNullKey(V value) {
        Entry entry = table[0];

        while (entry != null) {
            if (entry.key == null) {
                entry.value = value;
                return;
            }
            entry = entry.next;
        }
        addEntry(0, null, value, 0);
    }

    static int indexFor(int h, int length) {
        return h & (length - 1);
    }

    void addEntry(int hash, K key, V value, int index) {
        if (size + 1 > threshold) {
            resize(table.length * 2);
        }
        Entry<K, V> entry = table[index];
        table[index] = new Entry<>(hash, key, value, entry);
    }

    public V get(K key) {
        Entry<K, V> entry;
        if (key == null) {
            entry = table[0];

            while (entry != null) {
                if (entry.key == null) {
                    return entry.value;
                }
                entry = entry.next;
            }
        } else {
            entry = table[indexFor(key.hashCode(), table.length)];

            while (entry != null) {
                if (key.equals(entry.key)) {
                    return entry.value;
                }
                entry = entry.next;
            }
        }

        return null;
    }

    public boolean remove(K key) {
        Entry<K, V> prev = null;
        Entry<K, V> tmp;
        if (key == null) {
            tmp = table[0];

            while (tmp != null) {
                if (tmp.key == null) {
                    if (prev == null) {
                        table[0] = tmp.next;
                        return true;
                    }
                    prev.next = tmp.next;
                    return true;
                }
                prev = tmp;
                tmp = tmp.next;
            }
        } else {
            tmp = table[indexFor(key.hashCode(), table.length)];

            while (tmp != null) {
                if (key.equals(tmp.key)) {
                    if (prev == null) {
                        table[indexFor(key.hashCode(), table.length)] = tmp.next;
                        return true;
                    }
                    prev.next = tmp.next;
                    return true;
                }
                prev = tmp;
                tmp = tmp.next;
            }
        }
        return false;
    }

    private void resize(int newCapacity) {
        if (table.length == MAX_CAPACITY) {
            threshold = Integer.MAX_VALUE;
            return;
        }

        Entry[] newTable = new Entry[newCapacity];
        transfer(newTable);
        threshold = Math.round(newCapacity * loadFactor);
    }

    private void transfer(Entry<K, V>[] newTable) {
        Entry<K, V>[] oldTable = table;
        table = newTable;
        for (Entry<K, V> tmp : oldTable) {
            while (tmp != null) {
                addEntry(tmp.hash, tmp.key, tmp.value, indexFor(tmp.hash, newTable.length));
                tmp = tmp.next;
            }
        }
    }

    public int getCapacity() {
        return table.length;
    }

    public int getSize() {
        return size;
    }

    public int getThreshold() {
        return threshold;
    }


    public static void main(String[] args) {
        MyHashMap<String, Integer> map = new MyHashMap(10, 0.5f);
        map.put("a", 1);
        map.put("b", 2);
        map.put("c", 3);
        map.put("d", 4);
        map.put("e", 5);
        map.put("f", 6);

        System.out.println(map.get("a"));
        System.out.println(map.get("b"));
        System.out.println(map.get("e"));

        map.remove("e");
        map.put("a", 100);

        System.out.println(map.get("e"));
        System.out.println(map.get("a"));
    }
}