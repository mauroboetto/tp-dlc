
package parsers;

import utils.TSB_OAHashtable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

/**
 *
 * @author mauro
 */
public class WordCounter {

    private Map<String, Integer> map;

    public WordCounter() {
        map = new TSB_OAHashtable<>(100001);
    }
    
    public WordCounter(Map<String, Integer> m) {
        map = m;
    }
    
    public Map<String, Integer> getMap() {
        return map;
    }

    public void loadFromFile(String filename) throws FileNotFoundException {
        try (WordReader wr = new WordReader(filename)) {
            for (String palabra : wr) {
                incrementCounter(palabra.toLowerCase());
            }
        } catch (FileNotFoundException ex) {
            throw ex;
        } catch (IOException ex) {
        }
    }

    private void incrementCounter(String word) {
        Integer v = map.get(word);
        if (v != null) {
            map.put(word, v + 1);
        } else {
            map.put(word, 1);
        }

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("WordCounter{\n");
        for (Map.Entry<String, Integer> e: map.entrySet()) {
            sb.append('\t').append(e.getKey()).append(": ").append(e.getValue()).append(",\n");
        }
        sb.append('}');
        return  sb.toString();
    }
    
    public int calcTotal(){
        int total = 0;
        for (Map.Entry<String, Integer> e: map.entrySet()) {
            total += e.getValue();
        }
        return total;
    }
    
    public void serialize(String filename) throws FileNotFoundException, IOException{
        try (FileOutputStream out = new FileOutputStream(new File(filename)))
        {
            ObjectOutputStream ofile = new ObjectOutputStream(out);
            ofile.writeObject(map);
            ofile.flush();
        }
    }
    
    public void deserialize(String filename) throws FileNotFoundException, IOException, ClassNotFoundException {
        try (
                FileInputStream in = new FileInputStream(new File(filename));
                ObjectInputStream ifile = new ObjectInputStream(in)
            ) 
        {    
            map = (Map) ifile.readObject();
        }
    }
    
}
