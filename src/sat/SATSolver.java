package sat;

import immutable.ImList;
import sat.env.Environment;
import sat.formula.Clause;
import sat.formula.Formula;
import sat.formula.Literal;

/**
 * A simple DPLL SAT solver. See http://en.wikipedia.org/wiki/DPLL_algorithm
 */
public class SATSolver {
    /**
     * Solve the problem using a simple version of DPLL with backtracking and
     * unit propagation. The returned environment binds literals of class
     * bool.Variable rather than the special literals used in clausification of
     * class clausal.Literal, so that clients can more readily use it.
     * 
     * @return an environment for which the problem evaluates to Bool.TRUE, or
     *         null if no such environment exists.
     */
    public static Environment solve(Formula formula) {
        
        Environment env = new Environment();
        
        if (formula.getSize() == 0) {
            return env;
        }

        env = solve(formula.getClauses(), env);

        return env;
    }

    /**
     * Takes a partial assignment of variables to values, and recursively
     * searches for a complete satisfying assignment.
     * 
     * @param clauses
     *            formula in conjunctive normal form
     * @param env
     *            assignment of some or all variables in clauses to true or
     *            false values.
     * @return an environment for which all the clauses evaluate to Bool.TRUE,
     *         or null if no such environment exists.
     */
    private static Environment solve(ImList<Clause> clauses, Environment env) {
        Clause smallest_clause = clauses.first();

        for (Clause clause : clauses){
            if (clause.isEmpty()) {
                return null;
            }
            else if (clause.size() < smallest_clause.size()){
                    smallest_clause = clause;
                }
        }
        
        if (smallest_clause.isUnit()){
            Literal unit_var = smallest_clause.chooseLiteral();
            // if its 1 literal only : bind it to env and its true
            env.putTrue(unit_var.getVariable());
            // call substitute on this literal and get new simplified cnf
            ImList<Clause> simplified_cnf = substitute(clauses, unit_var);
            // recursively call solve
            solve(simplified_cnf, env);
            
        } else {
            // randomly take one literal from the smallest clause and set to true 
            Literal random_var = smallest_clause.chooseLiteral();

            // positive literal
            ImList<Clause> positive_cnf = substitute(clauses, random_var);
            Environment positive_env = solve(positive_cnf, env);

            if (positive_env != null) {
                positive_env.putTrue(random_var.getVariable());
                return positive_env;
            } 
            // negative literal
            Literal negated_random_var = random_var.getNegation();
            ImList<Clause> negative_cnf = substitute(clauses, negated_random_var);

            Environment negative_env = solve(negative_cnf, env);

            if (negative_env != null) {
                negative_env.putFalse(random_var.getVariable());
                return negative_env;
            }

        }
        return null;

    }

    /**
     * given a clause list and literal, produce a new list resulting from
     * setting that literal to true
     * 
     * @param clauses
     *            , a list of clauses
     * @param l
     *            , a literal to set to true
     * @return a new list of clauses resulting from setting l to true
     */
    private static ImList<Clause> substitute(ImList<Clause> clauses,
            Literal l) {
        // throw new RuntimeException("not yet implemented.");
        // ImList<Clause> new_clauses = new Formula().getClauses();

        for (Clause clause : clauses){
            // remove l from clause
            if (clause.contains(l)){
                // new_clauses.add(clause.reduce(l));
                clause = clause.reduce(l);
            }
        }
        return clauses;
    }
}
