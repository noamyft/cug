import com.github.javaparser.ast.body.MethodDeclaration;
import common.CommandLineValues;
import common.MutantLog;
import mutators.AMutator;
import mutators.CosMutator;
import mutators.RosMutator;

import java.util.HashSet;
import java.util.Set;

public class MethodMutationsGenerator {

    CommandLineValues config;


    public MethodMutationsGenerator(CommandLineValues config){
        this.config = config;
    }

    public Set<MutantLog> generateMutants(MutantLog mutantLog, Set<MethodDeclaration> existingMethods){
        System.out.println("generate mutants hi!");
        Set<MethodDeclaration> methods = new HashSet<>();
        Set<MutantLog> result = new HashSet<>();

        //TODO add mutators here
        AMutator[] mutators = new AMutator[]
                {new RosMutator(mutantLog),
                        new CosMutator(mutantLog)};

        for (AMutator mutator : mutators){
            System.out.println(mutator);
            result.addAll(mutator.getMutants(existingMethods));

        }

        System.out.println("generate mutants bye!");
        return result;
    }


}
