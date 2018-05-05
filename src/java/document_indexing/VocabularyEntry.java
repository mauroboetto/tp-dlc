/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package document_indexing;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 *
 * @author mauro
 */
class VocabularyEntry {
    private int quantity;
    private int maxTf;
    private long offset;
    private int postingBlockAmount;
    private String nextBlockWord;
    
    
    VocabularyEntry(PostingEntry posting, long offset) {
        this.quantity = posting.getCount();
        this.maxTf = posting.getMaxTf();
        this.postingBlockAmount = posting.getPostingBlockAmount();
        this.offset = offset;
    }
    
    public void setOffset(int offset) {
        this.offset = offset;
    }
    
    public long getOffset() {
        return offset;
    }
    
    
    public int getMaxTf() {
        return maxTf;
    }
    
    public int getPostingBlockAmount() {
        return postingBlockAmount;
    }
    
    public void setPostingBlockAmount(int postingBlockAmount) {
        this.postingBlockAmount = postingBlockAmount;
    }
    
    public String getNextBlockWord() {
        return nextBlockWord;
    }
    
    public int count() {
        return quantity;
    }


    public PostingEntry updatePosting(String postingFilename, short[] documentsIds, int[] documentsTfs, int count) {
        PostingEntry posting;
        // TODO load posting
        posting = new PostingEntry();
        
        posting.update(documentsIds, documentsTfs, count);
        this.quantity = posting.getCount();
        this.maxTf = posting.getMaxTf();
        
        return posting;
    }
    
}
