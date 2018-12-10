package common;

import com.github.javaparser.ast.body.MethodDeclaration;

import java.util.HashSet;
import java.util.Set;

public class MethodMutantData {

    String className;
    MethodDeclaration originalMethod;
    Set<MethodDeclaration> mutants;

    public String getMethodClassName() {
        return className + "_" + originalMethod.getNameAsString();
    }

    public String getMethodName() {
        return originalMethod.getNameAsString();
    }

    public MethodDeclaration getOriginalMethod() {
        return originalMethod;
    }

    public Set<MethodDeclaration> getMutants() {
        return mutants;
    }

    public MethodMutantData(MethodDeclaration originalMethod) {
        this.className = "";
        this.originalMethod = originalMethod;
        this.mutants = new HashSet<>();
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void addMutants(Set<MethodDeclaration> mutants){
        this.mutants.addAll(mutants);
    }
}
