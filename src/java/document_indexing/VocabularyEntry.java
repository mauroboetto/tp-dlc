/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package document_indexing;

import java.io.RandomAccessFile;

/**
 *
 * @author mauro
 */
class VocabularyEntry {
    private int quantity;
    private int max_tf;
    private long offset;
    private int postingBlockAmount;
    
    
    VocabularyEntry(PostingEntry posting, String postingFilename) {
        this.quantity = posting.getCount();
        this.max_tf = posting.getMaxTf();
        /*this.postingBlockAmount = ;
        
        try (RandomAccessFile raf = new RandomAccessFile()) {
            
        }*/
    }
    
}
