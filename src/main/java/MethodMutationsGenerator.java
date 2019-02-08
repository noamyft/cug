import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import common.CommandLineValues;
import common.MutantLog;
import common.MutatorAstnodePair;
import mutators.AMutator;
import mutators.CosMutator;
import mutators.RosMutator;
import mutators.SnrMutator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MethodMutationsGenerator {

    MethodDeclaration originalMethod;
    CommandLineValues config;
    AMutator[] mutators;


    public MethodMutationsGenerator(MethodDeclaration method){
        this(method, null);
    }

    public MethodMutationsGenerator(MethodDeclaration method, CommandLineValues config){

        this.config = config;
        this.originalMethod = method;
    }

    public int getMutationsCount(){
        return buildMutationVector(originalMethod).size();
    }

    private List<MutatorAstnodePair> buildMutationVector(MethodDeclaration method) {

        //TODO add mutators here
        this.mutators = new AMutator[]
                {new RosMutator(),
                        new CosMutator(), new SnrMutator()};

        List<MutatorAstnodePair> mutationsVec = new ArrayList<>();

        for (AMutator mutator : mutators){

            List<? extends Node> mutations = mutator.getAvailableMutations(method);
            for (Node n : mutations) {
                mutationsVec.add(new MutatorAstnodePair(mutator, n));
            }
        }

        return mutationsVec;
    }

    public MethodDeclaration generateMutantByVector(Set<Integer> selectedMutations){

        MethodDeclaration mutant = originalMethod.clone();
        List<MutatorAstnodePair> mutantMutationsVector = buildMutationVector(mutant);

        for (Integer mutationIndex : selectedMutations){
            AMutator mutator = mutantMutationsVector.get(mutationIndex).getMutator();
            Node changedNode = mutantMutationsVector.get(mutationIndex).getChangeNode();
            mutant = mutator.mutantMethod(mutant, changedNode).getMutant();
        }

        return mutant;
    }

    //    public Set<MutantLog> generateMutants(MutantLog mutantLog, Set<MethodDeclaration> existingMethods){
//
//        Set<MethodDeclaration> methods = new HashSet<>();
//        Set<MutantLog> result = new HashSet<>();
//
//        //TODO add mutators here
//        AMutator[] mutators = new AMutator[]
//                {new RosMutator(mutantLog),
//                        new CosMutator(mutantLog)};
//
//        for (AMutator mutator : mutators){
//
//            result.addAll(mutator.getMutants(existingMethods));
//
//        }
//
//
//        return result;
//    }




}
