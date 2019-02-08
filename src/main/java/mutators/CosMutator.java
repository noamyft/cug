package mutators;

import com.github.javaparser.Range;
import com.github.javaparser.ast.Node;
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

import static com.github.javaparser.ast.expr.BinaryExpr.Operator.*;
import static com.github.javaparser.ast.expr.BinaryExpr.Operator.LESS;
import static com.github.javaparser.ast.expr.BinaryExpr.Operator.LESS_EQUALS;

/**
 * Cos mutator - swap operands in commutative operations
 */
public class CosMutator extends AMutator {

//    List<BinaryExpr> allBinaryExpr;

    /*use these methonds in all mutators*/
//    public CosMutator(MethodDeclaration method){
//        this(method, Integer.MAX_VALUE);
//    }
//    public CosMutator(MethodDeclaration method, int maxMutants) {
//        super(method,maxMutants);
//
//        allBinaryExpr = new ArrayList<>();
//
//        getAllNodeOfClass(method, allBinaryExpr, BinaryExpr.class);
//
//        allBinaryExpr = allBinaryExpr.stream().filter(binaryExpr -> {
//            BinaryExpr.Operator op_type = binaryExpr.getOperator();
//            return ((op_type == BinaryExpr.Operator.PLUS) ||
//                    (op_type == BinaryExpr.Operator.MULTIPLY));
//        }).collect(Collectors.toList());
//
//        setAvailableMutations(allBinaryExpr);
//
//    }
    /*use these methonds in all mutators - until here*/

//    @Override
//    public HashSet<MutantLog> getMutants(Set<MethodDeclaration> existingMethods) {
//
////        Collections.shuffle(allBinaryExpr);
//
//        HashSet<MutantLog> result = new HashSet<>();
//
//        for (BinaryExpr b : allBinaryExpr){
//            //limit the number of mutants
////            if (result.size() >= this.maxMutants) {
////                return result;
////            }
//
//            MutantChangePair newResult = generateMutant(b);
//
//            addNewMutantLog(newResult.getMutant(), newResult.getChange(), result, existingMethods);
//        }
//
//        return result;
//    }


    @Override
    public List<BinaryExpr> getAvailableMutations(MethodDeclaration method) {
        List<BinaryExpr> allBinaryExpr = new ArrayList<>();

        getAllNodeOfClass(method, allBinaryExpr, BinaryExpr.class);

        allBinaryExpr = allBinaryExpr.stream().filter(binaryExpr -> {
            BinaryExpr.Operator op_type = binaryExpr.getOperator();
            return ((op_type == BinaryExpr.Operator.PLUS) ||
                    (op_type == BinaryExpr.Operator.MULTIPLY));
        }).collect(Collectors.toList());

        return allBinaryExpr;
    }

    @Override
    public MutantChangePair mutantMethod(MethodDeclaration method, Node nodeToMutate) {

        assert nodeToMutate instanceof BinaryExpr;
        BinaryExpr exp = (BinaryExpr) nodeToMutate;

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

            return new MutantChangePair(method, new Change(range, oldValue, newValue));
        }
        throw new RuntimeException(this.getClass() + " must generate mutant! cannot reach here");
    }

//    @Deprecated
//    private MutantChangePair generateMutant(BinaryExpr exp) {
//            /* here you need to make the desired mutation.
//             after make the mutation, call addMutant (to add it to the list)
//             and after that restore originalMethod to it original state
//             (the code before your changes) */
//
//        Expression left = exp.getLeft();
//        Expression right = exp.getRight();
//        BinaryExpr.Operator op_type = exp.getOperator();
//
//        if ((op_type == BinaryExpr.Operator.PLUS) ||
//                (op_type == BinaryExpr.Operator.MULTIPLY))
//        {
//
//            Range range = exp.getRange().get();
//            String oldValue = exp.toString();
//            swapOperandsMutantGen(exp, left, right);
//            String newValue = exp.toString();
//
//            /* add mutant and restore original method*/
//            MethodDeclaration mutant = cloneMethod(originalMethod);
//            exp.setLeft(left);
//            exp.setRight(right);
//
//            return new MutantChangePair(mutant, new Change(range, oldValue, newValue));
//        }
//        throw new RuntimeException(this.getClass() + " must generate mutant! cannot reach here");
//    }

    private void swapOperandsMutantGen(BinaryExpr exp, Expression left, Expression right) {

        exp.setRight(left);
        exp.setLeft(right);
    }
}
