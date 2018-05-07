/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package document_indexing;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Map;
import utils.TSB_OAHashtable;
import parsers.WordCounter;

import java.util.logging.Logger;

/**
 *
 * @author mauro
 */
public class VectorialIndexingManager {
    private final static Logger LOGGER = Logger.getLogger(VectorialIndexingManager.class.getName());
    
    public final static String DOCUMENTS_FILENAME = "document_indexes.bin";
    public final static String VOCABULARY_FILENAME = "vocabulary.bin";
    public final static String POSTING_FILENAME = "posting.bin";
    
    private final String documentIndexesFilename;
    private final VocabularyAndPostingManager vocabularyPostingManager;
    private Map<Integer, Double> searchTotals;
    private Map<Integer, Double> wordTfs;
    private double acumTfs;
    
    public VectorialIndexingManager() {
        this(DOCUMENTS_FILENAME, VOCABULARY_FILENAME, POSTING_FILENAME);
    }
    
    public VectorialIndexingManager(String documentIndexesFilename, 
            String vocabularyFilename, String postingFilename) {
        this.vocabularyPostingManager = new VocabularyAndPostingManager(vocabularyFilename, postingFilename);
        this.documentIndexesFilename = documentIndexesFilename;
        
    }
    
    
    public Iterable<SearchResult> getResults(Iterable<String> words) {
        searchTotals = new TSB_OAHashtable();
        
        ArrayList<SearchResult> ret = new ArrayList();
        ret.add(new SearchResult("link1","name1"));
        ret.add(new SearchResult("link2","name2"));
        ret.add(new SearchResult("link3",words.toString()));
        for (String word: words) {
            processWordTfs(word);
            updateSearchTotals();
        }
        
        for (Map.Entry<Integer, Double> entry: searchTotals.entrySet()) {
            // TODO procesar entradas y crear el resultado
            //ret.add(new SearchResult("arreglame"));
        }
            
        
        
        
        wordTfs = null;
        searchTotals = null;
        return ret;
    }
    
    private void processWordTfs(String word) {
        // Matriz de [ID_Documento][TF]
        int tfsXDocument[][] = vocabularyPostingManager.getDocumentsTfs(word);
        
        int tfMax = vocabularyPostingManager.getMaxTf(word);
        int totalDocuments = vocabularyPostingManager.getDocumentCount();
        int containedIn = vocabularyPostingManager.getDocumentsContaining(word);
        
        acumTfs = 0;
        wordTfs = new TSB_OAHashtable();
        
        
        // TODO: procesa los valores de una palabra
    }
    
    private void updateSearchTotals() {
        // TODO
        // Actualiza searchTotals con los valores de la ultima palabra
    }

    public void parseFile(File file) {
        try
        {
            vocabularyPostingManager.parseFile(file);
            
        }
        catch (FileNotFoundException ex) 
        {
            System.out.println("Error");
        }
    }

    public void parseFiles(File[] files) throws FileNotFoundException {
        vocabularyPostingManager.parseFiles(files);
    }
    
}
