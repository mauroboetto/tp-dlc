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
        this.nextBlockWord = null;
    }
    
    public void setOffset(int offset) {
        this.offset = offset;
    }
    
    public void setNextBlockWord(String word) {
        this.nextBlockWord = word;
    }
    
    public String getNextBlockWord() {
        return nextBlockWord;
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
    
    public int count() {
        return quantity;
    }


    public PostingEntry getPosting(String postingFilename) {
        try {
            return new PostingEntry(postingFilename, offset, quantity, postingBlockAmount);
        } catch (IOException ex) {
            System.out.println("FIXME: " + ex.getMessage());
            return new PostingEntry();
        }
    }
    
    public PostingEntry updatePosting(String postingFilename, short[] documentsIds, int[] documentsTfs, int count) {
        PostingEntry posting = getPosting(postingFilename);
        
        posting.update(documentsIds, documentsTfs, count);
        this.quantity = posting.getCount();
        this.maxTf = posting.getMaxTf();
        
        return posting;
    }
    
}
