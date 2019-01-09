package mutators;

import com.github.javaparser.Range;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import common.Change;
import common.MutantChangePair;
import common.MutantLog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Cos mutator - swap operands in commutative operations
 */
public class CosMutator extends AMutator {

    List<BinaryExpr> allBinaryExpr;

    /*use these methonds in all mutators*/
    public CosMutator(MutantLog method){
        this(method, Integer.MAX_VALUE);
    }
    public CosMutator(MutantLog method, int maxMutants) {
        super(method,maxMutants);

        allBinaryExpr = new ArrayList<>();

        getAllNodeOfClass(method.getMutant(), allBinaryExpr, BinaryExpr.class);

        allBinaryExpr = allBinaryExpr.stream().filter(binaryExpr -> {
            BinaryExpr.Operator op_type = binaryExpr.getOperator();
            return ((op_type == BinaryExpr.Operator.PLUS) ||
                    (op_type == BinaryExpr.Operator.MULTIPLY));
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

            MutantChangePair newResult = generateMutant(b);

            addNewMutantLog(newResult.getMutant(), newResult.getChange(), result, existingMethods);
        }

        return result;
    }

    public MutantChangePair generateMutant(BinaryExpr exp) {
            /* here you need to make the desired mutation.
             after make the mutation, call addMutant (to add it to the list)
             and after that restore originalMethod to it original state
             (the code before your changes) */

        Expression left = exp.getLeft();
        Expression right = exp.getRight();
        BinaryExpr.Operator op_type = exp.getOperator();

        if ((op_type == BinaryExpr.Operator.PLUS) ||
                (op_type == BinaryExpr.Operator.MULTIPLY))
        {

            Range range = exp.getRange().get();
            String oldValue = exp.toString();
            swapOperandsMutantGen(exp, left, right);
            String newValue = exp.toString();

            /* add mutant and restore original method*/
            MethodDeclaration mutant = cloneMethod(originalMutantLog.getMutant());
            exp.setLeft(left);
            exp.setRight(right);

            return new MutantChangePair(mutant, new Change(range, oldValue, newValue));
        }
        throw new RuntimeException(this.getClass() + " must generate mutant! cannot reach here");
    }

    private void swapOperandsMutantGen(BinaryExpr exp, Expression left, Expression right) {

        exp.setRight(left);
        exp.setLeft(right);
    }
}
