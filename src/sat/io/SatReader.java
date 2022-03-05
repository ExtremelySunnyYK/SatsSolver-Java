package sat.io;

import sat.formula.*;

import java.io.*;

import static sat.SATSolverTest.makeCl;
import static sat.SATSolverTest.makeFm;


public class SatReader {

    public static Formula formulaReader(String filePath) {
        // TODO : read from command line arguments
        Formula output = new Formula();

        BufferedReader br = null;

        try {
            // File f = new File(root, filePath);
            br = new BufferedReader(new FileReader(filePath));

            String multiLine = "";

            while ((multiLine = br.readLine()) != null) {
                for (String contentLine : multiLine.split(" 0")) {
                    
                    contentLine = contentLine.trim();

                    if (contentLine.equals("")) { continue; }

                    if (contentLine.charAt(0) == 'c') { continue; }

                    if (contentLine.charAt(0) == 'p') { continue; }
                    Literal[] literalArray = checkList(contentLine);
                    Clause clause = makeCl(literalArray);
                    output = output.addClause(clause);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return output;
    }

    private static Literal[] checkList(String contentLine) {
        String[] splitStr = contentLine.split("\\s+");
        Literal[] literalArray = new Literal[splitStr.length];

        for (int i = 0; i < splitStr.length; i++) {
            String s = splitStr[i];
            if (s.charAt(0) == '-') {
                // Negated literal
                literalArray[i] = NegLiteral.make(s.substring(1));
            } else {
                // Positive literal
                literalArray[i] = PosLiteral.make(s);
            }
        }
        return literalArray;
    }
}
