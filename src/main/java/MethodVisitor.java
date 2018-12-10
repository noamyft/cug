

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import common.MethodMutantData;
import mutators.CosMutator;
import mutators.RosMutator;

import java.util.HashSet;
import java.util.List;

public class MethodVisitor extends VoidVisitorAdapter<List<MethodMutantData>>  {

        @Override
        public void visit(MethodDeclaration n, List<MethodMutantData> arg) {
            /* here you can access the attributes of the method.
             this method will be called for all methods in this
             CompilationUnit, including inner class methods */
            MethodMutantData methodMutantData = new MethodMutantData(n);

            HashSet<MethodDeclaration> mutants;

            //TODO add mutators here
            mutants = new HashSet<MethodDeclaration>();
            RosMutator ros = new RosMutator(n);
            n.accept(ros, mutants);
            methodMutantData.addMutants(mutants);

            mutants = new HashSet<MethodDeclaration>();
            CosMutator cos = new CosMutator(n);
            n.accept(cos, mutants);
            methodMutantData.addMutants(mutants);


            arg.add(methodMutantData);

            super.visit(n, arg);
        }
}
