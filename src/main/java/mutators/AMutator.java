package mutators;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import common.Change;
import common.MutantLog;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class AMutator {

    final String classPrefix = "public class Test {";
    final String classSuffix = "}";

    protected int maxMutants;
    protected MutantLog originalMutantLog;
//    private HashSet<MethodDeclaration> mutants;

    public AMutator(MutantLog mutantLog, int maxMutants){
        this.originalMutantLog = mutantLog;
        this.maxMutants = maxMutants;
    }

    public abstract HashSet<MutantLog>  getMutants(Set<MethodDeclaration> existingMethods);

    protected static<T extends Node> void getAllNodeOfClass(Node node, List<T> result, Class<T> nodeCls){
        if (node.getClass().equals(nodeCls)){
            result.add((T)node);
        }
        for (Node child : node.getChildNodes()){
            getAllNodeOfClass(child, result, nodeCls);
        }
    }

    public void addNewMutantLog(MethodDeclaration newMutant, Change newChange,
                                HashSet<MutantLog> mutantLogs, Set<MethodDeclaration> existingMethods){
        if (!existingMethods.contains(newMutant)){
            mutantLogs.add(new MutantLog(newMutant, originalMutantLog, newChange));
            existingMethods.add(newMutant);
        }
    }

    public MethodDeclaration cloneMethod(MethodDeclaration method) {
//        TODO: javaparser have bug in parsering
        return method.clone();
//        String methodString = classPrefix + method.toString()  + classSuffix;
//        return (MethodDeclaration) JavaParser.parse(methodString).getType(0).getMember(0);
    }

}
