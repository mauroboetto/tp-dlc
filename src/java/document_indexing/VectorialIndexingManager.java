/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package document_indexing;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.SortedSet;
import java.util.Set;


/**
 *
 * @author mauro
 */
public class VectorialIndexingManager {
    private final VocabularyAndPostingManager vocabularyPostingManager;
    
    private Map<Short, SearchResult> results;
    private SortedSet<SearchResult> sortedResults;
    private Set<Short> currentWordDocuments;
    
    public VectorialIndexingManager() {
        this.vocabularyPostingManager = new VocabularyAndPostingManager();
    }
    
    public VectorialIndexingManager(String documentIndexesFilename, 
            String vocabularyFilename, String postingFilename) {
        this.vocabularyPostingManager = new VocabularyAndPostingManager(
                documentIndexesFilename, vocabularyFilename, postingFilename);
    }
    
    
    public Iterable<SearchResult> getResults(Set<String> words) {
        results = new HashMap();
        currentWordDocuments = new HashSet();
        sortedResults = new TreeSet();
        
        
        for (String word: words) {
            processWordTfs(word);
        }
        
        Iterable<SearchResult> ret = sortedResults;
        
        results = null;
        currentWordDocuments = null;
        sortedResults = null;
        return ret;
    }
    
    private void processWordTfs(String word) {
        PostingEntries posting = vocabularyPostingManager.getDocumentsPosting(word);
        if (posting == null) {
            return;
        }
        
        //int tfMax = vocabularyPostingManager.getMaxTf(word);
        int totalDocuments = vocabularyPostingManager.getDocumentCount();
        int containedIn = vocabularyPostingManager.getDocumentsContaining(word);
        double logFactor = Math.log(((double) totalDocuments) / containedIn);
        
        
        double vectorModule = 0;
        for (PostingEntries.PostingSingleEntry entry: posting) {
            short id = entry.documentId;
            int tf = entry.documentTf;
            double semiAdjustedTf = tf * logFactor;
            vectorModule += Math.pow(semiAdjustedTf, 2);
            
            if (!currentWordDocuments.contains(id)) {
                currentWordDocuments.add(id);
            }
            
            SearchResult result = results.get(id);
            if (result == null) {
                result = new SearchResult(vocabularyPostingManager.getDocumentName(id));
                results.put(id, result);
            }
            result.addParameterValues(word, tf, semiAdjustedTf);
            
        }
        
        vectorModule = Math.sqrt(vectorModule);
        Iterator<Short> it = currentWordDocuments.iterator();
        while (it.hasNext()) {
            short id = it.next();
            it.remove();
            SearchResult result = results.get(id);
            
            // update order
            sortedResults.remove(result);
            result.calcLastParameterAdjustedTf(vectorModule);
            sortedResults.add(result);
            
        }
        
    }
    
    public void parseFiles(File[] files) throws FileNotFoundException {
        vocabularyPostingManager.parseFiles(files);
    }
}
