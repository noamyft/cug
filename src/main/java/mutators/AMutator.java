package mutators;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import common.MethodMutantData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public abstract class AMutator {

    protected int maxMutants;
    protected MethodDeclaration originalMethod;
//    private HashSet<MethodDeclaration> mutants;

    public AMutator(MethodDeclaration method, int maxMutants){
        this.originalMethod = method;
        this.maxMutants = maxMutants;
    }

    public abstract HashSet<MethodDeclaration>  getMutants();

    protected static<T extends Node> void getAllNodeOfClass(Node node, List<T> result, Class<T> nodeCls){
        if (node.getClass().equals(nodeCls)){
            result.add((T)node);
        }
        for (Node child : node.getChildNodes()){
            getAllNodeOfClass(child, result, nodeCls);
        }
    }

}
