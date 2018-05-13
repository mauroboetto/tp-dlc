
package parsers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * @author mauro
 */
public class WordReader implements Iterable<String>, Closeable {

    private final static Pattern WORD_PATTERN = Pattern.compile("\\b\\p{L}+\\b");
    private final BufferedReader reader;
    private Matcher m;
    
    public WordReader(String filename) throws FileNotFoundException {
        reader = new BufferedReader(new FileReader(filename));
        readLine();
    }
    
    public static ArrayList<String> parseLine(String line) {
        ArrayList<String> ret = new ArrayList();
        Matcher m = WORD_PATTERN.matcher(line);
        while (m.find()) {
            ret.add(m.group());
        }
        
        return ret;
    }
    
    private boolean readLine() {
        try {
            String line = reader.readLine();
            if (line != null) {
                m = WORD_PATTERN.matcher(line);
                return true;
            }
        } catch (IOException ex) { }
        m = null;
        return false;

    }

    @Override
    public Iterator iterator() {
        return new WordIterator();
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }

    private class WordIterator implements Iterator<String> {

        @Override
        public boolean hasNext() {
            while (m != null && !m.find() && readLine()) { }
            return m != null;
        }

        @Override
        public String next() {
            return m.group();
        }

    }
}
