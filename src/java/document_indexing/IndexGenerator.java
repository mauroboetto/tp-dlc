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
    public final static String documentsDirectory = "web/documents";

    public static void main(String[] args) {
        VectorialIndexingManager im = new VectorialIndexingManager(
                                        "test_fns.bin", "test_voc.bin", "test_post.bin");
        
        File dir = new File(documentsDirectory);
        File[] files = dir.listFiles();
        int amount = 1; //files.length;
        
        
        try {
            if (amount != files.length) {
                File[] files2 = new File[amount];
                System.arraycopy(files, 0, files2, 0, amount);
                files = files2;
            }
            im.parseFiles(files);    
        } catch (FileNotFoundException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
        
    }
}
