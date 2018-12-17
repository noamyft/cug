import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import common.CommandLineValues;
import common.MethodMutantData;
import org.kohsuke.args4j.CmdLineException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws Exception {

        CommandLineValues s_CommandLineValues;
        try {
            s_CommandLineValues = new CommandLineValues(args);
        } catch (CmdLineException e) {
            e.printStackTrace();
            return;
        }

        Path outputFolder = Paths.get(s_CommandLineValues.Out);

        if (s_CommandLineValues.File != null) {
            mutateFile(outputFolder, s_CommandLineValues.File, s_CommandLineValues);
        } else if (s_CommandLineValues.Dir != null) {
            mutateDir(s_CommandLineValues);
        }

//         TODO: FOR DEBUGING
//        // parse it
//        FileInputStream in = new FileInputStream("Aug.java");
//        CompilationUnit cu = JavaParser.parse(in);

            // Go through all the types in the file
//            NodeList<TypeDeclaration<?>> types = cu.getTypes();
//            for (TypeDeclaration<?> type : types) {
//                // Go through all fields, methods, etc. in this type
//                NodeList<BodyDeclaration<?>> members = type.getMembers();
//                for (BodyDeclaration<?> member : members) {
//                    if (member instanceof MethodDeclaration) {
//                        MethodDeclaration method = (MethodDeclaration) member;
//                    }
//                }
//            }

            // visit and print the methods names
//            cu.accept(new TestVisitor(), null);

        //         prints the resulting compilation unit to default system output
//        System.out.println(cu.toString());
//         END: FOR DEBUGING


    }

    private static void mutateFile(Path outputFolder, File file, CommandLineValues config) {
        // creates an input stream for the file to be parsed
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        // parse the file
        CompilationUnit cu = JavaParser.parse(in);

        List<MethodMutantData> methodMutants = new LinkedList<>();
        // visit and print the methods names
        cu.accept(new MethodVisitor(config), methodMutants);

        if (config.MutationLevel > 1){
            for (MethodMutantData m : methodMutants) {
                Set<MethodDeclaration> oldMutants = m.getMutants();
                for (int i=1; i < config.MutationLevel; i++){

                    Set<MethodDeclaration> newMutants =
                            oldMutants.stream()
                                    .map(methodDeclaration -> {
                                        List<MethodMutantData> mutants = new LinkedList<>();

                                         methodDeclaration.accept(new MethodVisitor(config), mutants);
                                         assert mutants.size() == 1;
                                         return mutants.get(0);
                                    })
                                    .flatMap(methodMutantData -> methodMutantData.getMutants().stream())
                                    .filter(methodDeclaration -> !m.getOriginalMethod().equals(methodDeclaration))
                                    .collect(Collectors.toSet());

                    m.addMutants(newMutants);
                    oldMutants = newMutants;

                    //breaks if num of mutants exceeded the thresh
//                    if (m.getMutants().size() > 5000){
//                        break;
//                    }
                }
            }
        }

        //output to files
        for (MethodMutantData m : methodMutants){
            m.setClassName(file.getName());
            System.out.println(m.getMethodClassName() + "\t" + m.getMutants().size());

            if (!m.getMutants().isEmpty()) {
                //create dir
                Path methodFolder = generateUniqueFolderName(outputFolder, m.getMethodClassName());
                methodFolder.toFile().mkdirs();

                PrintWriter writer = null;
                try {
                    //create original file
                    File originalMethodFile = Paths.get(methodFolder.toAbsolutePath().toString(), m.getMethodName() + ".java").toFile();
                    writer = new PrintWriter(originalMethodFile, "UTF-8");
                    writer.println(m.getOriginalMethod());
                    writer.close();

                    //create mutant file
                    File mutantMethodFile = Paths.get(methodFolder.toAbsolutePath().toString(), m.getMethodName() + "_mutants.java").toFile();
                    writer = new PrintWriter(mutantMethodFile, "UTF-8");
                    for (MethodDeclaration md : m.getMutants()) {
                        writer.println(md);
                    }
                    writer.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } finally {
                    if (writer != null) writer.close();
                }
            }
        }
    }

    private static void mutateDir(CommandLineValues s_CommandLineValues) {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(s_CommandLineValues.NumThreads);
        LinkedList<Callable<Void>> tasks = new LinkedList<>();
        Path outputFolder = Paths.get(s_CommandLineValues.Out);
        try {
            Files.walk(Paths.get(s_CommandLineValues.Dir)).filter(Files::isRegularFile)
                    .filter(p -> p.toString().toLowerCase().endsWith(".java"))
                    .forEach(f -> {
                        tasks.add(() -> {
                            mutateFile(outputFolder, f.toFile(), s_CommandLineValues);
                            return null;
                        });
                    });
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try {
            executor.invokeAll(tasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }

    }

    private static Path generateUniqueFolderName(Path outputFolder, String originalName){
        int i = 0;
        Path methodFolder = Paths.get(outputFolder.toAbsolutePath().toString(), originalName.toLowerCase(), "src");
        while (methodFolder.toFile().exists()) {
            i++;
            methodFolder = Paths.get(outputFolder.toAbsolutePath().toString(), originalName.toLowerCase() + "__" + i, "src");
        }
        return methodFolder;
    }

    /**
     * Simple visitor implementation for visiting MethodDeclaration nodes.
     */
    private static class TestVisitor extends VoidVisitorAdapter<Void> {
//        @Override
//        public void visit(NameExpr n, Void arg) {
//            /* here you can access the attributes of the method.
//             this method will be called for all methods in this
//             CompilationUnit, including inner class methods */
//            if (n.toString().equals("n")) {
//                System.out.println(n + "--" + n.getParentNode().get() + "--" + n.getParentNode().get().getParentNode().get());
//            }
//
//            super.visit(n, arg);
//        }
        @Override
        public void visit(VariableDeclarationExpr n, Void arg)
        {
            List <VariableDeclarator> myVars = n.getVariables();
            for (VariableDeclarator vars: myVars){
                System.out.println("Variable Name: "+vars.getName());
            }
        }

        @Override
        public void visit(Parameter n, Void arg)
        {
//            List <VariableDeclarator> myVars = n.getVariables();
//            for (VariableDeclarator vars: myVars){
                System.out.println("Param Name: "+n.getName());
//            }
        }
    }
    /**
     * Simple visitor implementation for visiting MethodDeclaration nodes.
     */
//    private static class MethodVisitor extends VoidVisitorAdapter<Void> {
//        pri
//        @Override
//        public void visit(MethodDeclaration n, Void arg) {
//            /* here you can access the attributes of the method.
//             this method will be called for all methods in this
//             CompilationUnit, including inner class methods */
//            RosMutator mutator = new RosMutator(n);
//            n.accept(mutator, null);
//            for (MethodDeclaration m : mutator.getMutants())
//                System.out.println(m);
//
//            super.visit(n, arg);
//        }
//    }
}
