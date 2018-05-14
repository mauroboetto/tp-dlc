/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package document_indexing;

import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

/**
 *
 * @author mauro
 */
public class SearchResult implements Comparable {
    private final String filename;
    private final List<ParameterValues> parameters;
    private double relevance;
    
    public SearchResult(String filename) {
        this.filename = filename;
        this.parameters = new ArrayList();
        this.relevance = 0;
    }
    
    public String getFilename() {
        return filename;
    }
    
    public String getInfo() {
        StringBuilder sb = new StringBuilder();
        for (ParameterValues par: parameters) {
            sb.append(par.word);
            sb.append(": ");
            sb.append("tf = ");
            sb.append(par.tf);
            sb.append(",  tf ajustada = ");
            sb.append(par.adjustedTf);
            sb.append(",  tf máxima = ");
            sb.append(par.maxTf);
            sb.append(",  contenida en = ");
            sb.append(par.containedIn);
            sb.append('\n');
        }
        sb.append("Relevancia del documento: ");
        sb.append(relevance);
        sb.append('\n');
        return sb.toString();
    }
    
    public void addParameterValues(String word, int tf, double semiAdjustedTf, int maxTf, int containedIn) {
        parameters.add(new ParameterValues(word, tf, semiAdjustedTf, maxTf, containedIn));
    }
    
    public void calcLastParameterAdjustedTf(double vectorModule) {
        ParameterValues par = parameters.get(parameters.size() - 1);
        par.adjustedTf = par.semiAdjustedTf / vectorModule;
        relevance += par.adjustedTf;
    }
    

    @Override
    public int compareTo(Object t) {
        SearchResult other = (SearchResult) t;
        if (other.equals(this)) {
            return 0;
        }
        if (this.relevance == other.relevance) {
            return this.filename.compareTo(other.filename);
        }
        return (this.relevance - other.relevance) > 0 ? -1: 1;
    }
    
    @Override
    public boolean equals(Object t) {
        if (t == null || t.getClass() != this.getClass()) {
            return false;
        }
        SearchResult other = (SearchResult) t;
        return other.filename.equals(this.filename);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + Objects.hashCode(this.filename);
        return hash;
    }
    
    private class ParameterValues {
        private final String word;
        private final int tf;
        private final double semiAdjustedTf;
        private final int maxTf;
        private final int containedIn;
        private double adjustedTf;
        
        ParameterValues(String word, int tf, double semiAdjustedTf, int maxTf, int containedIn) {
            this.word = word;
            this.tf = tf;
            this.maxTf = maxTf;
            this.containedIn = containedIn;
            this.semiAdjustedTf = semiAdjustedTf;
        }
    }
}
