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
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import parsers.WordCounter;
import utils.TSB_OAHashtable;

/**
 *
 * @author mauro
 */
public class VocabularyAndPostingManager {
    private final static Logger LOGGER = Logger.getLogger(VectorialIndexingManager.class.getName());
    
    public final static String DOCUMENTS_FILENAME = "documents_filenames.bin";
    public final static String VOCABULARY_FILENAME = "vocabulary.bin";
    public final static String POSTING_FILENAME = "posting.bin";
    
    private final String documentIndexesFilename;
    private final String vocabularyFilename;
    private final String postingFilename;
    
    private Map<String, VocabularyEntry> vocabulary;
    
    private List<String> filenames;
    
    public VocabularyAndPostingManager() {
        this(DOCUMENTS_FILENAME, VOCABULARY_FILENAME, POSTING_FILENAME);
    }
    
    public VocabularyAndPostingManager(String documentIndexesFilename, 
            String vocabularyFilename, String postingFilename) {
        this.vocabularyFilename = vocabularyFilename;
        this.postingFilename = postingFilename;
        this.documentIndexesFilename = documentIndexesFilename;
        
        this.vocabulary = loadVocabulary();
        this.filenames = loadFilenames(); 
    }
    
    private Map<String, VocabularyEntry> loadVocabulary() {
        // FIXME: chequear si el archivo existe
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
    
    private List<String> loadFilenames() {
        return new ArrayList(); // FIXME
    }
    
    private void saveVocabulary() {
        // TODO
    }
    
    private void saveFilenames() {
        // TODO
    }
    
    public void parseFiles(String[] filenames) throws FileNotFoundException {
        final int PROCESS_AMOUNT = 20;
        int i, count;
        
        Map<String, Integer>[] maps = new Map[PROCESS_AMOUNT];
        WordCounter wc;
        i = 0;
        
        while (i < filenames.length) {
            int initial_index = this.filenames.size();
            for (count = 0; count < PROCESS_AMOUNT && i < filenames.length; count++, i++) {
                LOGGER.log(Level.INFO, "Parsing {0}", filenames[i]);
                wc = new WordCounter();
                wc.loadFromFile(filenames[i]);
                maps[count] = wc.getMap();
                this.filenames.add(filenames[i]);
            }
            processMaps(maps, count, initial_index);
        }
        saveVocabulary();
        saveFilenames();
    }
    
    /**
     * 
     * @param documentsIds: ID's de los documentos a procesar
     * @param maps: maps con cantidad de palabras, para cada documento
     * @param count: cantidad a procesar 
     */
    private void processMaps(Map<String, Integer>[] maps, 
            int count, int initial_index) {
        short[] documents = new short[count];
        int[] tfs = new int[count];
        for (int i = 0; i < count; i++) {
            Iterator<Map.Entry<String, Integer>> keySetIterator = maps[i].entrySet().iterator();
            Map.Entry<String, Integer> entry;
            String word;
            while (keySetIterator.hasNext()) {
                entry = keySetIterator.next();
                word = entry.getKey();
                tfs[0] = entry.getValue();
                documents[0] = (short) (initial_index + i); 
                keySetIterator.remove();
                
                int k = 1;
                for (int j = i + 1; j < count; j++) {
                    Integer tf = maps[j].remove(word);
                    if (tf != null) {
                        documents[k] = (short) (initial_index + i);
                        tfs[k] = tf;
                        k += 1;
                    }
                }
            
                VocabularyEntry vocabularyEntry = vocabulary.get(word);
                if (vocabularyEntry != null) {
                    PostingEntry posting = vocabularyEntry.updatePosting(postingFilename, documents, tfs, k);
                    if (vocabularyEntry.getPostingBlockAmount() != posting.getPostingBlockAmount()) {
                        vocabularyEntry.setPostingBlockAmount(rearrangePostingBlocks(posting, 
                                vocabularyEntry.getNextBlockWord()));
                    }
                    savePosting(posting, vocabularyEntry.getOffset());
                } else {
                    PostingEntry posting = new PostingEntry(documents, tfs, k);
                    long offset = savePosting(posting, -1);
                    vocabulary.put(word, new VocabularyEntry(posting, offset));
                }
                
            }
        }
    }
    
    /**
     * Expande los bloques correspondientes a una palabra
     * 
     * @param posting: El objeto posting que necesita una expansión de bloques
     * @param nextBlockWord: La palabra a la que le corresponde el bloque consiguiente
     * @return: La nueva cantidad de bloques
     */
    private int rearrangePostingBlocks(PostingEntry posting, String nextBlockWord) {
        return 1; // FIXME
    }
    
    /**
     * 
     * @param posting: Objeto a guardar
     * @param offset: Offset donde guardar. Si es menor a 0, guardar al final
     * @return offset Donde se guardó
     */
    private long savePosting(PostingEntry posting, long offset) {
        // TODO
        /*try (RandomAccessFile raf = new RandomAccessFile(postingFilename, "rw")) {
            
        } catch (IOException ex) {
            
        }*/
        return offset;
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
    
    public String getDocumentName(int index) {
        return filenames.get(index);
    }
}
