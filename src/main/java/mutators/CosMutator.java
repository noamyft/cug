package mutators;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.sun.javafx.fxml.expression.BinaryExpression;

import java.util.HashSet;

import static com.github.javaparser.ast.expr.BinaryExpr.Operator.*;
import static com.github.javaparser.ast.expr.BinaryExpr.Operator.GREATER;
import static com.github.javaparser.ast.expr.BinaryExpr.Operator.GREATER_EQUALS;

/**
 * Cos mutator - swap operands in commutative operations
 */
public class CosMutator extends VoidVisitorAdapter<HashSet<MethodDeclaration>> {

    private MethodDeclaration originalMethod;

    /*use these methonds in all mutators*/
    public CosMutator(MethodDeclaration method){
        this.originalMethod = method;
    }
    public void addMutant(HashSet<MethodDeclaration> mutants){
        mutants.add(originalMethod.clone());
    }
    /*use these methonds in all mutators - until here*/


    @Override
    public void visit(BinaryExpr exp, HashSet<MethodDeclaration> arg) {
            /* here you need to make the desired mutation.
             after make the mutation, call addMutant (to add it to the list)
             and after that restore originalMethod to it original state
             (the code before your changes) */

        //limit the number of mutants
        if (arg.size() > 20) {
            return;
        }

        Expression left = exp.getLeft();
        Expression right = exp.getRight();
        BinaryExpr.Operator op_type = exp.getOperator();

        if ((op_type == BinaryExpr.Operator.PLUS) ||
                (op_type == BinaryExpr.Operator.MULTIPLY))
        {
            swapOperandsMutantGen(exp, left, right);

            /* add mutant and restore original method*/
            addMutant(arg);
            exp.setLeft(left);
            exp.setRight(right);

        }

        super.visit(exp, arg);
    }

    private void swapOperandsMutantGen(BinaryExpr exp, Expression left, Expression right) {
        BinaryExpression mutant;

        /**
         * the traditional ROR implementation
         */

        exp.setRight(left);
        exp.setLeft(right);
    }
}
