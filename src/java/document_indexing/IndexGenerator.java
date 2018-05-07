/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package document_indexing;


import java.io.File;
import java.io.FileNotFoundException;

/**
 *
 * @author mauro
 */
public class IndexGenerator {
    public final static String documentsDirectory = "archivos/";
    
    
    public static void main(String[] args) throws FileNotFoundException {
        VectorialIndexingManager im = new VectorialIndexingManager("test_indexes.bin","test_voc.bin", "test_post.bin");
        
        File dir = new File(documentsDirectory);
        File[] files = dir.listFiles();
        
        im.parseFiles(files);
        
        
        
    }
}
