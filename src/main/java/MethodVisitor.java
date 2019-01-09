

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import common.CommandLineValues;
import common.MethodMutantData;
import mutators.AMutator;
import mutators.CosMutator;
import mutators.RosMutator;
import mutators.VnrMutator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class MethodVisitor extends VoidVisitorAdapter<List<MethodMutantData>>  {

    CommandLineValues config;
    Set<MethodDeclaration> existingMethods;

    public MethodVisitor(CommandLineValues config, Set<MethodDeclaration> existingMethods){
        this.config = config;
        this.existingMethods = existingMethods;
    }

    @Override
    public void visit(MethodDeclaration n, List<MethodMutantData> arg) {
        /* here you can access the attributes of the method.
         this method will be called for all methods in this
         CompilationUnit, including inner class methods */
        MethodMutantData methodMutantData = new MethodMutantData(n);

        //TODO add mutators here
//        AMutator[] mutators = new AMutator[]
//                {new RosMutator(n, config.MaxMutantsPerMutator),
//                        new CosMutator(n, config.MaxMutantsPerMutator),
//                        new VnrMutator(n, config.MaxMutantsPerMutator)};

//        for (AMutator mutator : mutators){
//            methodMutantData.addMutants(mutator.getMutants());
//        }

        arg.add(methodMutantData);

        super.visit(n, arg);
    }

    public static class BernulliRandomizer implements Supplier<Boolean>{

        double probabilityForTrue;
        public BernulliRandomizer(double probabilityForTrue){
            this.probabilityForTrue = probabilityForTrue;
        }

        @Override
        public Boolean get() {
            return Math.random() <= this.probabilityForTrue;
        }
    }
}
