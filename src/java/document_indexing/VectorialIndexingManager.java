/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package document_indexing;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Map;
import utils.TSB_OAHashtable;


/**
 *
 * @author mauro
 */
public class VectorialIndexingManager {
    private final VocabularyAndPostingManager vocabularyPostingManager;
    private Map<Integer, Double> searchTotals;
    private Map<Integer, Double> wordTfs;
    private double acumTfs;
    
    public VectorialIndexingManager() {
        this.vocabularyPostingManager = new VocabularyAndPostingManager();
    }
    
    public VectorialIndexingManager(String documentIndexesFilename, 
            String vocabularyFilename, String postingFilename) {
        this.vocabularyPostingManager = new VocabularyAndPostingManager(
                documentIndexesFilename, vocabularyFilename, postingFilename);
    }
    
    
    public Iterable<SearchResult> getResults(Iterable<String> words) {
        searchTotals = new TSB_OAHashtable();
        
        ArrayList<SearchResult> ret = new ArrayList();
        ret.add(new SearchResult("Palabras"));
        for (String word: words) {
            processWordTfs(word);
            updateSearchTotals();
        }
        
        for (Map.Entry<Integer, Double> entry: searchTotals.entrySet()) {
            // TODO procesar entradas y crear el resultado
            ret.add(new SearchResult("arreglame"));
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
    
    
    public void parseFiles(String[] filenames) throws FileNotFoundException {
        vocabularyPostingManager.parseFiles(filenames);
    }
}
