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
    
    public VectorialIndexingManager(String resourcesDir) {
        this.vocabularyPostingManager = new VocabularyAndPostingManager(
                resourcesDir);
    }
    
    
    public Iterable<SearchResult> getResults(Set<String> words) {
        results = new HashMap();
        currentWordDocuments = new HashSet();
        sortedResults = new TreeSet();
        
        
        for (String word: words) {
            processWordTfs(word.toLowerCase());
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
        
        int maxTf = vocabularyPostingManager.getMaxTf(word);
        int totalDocuments = vocabularyPostingManager.getDocumentCount();
        int containedIn = vocabularyPostingManager.getDocumentsContaining(word);
        double logFactor = Math.log(((double) totalDocuments) / containedIn);
        
        boolean useFakeVectorModule;
        if (useFakeVectorModule = logFactor == 0) {
            // Es mayor que cualquier otra cantidad posible, pero más chico que el total
            double fakeContainedIn = totalDocuments - 0.0001;
            logFactor = Math.log(totalDocuments / fakeContainedIn);
            
        }
        
        
        double vectorModule = 0;
        for (PostingEntries.PostingSingleEntry entry: posting) {
            short id = entry.documentId;
            int tf = entry.documentTf;
            double semiAdjustedTf = tf * logFactor;
            
            if (!useFakeVectorModule) {
                vectorModule += Math.pow(semiAdjustedTf, 2);
            }
            
            if (!currentWordDocuments.contains(id)) {
                currentWordDocuments.add(id);
            }
            
            SearchResult result = results.get(id);
            if (result == null) {
                result = new SearchResult(vocabularyPostingManager.getDocumentName(id));
                results.put(id, result);
            }
            result.addParameterValues(word, tf, semiAdjustedTf, maxTf, containedIn);
            
        }
        
        if (!useFakeVectorModule) {
            vectorModule = Math.sqrt(vectorModule);
        } else {
            vectorModule = 1;
        }
        
        Iterator<Short> it = currentWordDocuments.iterator();
        while (it.hasNext()) {
            short id = it.next();
            it.remove();
            SearchResult result = results.get(id);
            
            // actualizar el órden
            sortedResults.remove(result);
            result.calcLastParameterAdjustedTf(vectorModule);
            sortedResults.add(result);
            
        }
        
    }
    
    public void parseFiles(File[] files) throws FileNotFoundException {
        vocabularyPostingManager.parseFiles(files);
    }
    
    public String showVocabulary() {
        return vocabularyPostingManager.showVocabulary();
    }
}
