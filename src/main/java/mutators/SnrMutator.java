package mutators;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import common.Change;
import common.MutantChangePair;


import java.util.*;

import java.util.HashSet;

/**
 * Snr mutator - simple variable name
 */

public class SnrMutator extends AMutator {

    private static final String MAGIC_VAR_NAME = "zpkjxq";
//    private Set<Node> variables;
//    private Set<String> allNames;
//    private Map<String,String> refactorVariables;

    /*use these methonds in all mutators*/
//    public SnrMutator(MethodDeclaration method){
//        this(method, Integer.MAX_VALUE);
//    }

//    public SnrMutator(MethodDeclaration method, int maxMutants){
////        super(method,maxMutants);
//        this.variables = new HashSet<>();
//        this.allNames = new HashSet<>();
//
//
//        method.accept(new VoidVisitorAdapter<Void>() {
//            @Override
//            public void visit(VariableDeclarationExpr n, Void arg)
//            {
//                List<VariableDeclarator> myVars = n.getVariables();
////                Set<String> varsNames = myVars.stream().map(v -> v.getName().asString())
////                        .filter((s) -> s.length() > 1).collect(Collectors.toSet());
//                variables.addAll(myVars);
//                super.visit(n, arg);
//            }
//            @Override
//            public void visit(Parameter n, Void arg)
//            {
//                variables.add(n);
////                if (n.getName().asString().length() > 1){
////                    variables.add(n.getName().asString());
////                }
//                super.visit(n, arg);
//            }
//        }, null);
//
//        method.accept(new VoidVisitorAdapter<Void>() {
//            @Override
//            public void visit(SimpleName n, Void arg)
//            {
//                allNames.add(n.getIdentifier());
//                super.visit(n, arg);
//            }
//        }, null);
//
//    }
    /*use these methonds in all mutators - until here*/

    @Override
    public List<? extends Node> getAvailableMutations(MethodDeclaration method) {
        List<Node> variables = new ArrayList<>();

        method.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(VariableDeclarationExpr n, Void arg)
            {
                List<VariableDeclarator> myVars = n.getVariables();
                variables.addAll(myVars);
                super.visit(n, arg);
            }
            @Override
            public void visit(Parameter n, Void arg)
            {
                variables.add(n);
                super.visit(n, arg);
            }
        }, null);

        return variables;
    }

    @Override
    public MutantChangePair mutantMethod(MethodDeclaration method, Node nodeToMutate) {

        assert nodeToMutate instanceof NodeWithSimpleName;
        NodeWithSimpleName varNode = (NodeWithSimpleName) nodeToMutate;
        String oldName = varNode.getNameAsString();
        String newName = createGenericName(method);

        method.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(SimpleName n, Void arg)
            {
                if (n.getIdentifier().toLowerCase().equals(oldName)){
                    n.setIdentifier(newName);
                }
            }
        }, null);

        return new MutantChangePair(method, new Change(nodeToMutate.getRange().get(), oldName, newName));
    }

//    public HashSet<MethodDeclaration> getMutants() {
//            /* here you need to make the desired mutation.
//             after make the mutation, call addMutant (to add it to the list)
//             and after that restore originalMethod to it original state
//             (the code before your changes) */
//        HashSet<MethodDeclaration> result = new HashSet<>();

//        List<String> vars = new ArrayList<>(this.variables);
//        Collections.shuffle(vars);
//
//        for (String v : vars){
//            //limit the number of mutants
//            if (result.size() >= this.maxMutants) {
//                return result;
//            }
//
//            String newName = createGenericName(v, this.allNames);
//            if (newName.equals(v)){
//                continue;
//            }
//            MethodDeclaration mutant = originalMethod.clone();
//            mutant.accept(new VoidVisitorAdapter<Void>() {
//                @Override
//                public void visit(SimpleName n, Void arg)
//                {
//                    if (n.getIdentifier().equals(v)){
//                        n.setIdentifier(newName);
//                    }
//                }
//            }, null);
//
//            result.add(mutant);
//        }
//        return result;
//    }

//    private HashMap<String, String> renameVars(Set<String> variables, Supplier<Boolean> booleanRandomizer) {
//
//        HashMap<String, String> result = new HashMap<>();
//
//        for (String v : variables){
//            if (v.length() ==  1){
//                continue;
//            }
//            if (booleanRandomizer.get()){
//                for (int i=1; i <= v.length(); i++){
//                    String newName = v.substring(0,i).toLowerCase();
//                    if (variables.stream().filter(s -> s.equals(newName)).count() == 0 &&
//                            result.values().stream().filter(s -> s.equals(newName)).count() == 0){
//                        result.put(v, newName);
//                        break;
//                    }
//                }
//            }
//        }
//
//        return result;
//    }

//    private String createGenericName(String var, Set<String> variables) {
//
//        for (int i=1; i < var.length(); i++){
//            String newName = var.substring(0,i).toLowerCase();
//            if (variables.stream().filter(s -> s.equals(newName)).count() == 0){
//                return newName;
//            }
//        }
//
//        return var;
//    }

    private String createGenericName(MethodDeclaration method) {

        Set<String> allNames = getAllNames(method);

        for (char c='a'; c <= 'z'; c++){
            String newName = (MAGIC_VAR_NAME + c).toLowerCase();
            if (allNames.stream().filter(s -> s.equals(newName)).count() == 0){
                return newName;
            }
        }

        throw new RuntimeException(this.getClass() + " try to rename more than 26 vars");
    }

    private Set<String> getAllNames(MethodDeclaration method){

        Set<String> allNames = new HashSet<>();

        method.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(SimpleName n, Void arg)
            {
                allNames.add(n.getIdentifier());
                super.visit(n, arg);
            }
        }, null);

        return allNames;
    }
}
