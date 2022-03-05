package sat;

import immutable.EmptyImList;
import immutable.ImList;
import sat.env.Environment;
import sat.env.Variable;
import sat.formula.*;

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
     * null if no such environment exists.
     */
    public static Environment solve(Formula formula) {
        Environment env = new Environment();

        if (formula.getSize() == 0) {
            return env;
        }
//        System.out.println(formula.getClauses());
//        System.out.println("FORMULA>GETCLAUUSES");
        env = solve(formula.getClauses(), env);

        return env;
    }

    /**
     * Takes a partial assignment of variables to values, and recursively
     * searches for a complete satisfying assignment.
     *
     * @param clauses formula in conjunctive normal form
     * @param env     assignment of some or all variables in clauses to true or
     *                false values.
     * @return an environment for which all the clauses evaluate to Bool.TRUE,
     * or null if no such environment exists.
     */
    private static Environment solve(ImList<Clause> clauses, Environment env) {
        if (clauses.isEmpty()) {
            return env;
        }
        Clause smallest_clause = clauses.first();

        for (Clause clause : clauses) {
            if (clause.isEmpty()) {
                return null;
            } else if (clause.size() < smallest_clause.size()) {
                smallest_clause = clause;
            }
        }

        if (smallest_clause.isUnit()) {
            Literal unit_var = smallest_clause.chooseLiteral();
            // if its 1 literal only : bind it to env and its truthy value
            env = checkLiteral(unit_var,env);
            // call substitute on this literal and get new simplified cnf
            ImList<Clause> simplified_cnf = substitute(clauses, unit_var);
            // recursively call solve
            return solve(simplified_cnf, env);

        } else {
            // randomly take one literal from the smallest clause and set to true 
            Literal random_lit = smallest_clause.chooseLiteral();

            // positive literal
            ImList<Clause> positive_cnf = substitute(clauses, random_lit);
            Environment positive_env = solve(positive_cnf, env);

            if (positive_env != null) {
                positive_env = checkLiteral(random_lit, positive_env);
                return positive_env;
            }
            // negative literal
            Literal negated_random_lit = random_lit.getNegation();
            ImList<Clause> negative_cnf = substitute(clauses, negated_random_lit);

            Environment negative_env = solve(negative_cnf, env);

            if (negative_env != null) {
                negative_env = checkLiteral(negated_random_lit, negative_env);
                return negative_env;
            }
        }
        return null;
    }

    private static Environment checkLiteral(Literal random_lit, Environment env) {
        Variable random_var = random_lit.getVariable();
        if (random_lit instanceof PosLiteral) {
            env = env.putTrue(random_var);
        } else {
            env = env.putFalse(random_var);
        }
        return env;
    }

    /**
     * given a clause list and literal, produce a new list resulting from
     * setting that literal to true
     *
     * @param clauses , a list of clauses
     * @param l       , a literal to set to true
     * @return a new list of clauses resulting from setting l to true
     */
    private static ImList<Clause> substitute(ImList<Clause> clauses,
                                             Literal l) {
        ImList<Clause> new_clauses = new EmptyImList<Clause>();

        for (Clause clause : clauses) {
            // remove l from clause
            if (clause.contains(l) || clause.contains(l.getNegation())) {
                Clause reducedClause = clause.reduce(l); // would reduce return us empty clause?
                if (reducedClause != null) {
                    new_clauses = new_clauses.add(reducedClause);
                }
//                clause = clause.reduce(l);
            } else {
                new_clauses = new_clauses.add(clause);
            }
        }
        return new_clauses;
    }
}
