
package utils;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author mauro
 */
public class TSB_OAHashtable<K, V> implements Map<K, V>, Cloneable, Serializable {

    private static final int MAX_SIZE = Integer.MAX_VALUE;
    private static final int DEFAULT_SIZE = 101;
    private static final float DEFAULT_FACTOR = 0.5f;

    private Entry<K, V>[] entries;
    private int count;
    private int initialCapacity;
    private float loadFactor;

    private transient int modCount;
    
    private transient Set<K> keySet = null;
    private transient Set<Map.Entry<K,V>> entrySet = null;
    private transient Collection<V> values = null;

    /* 
    Constructores:
     */
    public TSB_OAHashtable() {
        this(DEFAULT_SIZE, DEFAULT_FACTOR);
    }

    public TSB_OAHashtable(int initialCapacity) {
        this(initialCapacity, DEFAULT_FACTOR);
    }

    public TSB_OAHashtable(int initialCapacity, float loadFactor) {
        if (initialCapacity <= 0) {
            initialCapacity = DEFAULT_SIZE;
        }
        if (loadFactor <= 0 || loadFactor > 1) {
            loadFactor = DEFAULT_FACTOR;
        }
        this.entries = new Entry[initialCapacity];
        this.count = 0;
        this.initialCapacity = initialCapacity;
        this.loadFactor = loadFactor;

        this.modCount = 0;
    }

    public TSB_OAHashtable(Map<? extends K, ? extends V> t) {
        this();
        this.putAll(t);
    }

    @Override
    public int size() {
        return count;
    }

