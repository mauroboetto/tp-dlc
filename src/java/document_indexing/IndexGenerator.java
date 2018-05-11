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
    public final static String documentsDirectory = "web/test-documents";
    private final static String[] testFilenames = { 
        "web/test_fns.bin", "web/test_voc.bin", "web/test_post.bin" };

    public static void main(String[] args) {
        
        for (String fn: testFilenames) {
            File f = new File(fn);
            if (f.exists()) {
                f.delete();
            }
        }
        
        VectorialIndexingManager im = new VectorialIndexingManager(
                testFilenames[0], testFilenames[1], testFilenames[2]);
        
        File dir = new File(documentsDirectory);
        File[] files = dir.listFiles();
        int amount = files.length;
        
        
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
