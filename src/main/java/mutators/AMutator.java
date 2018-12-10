package mutators;

import com.github.javaparser.ast.body.MethodDeclaration;
import java.util.HashSet;

public abstract class AMutator {

    private MethodDeclaration originalMethod;
    private HashSet<MethodDeclaration> mutants;

    public AMutator(MethodDeclaration method){
        this.originalMethod = method;
        mutants = new HashSet<MethodDeclaration>();
    }

    public HashSet<MethodDeclaration> getMutants(){
        return mutants;
    }

    public void addMutant(){
        mutants.add(originalMethod.clone());
    }

}
