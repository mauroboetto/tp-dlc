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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import parsers.WordCounter;


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
    private String lastPostingWord;
    
    public VocabularyAndPostingManager() {
        this(DOCUMENTS_FILENAME, VOCABULARY_FILENAME, POSTING_FILENAME);
    }
    
    public VocabularyAndPostingManager(String documentIndexesFilename, 
            String vocabularyFilename, String postingFilename) {
        this.vocabularyFilename = vocabularyFilename;
        this.postingFilename = postingFilename;
        this.documentIndexesFilename = documentIndexesFilename;
        
        loadVocabulary();
        loadFilenames();
    }
    
    private void loadVocabulary() {
        File f = new File(vocabularyFilename);
        if (f.exists() && ! f.isDirectory()) {
            try (
                    FileInputStream in = new FileInputStream(f);
                    ObjectInputStream ifile = new ObjectInputStream(in);
                )
            {           
                vocabulary = (Map) ifile.readObject();
                lastPostingWord = (String) ifile.readObject();
                return;
            } catch (IOException | ClassNotFoundException ex) {
                LOGGER.log(Level.SEVERE, "Vocabulary file, invalid format");

            }    
        }
        
        vocabulary = new HashMap();
        lastPostingWord = null;
    }
    
    private void loadFilenames() {
        File f = new File(documentIndexesFilename);
        if (f.exists() && ! f.isDirectory()) {
            try (
                    FileInputStream in = new FileInputStream(f);
                    ObjectInputStream ifile = new ObjectInputStream(in);
                )
            {           
                filenames = (List) ifile.readObject();
                return;
            } catch (IOException | ClassNotFoundException ex) {
                LOGGER.log(Level.SEVERE, "Filenames file, invalid format");
            }    
        }
        
        filenames = new ArrayList();
    }
    
    private void saveVocabulary() {
        // TODO
    }
    
    private void saveFilenames() {
        // TODO
    }
    
    public void parseFiles(File[] files) throws FileNotFoundException {
        final int PROCESS_AMOUNT = 20;
        int i, count;
        
        Map<String, Integer>[] maps = new Map[PROCESS_AMOUNT];
        WordCounter wc;
        i = 0;

        while (i < files.length) {
            int initial_index = this.filenames.size();
            for (count = 0; count < PROCESS_AMOUNT && i < files.length; count++, i++) {
                String fn = files[i].getName();
                LOGGER.log(Level.INFO, "Parsing {0}", fn);
                wc = new WordCounter();
                wc.loadFromFile(files[i].getAbsolutePath());
                maps[count] = wc.getMap();
                this.filenames.add(fn);
            }
            processMaps(maps, count, initial_index);
        }
        saveVocabulary();
        saveFilenames();
    }
    
    /**
     * 
     * @param documentsIds: ID's de los documentos a procesar
     * @param count: cantidad a procesar 
     * @param initialIndex: primer índice de documentos sin usar
     */
    private void processMaps(Map<String, Integer>[] maps, 
            int count, int initialIndex) {
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
                documents[0] = (short) (initialIndex + i); 
                keySetIterator.remove();
                
                int k = 1;
                for (int j = i + 1; j < count; j++) {
                    Integer tf = maps[j].remove(word);
                    if (tf != null) {
                        documents[k] = (short) (initialIndex + i);
                        tfs[k] = tf;
                        k += 1;
                    }
                }
            
                VocabularyEntry vocabularyEntry = vocabulary.get(word);
                if (vocabularyEntry != null) {
                    PostingEntry posting = vocabularyEntry.updatePosting(postingFilename, documents, tfs, k);
                    rearrangePostingBlocks(vocabularyEntry, posting);
                } else {
                    PostingEntry posting = new PostingEntry(documents, tfs, k);
                    long offset = savePosting(posting, -1);
                    if (lastPostingWord != null) {
                        vocabulary.get(lastPostingWord).setNextBlockWord(word);
                    }
                    vocabulary.put(word, new VocabularyEntry(posting, offset));
                    lastPostingWord = word;
                }
            }
        }
    }
    
    /**
     * Expande los bloques correspondientes a una palabra si es necesario y guarda los cambios
     * 
     * @param vocabularyEntry: entrada del vocabulario a expandir
     * @param posting
     */
    private void rearrangePostingBlocks(VocabularyEntry vocabularyEntry, PostingEntry posting) {
        String nextBlockWord = vocabularyEntry.getNextBlockWord();
        int neededBlocks = posting.getPostingBlockAmount();
        int availableBlocks = vocabularyEntry.getPostingBlockAmount();
        do {
            VocabularyEntry nextBlockEntry = vocabulary.get(nextBlockWord);
            String followingBlockWord = nextBlockEntry.getNextBlockWord();
            nextBlockEntry.setNextBlockWord(null);
            vocabularyEntry.setNextBlockWord(followingBlockWord);
            savePosting(nextBlockEntry.getPosting(postingFilename), -1);
            
        } while (neededBlocks > availableBlocks);
        
        vocabularyEntry.setPostingBlockAmount(availableBlocks);
        savePosting(posting, vocabularyEntry.getOffset());
    }
    
    /**
     * 
     * @param posting: Objeto a guardar
     * @param offset: Offset donde guardar. Si es menor a 0, guardar al final
     * @return offset donde se guardó
     */
    private long savePosting(PostingEntry posting, long offset) {
        try (RandomAccessFile raf = new RandomAccessFile(postingFilename, "rw")) {
            if (offset < 0) {
                offset = raf.length();
            }
            raf.seek(offset);
            raf.write(posting.toBytes());
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "IOException: {0}", ex.getMessage());
        }
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
