package mutators;

import com.github.javaparser.Range;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.BinaryExpr.Operator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.sun.javafx.fxml.expression.BinaryExpression;
import common.Change;
import common.MutantLog;
import javafx.util.Pair;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.github.javaparser.ast.expr.BinaryExpr.Operator.*;

/**
 * Ros mutator - swap operands in comparison operators
 */
public class RosMutator extends AMutator {

    List<BinaryExpr> allBinaryExpr;

    /*use these methonds in all mutators*/
    public RosMutator(MutantLog method){
        this(method, Integer.MAX_VALUE);
    }
    public RosMutator(MutantLog method, int maxMutants) {
        super(method,maxMutants);

        allBinaryExpr = new ArrayList<>();

        getAllNodeOfClass(method.getMutant(), allBinaryExpr, BinaryExpr.class);

        allBinaryExpr = allBinaryExpr.stream().filter(binaryExpr -> {
            BinaryExpr.Operator op_type = binaryExpr.getOperator();
            return ((op_type == EQUALS) ||
                    (op_type == NOT_EQUALS) ||
                    (op_type == GREATER) ||
                    (op_type == GREATER_EQUALS) ||
                    (op_type == LESS) ||
                    (op_type == LESS_EQUALS));
        }).collect(Collectors.toList());

    }
    /*use these methonds in all mutators - until here*/

    @Override
    public HashSet<MutantLog> getMutants(Set<MethodDeclaration> existingMethods) {

//        Collections.shuffle(allBinaryExpr);

        HashSet<MutantLog> result = new HashSet<>();

        for (BinaryExpr b : allBinaryExpr){
            //limit the number of mutants
//            if (result.size() >= this.maxMutants) {
//                return result;
//            }

            Pair<MethodDeclaration, Change> newResult = generateMutant(b);

            addNewMutantLog(newResult.getKey(), newResult.getValue(), result, existingMethods);

        }

        return result;
    }

    private Pair<MethodDeclaration, Change> generateMutant(BinaryExpr exp) {
            /* here you need to make the desired mutation.
             after make the mutation, call addMutant (to add it to the list)
             and after that restore originalMethod to it original state
             (the code before your changes) */


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
            Range range = exp.getRange().get();
            String oldValue = exp.toString();
            swapRelationalOperandsMutantGen(exp, left, right, op_type);
            String newValue = exp.toString();

            /* add mutant and restore original method*/
            MethodDeclaration mutant = cloneMethod(originalMutantLog.getMutant());
            exp.setLeft(left);
            exp.setRight(right);
            exp.setOperator(op_type);

            return new Pair<>(mutant, new Change(range, oldValue, newValue));
        }

        throw new RuntimeException(this.getClass() + " must generate mutant! cannot reach here");
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
