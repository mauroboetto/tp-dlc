/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package document_indexing;

import utils.ArrayUtils;
import utils.ArrayCastException;

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
        this.documentsIds = documentsIds;
        this.documentsTfs = documentsTfs;
        this.count = count;
        sort();
    }
    
    PostingEntry(String filename, long offset, int count, int blockCount) 
            throws FileNotFoundException, IOException, ArrayCastException {
        this.count = count;
        this.documentsIds = new short[ENTRIES_PER_BLOCK];
        this.documentsTfs = new int[ENTRIES_PER_BLOCK];
        
        // Open file and seek data offset
        try(RandomAccessFile raf = new RandomAccessFile(filename, "rb")) {
            raf.seek(offset);

            // load tfs
            byte[] buffer = new byte[ENTRIES_PER_BLOCK * Integer.SIZE];
            raf.read(buffer);
            ArrayUtils.byteArrayToIntArray(this.documentsTfs, buffer);

            // load document ids
            buffer = new byte[ENTRIES_PER_BLOCK * Short.SIZE];
            raf.read(buffer);
            ArrayUtils.byteArrayToShortArray(this.documentsIds, buffer);
        }

    }
    
    
    private void sort() {
        
    }
    
    public int getCount() {
        return count;
    }
    
    public int getMaxTf() {
        return documentsTfs[0];
    }
    
    public int getPostingBlockAmount() {
        return 0; // FIXME
    }
}
