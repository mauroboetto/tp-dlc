/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package document_indexing;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 *
 * @author mauro
 */
class PostingEntry {
    public final static int ENTRIES_PER_BLOCK = 20;
    public final static int BLOCK_SIZE = (Integer.SIZE + Short.SIZE) * ENTRIES_PER_BLOCK;
    
    private short[] documentsIds;
    private int[] documentsTfs;
    private int count;
    
    PostingEntry() {
        this.documentsIds = new short[ENTRIES_PER_BLOCK];
        this.documentsTfs = new int[ENTRIES_PER_BLOCK];
        this.count = 0;
    }
    
    PostingEntry(short[] documentsIds, int[] documentsTfs, int count) {
        if (documentsIds.length != documentsTfs.length 
                && documentsTfs.length % ENTRIES_PER_BLOCK != 0) {
            throw new InvalidPostingEntryArraySize("Invalid array sizes");
        }
        this.documentsIds = documentsIds;
        this.documentsTfs = documentsTfs;
        this.count = count;
        sort();
    }
    
    PostingEntry(String filename, long offset, int count, int blockCount) 
            throws FileNotFoundException, IOException {
        this.count = count;
        int entriesCount = ENTRIES_PER_BLOCK * blockCount;
        this.documentsIds = new short[entriesCount];
        this.documentsTfs = new int[entriesCount];
        
        
        // Open file and seek data offset
        try(RandomAccessFile raf = new RandomAccessFile(filename, "rb")) {
            raf.seek(offset);

            // load tfs
            byte[] buffer = new byte[BLOCK_SIZE * blockCount];
            raf.read(buffer);
            int i, j;
            for (i = 0, j = 0; i < entriesCount; i++, j += 4) {
                documentsTfs[i] = (buffer[j] << 24) | (buffer[j + 1] << 16) 
                            | (buffer[j + 2] << 8) | buffer[j + 3];
            }
            for (i = 0; i < entriesCount; i++, j += 2) {
                documentsIds[i] = (short) ((buffer[j] << 8) | buffer[j + 1]);
            }
        }

    }
    
    public void update(short[] documentsIds, int[] documentsTfs, int count) {
        int arrayLength = this.documentsTfs.length;
        int neededLength = count + this.count;
        while (neededLength < arrayLength) {
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
        this.count = count;
    }
    
    
    private void sort() {
        // TODO
    }
    
    public int getCount() {
        return count;
    }
    
    public int getMaxTf() {
        return documentsTfs[0];
    }
    
    public int getPostingBlockAmount() {
        return (int) Math.ceil(((double) count) / ENTRIES_PER_BLOCK);
    }
    
    public byte[] toBytes() {
        int arraySize = (Integer.SIZE + Short.SIZE) * count;
        byte[] ret = new byte[arraySize];
        int i, j, value;
        i = 0;
        for (j = 0; i < documentsTfs.length; j++) {
            value = documentsTfs[j];
            ret[i++] = (byte) (value & 0xff);
            ret[i++] = (byte) (value & 0xff00);
            ret[i++] = (byte) (value & 0xff0000);
            ret[i++] = (byte) (value & 0xff000000);
        }
        
        for (j = 0; i < documentsIds.length; j++) {
            value = documentsIds[j];
            ret[i++] = (byte) (value & 0xff);
            ret[i++] = (byte) (value & 0xff00);
        }
        return ret;
    }
    
    public class InvalidPostingEntryArraySize extends RuntimeException {
        InvalidPostingEntryArraySize(String message) {
            super(message);
        }
    }
}
