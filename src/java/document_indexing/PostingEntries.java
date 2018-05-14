/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package document_indexing;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;

/**
 *
 * @author mauro
 */
class PostingEntries implements Iterable<PostingEntries.PostingSingleEntry> {
    private final static int INTEGER_SIZE = 4;
    private final static int SHORT_SIZE = 2;
    public final static int ENTRIES_PER_BLOCK = 60;
    public final static int BLOCK_SIZE = (INTEGER_SIZE + SHORT_SIZE) * ENTRIES_PER_BLOCK;
    
    private short[] documentsIds;
    private int[] documentsTfs;
    private int count;
    
    PostingEntries() {
        this.documentsIds = new short[ENTRIES_PER_BLOCK];
        this.documentsTfs = new int[ENTRIES_PER_BLOCK];
        this.count = 0;
    }
    
    PostingEntries(short[] documentsIds, int[] documentsTfs, int count) {
        if (documentsIds.length != documentsTfs.length 
                || documentsTfs.length % ENTRIES_PER_BLOCK != 0) {
            throw new InvalidPostingEntryArraySize("Invalid array sizes");
        }
        this.documentsIds = documentsIds;
        this.documentsTfs = documentsTfs;
        this.count = count;
        sort();
    }
    
    PostingEntries(String filename, long offset, int count, int blockCount) 
            throws FileNotFoundException, IOException {
        this.count = count;
        int entriesCount = ENTRIES_PER_BLOCK * blockCount;
        this.documentsIds = new short[entriesCount];
        this.documentsTfs = new int[entriesCount];
        
        
        // Open file and seek data offset
        try(RandomAccessFile raf = new RandomAccessFile(filename, "r")) {
            raf.seek(offset);

            // load tfs
            byte[] buffer = new byte[BLOCK_SIZE * blockCount];
            raf.read(buffer);
            
            int tfsIndex = 0;
            int idsIndex = blockCount * INTEGER_SIZE * ENTRIES_PER_BLOCK;
            for (int i = 0; i < count; i++, tfsIndex += INTEGER_SIZE, idsIndex += SHORT_SIZE) {
                documentsTfs[i] = (buffer[tfsIndex] & 0xff) 
                        | ((buffer[tfsIndex + 1]  << 8) & 0xff00)
                        | ((buffer[tfsIndex + 2] << 16) & 0xff0000) 
                        | ((buffer[tfsIndex + 3] << 24) & 0xff000000);
            
                
                documentsIds[i] = (short) ((buffer[idsIndex] & 0xff)
                        | ((buffer[idsIndex + 1] << 8) & 0xff00));
            }
        }

    }
    
    public void update(short[] documentsIds, int[] documentsTfs, int count) {
        int arrayLength = this.documentsTfs.length;
        int neededLength = count + this.count;
        while (neededLength > arrayLength) {
            // ídem: arrayLength *= 2
            arrayLength <<= 1;
        }
        if (arrayLength != this.documentsTfs.length) {
            int[] aux1 = new int[arrayLength];
            short[] aux2 = new short[arrayLength];
            
            System.arraycopy(this.documentsTfs, 0, aux1, 0, this.count);
            this.documentsTfs = aux1;
            System.arraycopy(this.documentsIds, 0, aux2, 0, this.count);
            this.documentsIds = aux2;
        }
        System.arraycopy(documentsTfs, 0, this.documentsTfs, this.count, count);
        System.arraycopy(documentsIds, 0, this.documentsIds, this.count, count);
        this.count += count;
        sort();
    }
    
    
    private void sort() {
        sort(0, count - 1);
    }
    
    private void sort(int left, int right) {
        int pivot = documentsTfs[left];
        short pivotId = documentsIds[left];
        int i = left;
        int j = right;
        int aux1;
        short aux2;
        while (i < j) {
            while (documentsTfs[i] >= pivot && i < j) {
                i++;
            }
            while (documentsTfs[j] < pivot) {
                j--;
            }
            if (i < j) {
                aux1 = documentsTfs[i];
                aux2 = documentsIds[i];
                documentsTfs[i] = documentsTfs[j];
                documentsIds[i] = documentsIds[j];
                documentsTfs[j] = aux1;
                documentsIds[j] = aux2;
            }
        }
        documentsTfs[left] = documentsTfs[j];
        documentsIds[left] = documentsIds[j];
        documentsTfs[j] = pivot;
        documentsIds[j] = pivotId;
        
        if (left < j - 1) {
            sort(left, j - 1);
        }
        if (j + 1 < right) {
            sort(j + 1, right);
        }
    }
    
    public int getCount() {
        return count;
    }
    
    public int getMaxTf() {
        return documentsTfs[0];
    }
    
    public int getNeededBlocksAmount() {
        return documentsTfs.length / ENTRIES_PER_BLOCK;
    }
    
    public byte[] toBytes() {
        return toBytes(getNeededBlocksAmount());
    }
    
    public byte[] toBytes(int blockCount) {
        byte[] ret = new byte[blockCount * BLOCK_SIZE];
        
        int tfsIndex = 0;
        int idsIndex = blockCount * INTEGER_SIZE * ENTRIES_PER_BLOCK;
        for (int i = 0; i < count; i++) {
            int value = documentsTfs[i];
            ret[tfsIndex++] = (byte) (value & 0xff);
            ret[tfsIndex++] = (byte) ((value >> 8) & 0xff);
            ret[tfsIndex++] = (byte) ((value >> 16) & 0xff);
            ret[tfsIndex++] = (byte) ((value >> 24) & 0xff);
        
            
            value = documentsIds[i];
            ret[idsIndex++] = (byte) (value & 0xff);
            ret[idsIndex++] = (byte) ((value >> 8) & 0xff);
        }
        return ret;
    }

    @Override
    public Iterator<PostingSingleEntry> iterator() {
        return new PostingEntriesIterator();
    }
    
    public class InvalidPostingEntryArraySize extends RuntimeException {
        InvalidPostingEntryArraySize(String message) {
            super(message);
        }
    }
    
    public class PostingSingleEntry {
        public short documentId;
        public int documentTf;
    }
    
    private class PostingEntriesIterator implements Iterator<PostingSingleEntry> {
        private final PostingSingleEntry entry;
        private int index;

        
        public PostingEntriesIterator() {
            index = 0;
            entry = new PostingSingleEntry();
        }
        
        @Override
        public boolean hasNext() {
            return index < count;
        }

        @Override
        public PostingSingleEntry next() {
            entry.documentId = documentsIds[index];
            entry.documentTf = documentsTfs[index];
            index++;
            return entry;
        }
    }
}
