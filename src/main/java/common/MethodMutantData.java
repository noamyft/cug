package common;

import com.github.javaparser.ast.body.MethodDeclaration;

import java.util.HashSet;
import java.util.Set;

public class MethodMutantData {

    String className;
    MethodDeclaration originalMethod;

    int requestedMutationLevel;
    int actualMutationLevel;
    Set<Set<Integer>> mutants;

    public String getMethodClassName() {
        return className + "_" + originalMethod.getNameAsString();
    }

    public String getMethodName() {
        return originalMethod.getNameAsString();
    }

    public MethodDeclaration getOriginalMethod() {
        return originalMethod;
    }

    public Set<Set<Integer>> getMutants() {
        return mutants;
    }

    public MethodMutantData(MethodDeclaration originalMethod) {
        this.className = "";
        this.originalMethod = originalMethod;
        this.mutants = new HashSet<>();
        requestedMutationLevel = 0;
        actualMutationLevel = 0;
    }

    public int getRequestedMutationLevel() {
        return requestedMutationLevel;
    }

    public int getActualMutationLevel() {
        return actualMutationLevel;
    }

    public void setRequestedMutationLevel(int requestedMutationLevel) {
        this.requestedMutationLevel = requestedMutationLevel;
    }

    public void setActualMutationLevel(int actualMutationLevel) {
        this.actualMutationLevel = actualMutationLevel;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void addMutantToLog(Set<Set<Integer>> mutantLogs){
        this.mutants.addAll(mutantLogs);
    }
}
