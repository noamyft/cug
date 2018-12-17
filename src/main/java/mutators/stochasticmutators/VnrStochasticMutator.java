package mutators.stochasticmutators;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.sun.javafx.fxml.expression.BinaryExpression;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.sun.javafx.fxml.expression.BinaryExpression;

import java.util.*;

import static com.github.javaparser.ast.expr.BinaryExpr.Operator.*;
import static com.github.javaparser.ast.expr.BinaryExpr.Operator.GREATER;
import static com.github.javaparser.ast.expr.BinaryExpr.Operator.GREATER_EQUALS;

import java.util.HashSet;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Vnr mutator -
 */
public class VnrStochasticMutator extends VoidVisitorAdapter<HashSet<MethodDeclaration>> {

    private MethodDeclaration originalMethod;
    private Supplier<Boolean> booleanRandomizer;
    private Set<String> variables;
    private Set<String> allNames;
    private Map<String,String> refactorVariables;

    /*use these methonds in all mutators*/
    public VnrStochasticMutator(MethodDeclaration method){
        this(method, () -> true);
    }

    public VnrStochasticMutator(MethodDeclaration method, Supplier<Boolean> booleanRandomizer){
        this.originalMethod = method;
        this.booleanRandomizer = booleanRandomizer;
        this.variables = new HashSet<>();
        this.allNames = new HashSet<>();


        method.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(VariableDeclarationExpr n, Void arg)
            {
                List<VariableDeclarator> myVars = n.getVariables();
                Set<String> varsNames = myVars.stream().map(v -> v.getName().asString())
                        .filter((s) -> s.length() > 1).collect(Collectors.toSet());
                variables.addAll(varsNames);
            }
            @Override
            public void visit(Parameter n, Void arg)
            {
                if (n.getName().asString().length() > 1){
                    variables.add(n.getName().asString());
                }
            }
        }, null);

        method.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(SimpleName n, Void arg)
            {
                allNames.add(n.getIdentifier());
            }
        }, null);

//        this.refactorVariables = renameVars(variables, booleanRandomizer);
    }

    public void addMutant(HashSet<MethodDeclaration> mutants){
        mutants.add(originalMethod.clone());
    }
    /*use these methonds in all mutators - until here*/


    @Override
    public void visit(MethodDeclaration method, HashSet<MethodDeclaration> arg) {
            /* here you need to make the desired mutation.
             after make the mutation, call addMutant (to add it to the list)
             and after that restore originalMethod to it original state
             (the code before your changes) */

        for (String v : this.variables){
            //limit the number of mutants
            if (arg.size() > 20) {
                return;
            }

            if (!this.booleanRandomizer.get()){
                continue;
            }
            String newName = createNewName(v, this.allNames);
            if (newName.equals(v)){
                continue;
            }
            MethodDeclaration mutant = method.clone();
            mutant.accept(new VoidVisitorAdapter<Void>() {
                @Override
                public void visit(SimpleName n, Void arg)
                {
                    if (n.getIdentifier().equals(v)){
                        n.setIdentifier(newName);
                    }
                }
            }, null);

            arg.add(mutant);
        }

        super.visit(method, arg);
    }

    private HashMap<String, String> renameVars(Set<String> variables, Supplier<Boolean> booleanRandomizer) {

        HashMap<String, String> result = new HashMap<>();

        for (String v : variables){
            if (v.length() ==  1){
                continue;
            }
            if (booleanRandomizer.get()){
                for (int i=1; i <= v.length(); i++){
                    String newName = v.substring(0,i).toLowerCase();
                    if (variables.stream().filter(s -> s.equals(newName)).count() == 0 &&
                            result.values().stream().filter(s -> s.equals(newName)).count() == 0){
                        result.put(v, newName);
                        break;
                    }
                }
            }
        }

        return result;
    }

    private String createNewName(String var, Set<String> variables) {

        for (int i=1; i < var.length(); i++){
            String newName = var.substring(0,i).toLowerCase();
            if (variables.stream().filter(s -> s.equals(newName)).count() == 0){
                return newName;
            }
        }

        return var;
    }
}
