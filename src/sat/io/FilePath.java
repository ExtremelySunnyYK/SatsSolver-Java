package sat.io;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class FilePath {
    public static String FILE_IN = "sampleCNF/largeSat.cnf";
    public static String FILE_OUT = "sampleCNF/BoolAssignment.txt";

    public static FileReader getFileIn() throws FileNotFoundException {
        return new FileReader(FILE_IN);
    }
    public static FileReader getFileOut() throws FileNotFoundException{
        return new FileReader(FILE_OUT);
    }
}
