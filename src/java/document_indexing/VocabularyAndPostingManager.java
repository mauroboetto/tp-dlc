/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package document_indexing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;
import parsers.WordCounter;
import utils.TSB_OAHashtable;

/**
 *
 * @author mauro
 */
public class VocabularyAndPostingManager {
    
    private final String vocabularyFilename;
    private final String postingFilename;
    private Map<String, VocabularyEntry> vocabulary;
    
    VocabularyAndPostingManager(String vocabularyFilename, String postingFilename) {
        this.vocabularyFilename = vocabularyFilename;
        this.postingFilename = postingFilename;
        
        this.vocabulary = loadVocabulary();
    }
    
    private Map<String, VocabularyEntry> loadVocabulary() {
        TSB_OAHashtable<String, VocabularyEntry> ret;
        try (
                FileInputStream in = new FileInputStream(new File(vocabularyFilename));
                ObjectInputStream ifile = new ObjectInputStream(in);
            )
        {           
            ret = (TSB_OAHashtable) ifile.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            ret = new TSB_OAHashtable();
        }
        return ret;
    }
    
    private void saveVocabulary() {
        
    }
    
    public void parseFiles(String[] filenames) throws FileNotFoundException {
        final int PROCESS_AMOUNT = 10;
        int i, count;
        
        Map<String, Integer>[] maps = new Map[PROCESS_AMOUNT];
        short[] documentsIds = new short[PROCESS_AMOUNT];
        WordCounter wc;
        i = 0;
        while (i < filenames.length) {
            for (count = 0; count < PROCESS_AMOUNT && i < filenames.length; count++, i++) {
                wc = new WordCounter();
                wc.loadFromFile(filenames[i]);
                maps[count] = wc.getMap();
            }
            processMaps(documentsIds, maps, count);
        }
    }
    
    private void processMaps(short[] documentsIds, Map<String, Integer>[] maps, int count) {
        short[] documents = new short[count];
        int[] tfs = new int[count];
        for (int i = 0; i < count; i++) {
            for (String word: maps[i].keySet()) {
                documents[0] = documentsIds[i];
                tfs[0] = maps[i].remove(word);
                
                int k = 1;
                for (int j = i + 1; j < count; j++) {
                    Integer tf = maps[i].remove(word);
                    if (tf != null) {
                        documents[k] = documentsIds[j];
                        tfs[k] = tf;
                        k += 1;
                    }
                }
            }
            
        }
        
        
    }
    
    private void updatePosting(String word, short document, int tf) {
        
    }
    
    public void parseFile(String filename) throws FileNotFoundException {
        WordCounter wc = new WordCounter();
        wc.loadFromFile(filename);
        
        //return (TSB_OAHashtable<String, Integer>) wc.getMap();
    }
    
    public int[][] getDocumentsTfs(String word) {
        
        // FIXME
        return new int[10][2];
    }
    
    public int getMaxTf(String word) {
        VocabularyEntry entry = vocabulary.get(word);
        if (entry == null) {
            return 0;
        }
        return entry.getMaxTf();
    }
    
    public int getDocumentCount() {
        return vocabulary.size();
    }
    
    public int getDocumentsContaining(String word) {
        VocabularyEntry entry = vocabulary.get(word);
        if (entry == null) {
            return 0;
        }
        return entry.count();
    }
}
