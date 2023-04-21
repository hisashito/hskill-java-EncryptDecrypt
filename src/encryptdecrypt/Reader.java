package encryptdecrypt;

import java.util.stream.Stream;

public interface Reader {
    Stream<String> read();
}
