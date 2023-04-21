package encryptdecrypt;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class FileReader implements Reader{

    Path path;
    FileReader(Path path) {
        this.path = path;
    }

    @Override
    public Stream<String> read() {
        try {
            BufferedReader bufferedReader = Files.newBufferedReader(path);
            //System.out.println("LINES TO READ: ");
            return bufferedReader.lines(); //.peek(System.out::println);
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return Stream.empty();
        }
    }
}
