package encryptdecrypt;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.stream.Stream;

public class FilePrinter implements Printer{

    Path filePath;

    public FilePrinter(Path outFile) {
        this.filePath = outFile;
    }

    @Override
    public void print(String line) {
        //System.out.println("LINE TO PRINT: " + line);
        try(PrintWriter out = new PrintWriter(
                Files.newBufferedWriter(
                        filePath, Charset.forName("UTF-8"),
                        StandardOpenOption.CREATE,
                        StandardOpenOption.WRITE,
                        StandardOpenOption.TRUNCATE_EXISTING)))
        ///StandardOpenOption.CREATE))) //, StandardOpenOption.APPEND)))
        {
            out.println(line);
        } catch (IOException e) {
            System.out.println("Oops... bye!");
        }
    }

    public void print(Stream<String> line) {
        try (PrintWriter out = new PrintWriter(
                Files.newBufferedWriter(
                        filePath, Charset.forName("UTF-8"),
                        StandardOpenOption.CREATE,
                        StandardOpenOption.WRITE,
                        StandardOpenOption.APPEND))) {
            out.println(line);
        } catch (IOException e) {
            System.out.println("Oops... bye!");
        }
    }
}
