package encryptdecrypt;

import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    static String[] parametersFormat = {"enc|dec", "\\d+", "\\p{Print}*", "shift|unicode"};
    static String mode = "enc";
    static int key = 0;
    static String data = "";
    static boolean isData = false;
    static Path inPath = null;
    static Path outPath = null;
    static EncDecAlgorithmType alg = EncDecAlgorithmType.SHIFT;
    static EncDecShiftAlgorithmFactory encdecfac = new EncDecShiftAlgorithmFactory();
    static EncDecShiftAlgorithm encdec = encdecfac.getAlgorithm(alg);

    public static void main(String[] args) {
        if (!readInputParameters(args)) return;
        processParameters();
    }

    static void processParameters() {
        Reader reader = new DataArgumentReader(data);
        Printer printer = new ConsolePrinter();
        if (null != inPath && !isData) {
            reader = new FileReader(inPath);
        }
        if (null != outPath) {
            printer = new FilePrinter(outPath);
        }
        if ("enc".matches(mode)) {
            reader.read().map(s -> encdec.encode(s,key)).forEach(printer::print);
        } else if ("dec".matches(mode)) {
            reader.read().map(s -> encdec.decode(s,key)).forEach(printer::print);
        }
    }

    static boolean readInputParameters(String args[]) {
        boolean res = true;
        String temp = "";
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-mode" -> {
                    temp = args[++i];
                    if (temp.matches(parametersFormat[0])) {
                        mode = temp;
                    } else {
                        System.out.println("mode not supported");
                        res = false;
                    }
                }
                case "-key" -> {
                    temp = args[++i];
                    if (temp.matches(parametersFormat[1])) {
                        key = Integer.parseInt(temp);
                    } else {
                        System.out.println("key is not a number");
                        res = false;
                    }
                }
                case "-data" -> {
                    data = args[++i];
                    isData = true;
                }
                case "-in" -> {
                    if (!Files.isRegularFile(inPath = Path.of(args[++i]))) {
                        res = false;
                    }
                }
                case "-out" -> {
                    if (Files.isDirectory(outPath = Path.of(args[++i])) || Files.isExecutable(outPath)) {
                        res = false;
                    }
                }
                case "-alg" -> {
                    temp = args[++i];
                    if (temp.matches(parametersFormat[3])) {
                        encdec = "unicode".matches(temp) ? encdecfac.getAlgorithm(EncDecAlgorithmType.valueOf(temp.toUpperCase())) : encdec;
                    } else {
                        res = false;
                    }
                }
            }
        }
        return res;
    }
    static abstract class EncDecShiftAlgorithm {

        public String encode(String from, int key) {
            return shift(from, key);
        }

        public String decode(String from, int key) {
            return shift(from, -key);
        }
        abstract String shift(String from, int key);
    }

    enum EncDecAlgorithmType {
        SHIFT,
        UNICODE,
    }

    static class AlphabetShiftEncDec extends EncDecShiftAlgorithm {
        char maxLimInf = 'Z';
        char minLimInf = 'A';
        char minLimSup = 'a';
        char maxLimSup = 'z';

        @Override
        public String shift(String from, int key) {
            StringBuilder sb = new StringBuilder();
            from.chars().map(c -> {
                if (!Character.isAlphabetic(c)) { return c;}
                int range = (1 + maxLimSup - minLimSup);
                int shift = (c + key % range);
                int res = -1;
                if (c >= minLimSup) {
                    res = shift > maxLimSup ? shift - range : shift < minLimSup ? shift + range : shift;
                } else if (c <= maxLimInf) {
                    res = shift > maxLimInf ? shift - range : shift < minLimInf ? shift + range : shift;
                }
                return res;
            }).forEachOrdered(sb::appendCodePoint);
            return sb.toString();
        }
    }

    static class UnicodeShiftEncDec extends EncDecShiftAlgorithm {
        char minLimSup = ' ';
        char maxLimSup = '~';
        @Override
        public String shift(String from, int key) {
            StringBuilder sb = new StringBuilder();
            from.chars().map(c -> {
                int range = (1 + maxLimSup - minLimSup);
                int shift = (c + key % range);
                int res = shift > maxLimSup ? shift - range : shift < minLimSup ? shift + range : shift;
                return res;
            }).forEachOrdered(sb::appendCodePoint);
            return sb.toString();
        }
    }

    static class EncDecShiftAlgorithmFactory {

        public EncDecShiftAlgorithm getAlgorithm(EncDecAlgorithmType type) {
            if (type.equals(EncDecAlgorithmType.SHIFT)) {
                return new AlphabetShiftEncDec();
            } else if (type.equals(EncDecAlgorithmType.UNICODE)) {
                return new UnicodeShiftEncDec();
            }
            System.out.println("algorithm not recognizable");
            return null;
        }
    }
}
