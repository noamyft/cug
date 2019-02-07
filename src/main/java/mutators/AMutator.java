package mutators;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import common.Change;
import common.MutantChangePair;
import common.MutantLog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class AMutator {

    final String classPrefix = "public class Test {";
    final String classSuffix = "}";

    protected int maxMutants;
    protected MethodDeclaration originalMethod;
    protected List<Node> availableMutations;
//    private HashSet<MethodDeclaration> mutants;

    public AMutator(MethodDeclaration mutant, int maxMutants){
        this.originalMethod = mutant;
        this.maxMutants = maxMutants;
        this.availableMutations = new ArrayList<>();
    }

    @Deprecated
    public abstract HashSet<MutantLog>  getMutants(Set<MethodDeclaration> existingMethods);

    /**
     * this methos mutate in-place (and override the original method)
     *
     * @param nodeToMutate
     * @return
     */
    public abstract MutantChangePair mutantMethod(Node nodeToMutate);


    public List<Node>  getAvailableMutations(){
        return availableMutations;
    };

    public void setAvailableMutations(List<? extends Node> availableMutations) {
        this.availableMutations.clear();
        this.availableMutations.addAll(availableMutations);
    }

    protected static<T extends Node> void getAllNodeOfClass(Node node, List<T> result, Class<T> nodeCls){
        if (node.getClass().equals(nodeCls)){
            result.add((T)node);
        }
        for (Node child : node.getChildNodes()){
            getAllNodeOfClass(child, result, nodeCls);
        }
    }

    @Deprecated
    public void addNewMutantLog(MethodDeclaration newMutant, Change newChange,
                                HashSet<MutantLog> mutantLogs, Set<MethodDeclaration> existingMethods){
        if (!existingMethods.contains(newMutant)){
//            mutantLogs.add(new MutantLog(newMutant, originalMethod, newChange));
//            existingMethods.add(newMutant);
        }
    }

    public MethodDeclaration cloneMethod(MethodDeclaration method) {
//        TODO: javaparser have bug in parsering
        return method.clone();
//        String methodString = classPrefix + method.toString()  + classSuffix;
//        return (MethodDeclaration) JavaParser.parse(methodString).getType(0).getMember(0);
    }

}
