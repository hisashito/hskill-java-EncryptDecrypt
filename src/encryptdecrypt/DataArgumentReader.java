package encryptdecrypt;

import java.util.stream.Stream;

public class DataArgumentReader implements Reader{

    String str;

    DataArgumentReader(String str) {
        this.str = str;
    }

    @Override
    public Stream<String> read() {
        return Stream.of(str);
    }
}
