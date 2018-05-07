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
    private String linkFile;
    private String nameFile;
    
    public SearchResult(String link, String name) {
        linkFile = link;
        nameFile = name;
    }
    
    @Override
    public String toString() {
        return "<a href= \"" + linkFile + "\">" + nameFile + "</a>";
    }
}