    @Override
    public boolean isEmpty() {
        return count == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return get((K) key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        return contains(value);
    }

    @Override
    public V get(Object key) {
        if (key == null) {
            throw new NullPointerException("get(): parámetro null");
        }
        int index = getIndex((K) key);
        return (index != -1)? entries[index].getValue(): null;
    }

    @Override
    public V put(K key, V value) {
        return __put(key, value, entries);
    }

    @Override
    public V remove(Object key) {
        if (key == null) {
            throw new NullPointerException("remove(): parámetro null");
        }
        int index = getIndex((K) key);
        if (index == -1) {
            return null;
        }
        return removeByIndex(index);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        for(Map.Entry<? extends K, ? extends V> e : map.entrySet())
        {
            put(e.getKey(), e.getValue());
        }
    }

    @Override
    public void clear() {
        entries = new Entry[initialCapacity];
        count = 0;
        modCount++;
    }

    @Override
    public Set<K> keySet() {
        if(keySet == null) 
        { 
            // keySet = Collections.synchronizedSet(new KeySet()); 
            keySet = new KeySet();
        }
        return keySet; 
    }

    @Override
    public Collection<V> values() {
        if(values == null)
        {
            // values = Collections.synchronizedCollection(new ValueCollection());
            values = new ValueCollection();
        }
        return values; 
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        if(entrySet == null) 
        { 
            // entrySet = Collections.synchronizedSet(new EntrySet()); 
            entrySet = new EntrySet();
        }
        return entrySet;
    }
    
    /*
    Otras redefiniciones:
    */
    @Override
    protected Object clone() throws CloneNotSupportedException 
    {
        TSB_OAHashtable<K, V> t = (TSB_OAHashtable<K, V>)super.clone();
        t.entries = new Entry[entries.length];
        for (Map.Entry<K, V> e: this.entrySet()) 
        {
            t.put(e.getKey(), e.getValue());
        }
        t.keySet = null;
        t.entrySet = null;
        t.values = null;
        t.modCount = 0;
        return t;
    }

   
    @Override
    public boolean equals(Object obj) 
    {
        if(!(obj instanceof Map)) { return false; }
        
        Map<K, V> t = (Map<K, V>) obj;
        if(t.size() != this.size()) { return false; }

        try 
        {
            Iterator<Map.Entry<K,V>> i = this.entrySet().iterator();
            while(i.hasNext()) 
            {
                Map.Entry<K, V> e = i.next();
                K key = e.getKey();
                V value = e.getValue();
                if(t.get(key) == null) { return false; }
                else 
                {
                    if(!value.equals(t.get(key))) { return false; }
                }
            }
        } 
        
        catch (ClassCastException | NullPointerException e) 
        {
            return false;
        }

        return true;    
    }

    
    @Override
    public int hashCode() 
    {
        if(this.isEmpty()) {return 0;}
        
        int hc = 0;
        for(Map.Entry<K, V> e: this.entrySet())
        {
            hc += e.hashCode();
        }
        
        return hc;
    }
    
    
    @Override
    public String toString() 
    {
        StringBuilder cad = new StringBuilder("Hashtable {");
        for(Map.Entry<K, V> e: this.entrySet())
        {
            cad.append("\t\n").append(e.toString()).append(',');  
        }
        cad.append("\n}");
        return cad.toString();
    }
    
    
    /* 
    Métodos propios
    */
    public boolean contains(Object value) {
        for (int i = 0; i < entries.length; i++) {
            if (entries[i] != null && entries[i].getValue().equals(value)) {
                return true;
            }
        }
        return false;
    }
    protected void rehash() {
        long auxNewSize = 2 * this.entries.length + 1;
        int newSize = auxNewSize > MAX_SIZE? MAX_SIZE: (int) auxNewSize;
        Entry<K, V>[] temp = new Entry[newSize]; 
        for (Map.Entry<K, V> e: this.entrySet()) {
            __put(e.getKey(), e.getValue(), temp);
        }
        this.entries = temp;
        modCount++;
    }
    
    protected boolean hasToRehash() {
        return (count / (float) entries.length) > loadFactor;
    }
    
    protected boolean removeEntry(Map.Entry<K, V> e) {
        int index = getIndex(e.getKey());
        if (index != -1  && e.equals(entries[index])) {
            removeByIndex(index);
            return true;
        }
        return false;
    }
    
    protected boolean containsEntry(Map.Entry<K, V> e) {
        int index = getIndex(e.getKey());
        if (index != -1  && e.equals(entries[index])) {
            return true;
        }
        return false;
    }

    /* 
    Métodos privados:
     */
    private int __hash(K key, int size) {
        int hashedKey = key.hashCode();
        if (hashedKey < 0) {
            hashedKey *= -1;
        }
        return hashedKey % size;
    }

    private int getIndexToAlloc(K key, Entry<K, V>[] entries) {
        int baseIndex = __hash(key, entries.length);

        int loopStart = baseIndex;
        int loopLimit = entries.length;
        for (int i = 0; i < 2; i++) {
            for (int j = loopStart; j < loopLimit; j++) {
                if (entries[j] == null || entries[j].isGrave() || key.equals(entries[j].getKey())) {
                    return j;
                }
            }
            loopStart = 0;
            loopLimit = baseIndex;
        }

        return -1;
    }

    private int getIndex(K key) {
        int index = getIndexToAlloc(key, entries);
        if (index != -1 && entries[index] == null) {
            index = -1;
        }
        return index;
    }

    private V __put(K key, V value, Entry<K, V>[] entries) {
        if (key == null || value == null) {
            throw new NullPointerException("put(): parámetro null");
        }
        if (entries == this.entries && hasToRehash()) {
            rehash();
        }
        
        int index = getIndexToAlloc(key, entries);
        V old = null;
        if (entries[index] != null && !entries[index].isGrave()) {
            old = entries[index].getValue();
        }
        
        entries[index] = new Entry<>(key, value);
        
        if (entries == this.entries && old == null) {
            modCount++;
            count++;
        }
        
        return old;
    }
    
    private V removeByIndex(int index) {
        V old = entries[index].getValue();
        entries[index].kill();
        modCount++;
        count--;
        return old;
    }

    private class Entry<K, V> implements Map.Entry<K, V>, Serializable {

        private K key;
        private V value;
        private boolean grave;

        Entry(K key, V value) {
            if (key == null || value == null) {
                throw new IllegalArgumentException("Entry(): parámetro null...");
            }
            this.key = key;
            this.value = value;
            this.grave = false;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            if (value == null) {
                throw new NullPointerException("setValue(): parámetro null");
            }
            V old = this.value; 
            this.value = value;
            return old;
        }
        
        @Override
        public int hashCode() 
        {
            int hash = 7;
            hash = 61 * hash + Objects.hashCode(this.key);
            hash = 61 * hash + Objects.hashCode(this.value);            
            return hash;
        }

        @Override
        public boolean equals(Object obj) 
        {
            if (this == obj ) { 
                return true; 
            }
            if (obj == null || this.getClass() != obj.getClass()) { 
                return false; 
            }
            
            final Entry other = (Entry) obj;          
            return Objects.equals(this.key, other.key) && Objects.equals(this.value, other.value);
        }       
        
        @Override
        public String toString()
        {
            return "(" + key.toString() + ", " + value.toString() + ")";
        }

        /*
        Metodos propios:
         */
        public boolean isGrave() {
            return grave;
        }
        
        public void kill() {
            grave = true;
        }
    }
        
    private class EntrySet extends AbstractSet<Map.Entry<K, V>> {

        @Override
        public EntrySetIterator iterator() {
            return new EntrySetIterator();
        }

        @Override
        public boolean contains(Object o) {
            if(o == null) { 
                return false; 
            } 
            if(!(o instanceof Entry)) { 
                return false; 
            }

            return TSB_OAHashtable.this.containsEntry((Entry) o);
        }


        @Override
        public boolean remove(Object o) {
            if(o == null) { 
                throw new NullPointerException("remove(): parámetro null");
            }
            if(!(o instanceof Entry)) { 
                return false; 
            }
            return TSB_OAHashtable.this.removeEntry((Entry) o);
        }

        @Override
        public int size() {
            return TSB_OAHashtable.this.size();
        }

        @Override
        public void clear() {
            TSB_OAHashtable.this.clear();
        }

        private class EntrySetIterator implements Iterator<Map.Entry<K, V>> {
            private int expectedModCount;
            private int index;
            private int seenEntries;
            private boolean nextOk;
            
            public EntrySetIterator() {
                expectedModCount = TSB_OAHashtable.this.modCount;
                seenEntries = 0;
                index = -1;
                nextOk = false;
            }
            

            @Override
            public boolean hasNext() {
                return TSB_OAHashtable.this.size() != seenEntries;
            }

            @Override
            public Map.Entry<K, V> next() {
                if (expectedModCount != TSB_OAHashtable.this.modCount) {
                    throw new ConcurrentModificationException("next(): modificación inesperada de la tabla.");
                }
                if (!hasNext()) {
                    throw new NoSuchElementException("next(): no existe el elemento pedido...");
                }
                for (int i = index + 1; i < TSB_OAHashtable.this.entries.length; i++) {
                    if (TSB_OAHashtable.this.entries[i] != null && !TSB_OAHashtable.this.entries[i].isGrave()) {
                        nextOk = true;
                        seenEntries++;
                        index = i;
                        return TSB_OAHashtable.this.entries[i];
                    }
                }
                System.out.println(seenEntries + " / " + TSB_OAHashtable.this.size());
                throw new NoSuchElementException("next(): no existe el elemento pedido...");
            }

            @Override
            public void remove() {
                if(!nextOk) { 
                    throw new IllegalStateException("remove(): debe invocar a next() antes de remove()..."); 
                }
                TSB_OAHashtable.this.removeByIndex(index);
                nextOk = false;
                expectedModCount++;
                seenEntries--;
            }
        }

    }
    
    private class KeySet extends AbstractSet<K> {

        @Override
        public KeySetIterator iterator() {
            return new KeySetIterator();
        }

        @Override
        public boolean contains(Object o) {
            return TSB_OAHashtable.this.containsKey(o);
        }


        @Override
        public boolean remove(Object o) {
            return TSB_OAHashtable.this.remove(o) != null;
        }

        @Override
        public int size() {
            return TSB_OAHashtable.this.size();
        }

        @Override
        public void clear() {
            TSB_OAHashtable.this.clear();
        }

        private class KeySetIterator implements Iterator<K> {
            private final Iterator<Map.Entry<K,V>> entryIterator;
            
            public KeySetIterator() {
                entryIterator = TSB_OAHashtable.this.entrySet().iterator();
            }
            

            @Override
            public boolean hasNext() {
                return entryIterator.hasNext();
            }

            @Override
            public K next() {
                return entryIterator.next().getKey();
            }

            @Override
            public void remove() {
                entryIterator.remove();
            }
        }

    }
    
    private class ValueCollection extends AbstractCollection<V> {
        @Override
        public Iterator<V> iterator() {
            return new ValueCollectionIterator();
        }
        
        @Override
        public int size() {
            return TSB_OAHashtable.this.size();
        }
        
        @Override
        public boolean contains(Object o) {
            return TSB_OAHashtable.this.containsValue(o);
        }
        
        @Override
        public void clear() {
            TSB_OAHashtable.this.clear();
        }
        
        
        private class ValueCollectionIterator implements Iterator<V>{
            private final Iterator<Map.Entry<K,V>> entryIterator;
            
            public ValueCollectionIterator() {
                entryIterator = TSB_OAHashtable.this.entrySet().iterator();
            }
            

            @Override
            public boolean hasNext() {
                return entryIterator.hasNext();
            }

            @Override
            public V next() {
                return entryIterator.next().getValue();
            }

            @Override
            public void remove() {
                entryIterator.remove();
            }
        }
    }
}
