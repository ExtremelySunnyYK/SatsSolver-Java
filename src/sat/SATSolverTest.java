package sat;


import static org.junit.Assert.*;
import org.junit.Test;


import sat.env.*;
import sat.formula.*;
import sat.io.FilePath;
import sat.io.SatReader;
import sat.io.SatWriter;


public class SATSolverTest {
    private static String filepath;
    Literal a = PosLiteral.make("a");
    Literal b = PosLiteral.make("b");
    Literal c = PosLiteral.make("c");
    Literal na = a.getNegation();
    Literal nb = b.getNegation();
    Literal nc = c.getNegation();

    public static void main(String[] args) {
//         Formula f2 = new Formula();
        // pass in the file path as argument
         String filepath = FilePath.FILE_IN_USAT;
         if (args.length != 0) {
             filepath = args[0];
         }
         Formula f2 = SatReader.formulaReader(filepath);
         System.out.println("SAT solver starts!");
         long started = System.nanoTime();

         // Solve for satisfiability
         Environment e = SATSolver.solve(f2);

         if (e == null) {
             System.out.println("unsatisfiable");
         } else {
             System.out.println("satisfiable");
         }

         // Stop timer
         long time = System.nanoTime();
         long timeTaken = time - started;
         System.out.println("Time:" + timeTaken / 1000000.0 + "ms");

         // Write env to BoolAssignment.txt
         if (e!=null) { SatWriter.writer(e); }

    }


	
    public void testSATSolver1(){
    	// (a v b)
    	Environment e = SATSolver.solve(makeFm(makeCl(a,b))	);
        assertTrue ("checking if a or b is true",
    			Bool.TRUE == e.get(a.getVariable())  
    			|| Bool.TRUE == e.get(b.getVariable())	);
    	
    }


    public void testSATSolver2(){
    	// (~a)
        System.out.println("testing...");
    	Environment e = SATSolver.solve(makeFm(makeCl(na)));
        System.out.println(e);
        assertEquals("checking if ", Bool.FALSE, e.get(na.getVariable()));
 	
    }
    
    public static Formula makeFm(Clause... e) {
        Formula f = new Formula();
        for (Clause c : e) {
            f = f.addClause(c);
        }
        return f;
    }
    
    public static Clause makeCl(Literal... e) {
        Clause c = new Clause();
        for (Literal l : e) {
            c = c.add(l);
        }
        return c;
    }
    
    
    
}