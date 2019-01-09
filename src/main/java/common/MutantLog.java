package common;

import com.github.javaparser.ast.body.MethodDeclaration;

import java.util.ArrayList;
import java.util.List;

public class MutantLog {


    MethodDeclaration mutant;
    List<Change> changes;

    public MutantLog(MethodDeclaration mutant, List<Change> changes) {
        this.mutant = mutant;
        this.changes = new ArrayList<>(changes);
    }

    public MutantLog(MethodDeclaration mutant, MutantLog oldLog, Change newChange) {
        this.mutant = mutant;
        this.changes = new ArrayList<>(oldLog.changes);
        appendChange(newChange);
    }

    public MutantLog(MethodDeclaration mutant) {
        this.mutant = mutant;
        this.changes = new ArrayList<>();
    }

    public int getMutationLevel(){
        return changes.size();
    }

    public MethodDeclaration getMutant() {
        return mutant;
    }

    public List<Change> getChanges() {
        return changes;
    }

    public void appendChange(Change change) {
        changes.add(change);
    }



}
