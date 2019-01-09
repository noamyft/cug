package common;

import com.github.javaparser.ast.body.MethodDeclaration;

public class MutantChangePair {
    MethodDeclaration mutant;
    Change change;

    public MutantChangePair(MethodDeclaration mutant, Change change) {
        this.mutant = mutant;
        this.change = change;
    }

    public MethodDeclaration getMutant() {
        return mutant;
    }

    public Change getChange() {
        return change;
    }

}
