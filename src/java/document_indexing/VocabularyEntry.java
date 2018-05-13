/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package document_indexing;

import java.io.IOException;
import java.io.Serializable;

/**
 *
 * @author mauro
 */
class VocabularyEntry implements Serializable {
    private int quantity;
    private int maxTf;
    private long offset;
    private int postingBlockAmount;
    private String nextBlockWord;
    
    
    VocabularyEntry(PostingEntries posting, long offset) {
        this.quantity = posting.getCount();
        this.maxTf = posting.getMaxTf();
        this.postingBlockAmount = posting.getNeededBlocksAmount();
        this.offset = offset;
        this.nextBlockWord = null;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("\nquantity: ");
        sb.append(quantity);
        sb.append("\nmaxTf: ");
        sb.append(maxTf);
        sb.append("\noffset: ");
        sb.append(offset);
        sb.append("\nblock amount: ");
        sb.append(postingBlockAmount);
        sb.append("\nnext bloc word: ");
        sb.append(nextBlockWord);
        sb.append('\n');
        
        
        return sb.toString();
    }
    
    public void setOffset(long offset) {
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


    public PostingEntries getPosting(String postingFilename) {
        try {
            return new PostingEntries(postingFilename, offset, quantity, postingBlockAmount);
        } catch (IOException ex) {
            return new PostingEntries();
        }
    }
    
    public PostingEntries updatePosting(String postingFilename, short[] documentsIds, int[] documentsTfs, int count) {
        PostingEntries posting = getPosting(postingFilename);
        
        posting.update(documentsIds, documentsTfs, count);
        this.quantity = posting.getCount();
        this.maxTf = posting.getMaxTf();
        
        return posting;
    }
    
}
