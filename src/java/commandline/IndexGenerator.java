/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commandline;


import java.io.File;
import java.io.FileNotFoundException;
import document_indexing.VectorialIndexingManager;
import server.GugleConstants;

/**
 *
 * @author mauro
 */
public class IndexGenerator {
    public final static String DOCUMENTS_DIRECTORY = "web/documents";

    public static void main(String[] args) {
        VectorialIndexingManager im = new VectorialIndexingManager(
                GugleConstants.RESOURCES_DIR);
        
        File dir = new File(DOCUMENTS_DIRECTORY);
        File[] files = dir.listFiles();
        int amount = files.length;//150;
        
        
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
