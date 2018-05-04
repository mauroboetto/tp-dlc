/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package document_indexing;

/**
 *
 * @author mauro
 */
public class SearchResult {
    private String algo;
    
    public SearchResult(String aaa) {
        algo = aaa;
    }
    
    @Override
    public String toString() {
        return algo;
    }
}
