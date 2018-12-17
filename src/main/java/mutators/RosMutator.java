package mutators;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.BinaryExpr.Operator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.sun.javafx.fxml.expression.BinaryExpression;

import java.util.HashSet;
import java.util.function.Supplier;

import static com.github.javaparser.ast.expr.BinaryExpr.Operator.*;

/**
 * Ros mutator - swap operands in comparison operators
 */
public class RosMutator extends VoidVisitorAdapter<HashSet<MethodDeclaration>> {

    private Supplier<Boolean> booleanRandomizer;
    private MethodDeclaration originalMethod;

    /*use these methonds in all mutators*/
    public RosMutator(MethodDeclaration method){
        this(method, () -> true);
    }
    public RosMutator(MethodDeclaration method, Supplier<Boolean> booleanRandomizer) {
        this.originalMethod = method;
        this.booleanRandomizer = booleanRandomizer;
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
//        if (arg.size() > 20) {
//            return;
//        }

        //decide whether mutant this not or not
        if (!this.booleanRandomizer.get()){
            super.visit(exp, arg);
            return;
        }

        Expression left = exp.getLeft();
        Expression right = exp.getRight();
        BinaryExpr.Operator op_type = exp.getOperator();

        if ((op_type == EQUALS) ||
                (op_type == NOT_EQUALS) ||
                (op_type == GREATER) ||
                (op_type == GREATER_EQUALS) ||
                (op_type == LESS) ||
                (op_type == LESS_EQUALS))
        {
            swapRelationalOperandsMutantGen(exp, left, right, op_type);


            /* add mutant and restore original method*/
            addMutant(arg);
            exp.setLeft(left);
            exp.setRight(right);
            exp.setOperator(op_type);

        }

        super.visit(exp, arg);
    }

    private void swapRelationalOperandsMutantGen(BinaryExpr exp, Expression left, Expression right, BinaryExpr.Operator op_type) {
        BinaryExpression mutant;

        /**
         * the traditional ROR implementation
         */
        Operator oppositeOperator = mirrorRelationalOperator(op_type);
        exp.setRight(left);
        exp.setLeft(right);
        exp.setOperator(oppositeOperator);
    }

    private Operator mirrorRelationalOperator(Operator op) {
        switch (op){
            case EQUALS:
                return EQUALS;
            case NOT_EQUALS:
                return NOT_EQUALS;
            case GREATER:
                return LESS;
            case GREATER_EQUALS:
                return LESS_EQUALS;
            case LESS:
                return GREATER;
            case LESS_EQUALS:
                return GREATER_EQUALS;
            default:
                throw new RuntimeException("invalid operator. must be: ==, !=, >=, >, <, <=");
        }

    }
}
