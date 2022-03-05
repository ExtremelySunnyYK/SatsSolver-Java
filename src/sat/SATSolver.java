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

        return solve(formula.getClauses(), env);
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

        Literal unit_lit = smallest_clause.chooseLiteral();

        if (smallest_clause.isUnit()) {
            // if its 1 literal only : bind it to env and its truthy value
            env = checkLiteral(unit_lit,env);
            // call substitute on this literal and get new simplified cnf
            ImList<Clause> simplified_cnf = substitute(clauses, unit_lit);
            // recursively call solve
            return solve(simplified_cnf, env);

        } else {

            if (!(unit_lit instanceof PosLiteral)) {
                unit_lit = unit_lit.getNegation();
            }
            // positive literal
            ImList<Clause> positive_cnf = substitute(clauses, unit_lit);
            Environment positive_env = solve(positive_cnf, env);

            if (positive_env != null) {
                positive_env = env.putTrue(unit_lit.getVariable());
                return positive_env;
            }

            env = env.putFalse(unit_lit.getVariable());
            Literal negated_random_lit = unit_lit.getNegation();
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
        for (Clause clause : clauses) {
            if (clause.contains(l)) {
                clauses = clauses.remove(clause);
            }
            else if (clause.contains(l.getNegation())) {
                clauses = clauses.remove(clause);
                clauses = clauses.add(clause.reduce(l));
            }
        }
        return clauses;
    }
}
