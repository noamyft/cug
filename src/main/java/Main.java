import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import common.CommandLineValues;
import common.MethodMutantData;
import org.kohsuke.args4j.CmdLineException;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
            mutateFile(outputFolder, s_CommandLineValues.File, s_CommandLineValues.MutationLevel);
        } else if (s_CommandLineValues.Dir != null) {
            mutateDir(s_CommandLineValues);
        }


        // prints the resulting compilation unit to default system output
//        System.out.println(cu.toString());
    }

    private static void mutateFile(Path outputFolder, File file, int mutationLevel) {
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
        cu.accept(new MethodVisitor(), methodMutants);

        if (mutationLevel > 1){
            for (MethodMutantData m : methodMutants) {
                Set<MethodDeclaration> oldMutants = m.getMutants();
                for (int i=1; i < mutationLevel; i++){

                    Set<MethodDeclaration> newMutants =
                            oldMutants.stream()
                                    .map(methodDeclaration -> {
                                        List<MethodMutantData> mutants = new LinkedList<>();

                                         methodDeclaration.accept(new MethodVisitor(), mutants);
                                         assert mutants.size() == 1;
                                         return mutants.get(0);
                                    })
                                    .flatMap(methodMutantData -> methodMutantData.getMutants().stream())
                                    .filter(methodDeclaration -> !m.getOriginalMethod().equals(methodDeclaration))
                                    .collect(Collectors.toSet());

                    m.addMutants(newMutants);
                    oldMutants = newMutants;

                    //breaks if num of mutants exceeded the thresh
                    if (m.getMutants().size() > 5000){
                        break;
                    }
                }
            }
        }

        //output to files
        for (MethodMutantData m : methodMutants){
            m.setClassName(file.getName());
            System.out.println(m.getMethodClassName() + "\t" + m.getMutants().size());

            if (!m.getMutants().isEmpty()) {
                //create dir
                Path methodFolder = generateUniqueName(outputFolder, m.getMethodClassName());
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
                            mutateFile(outputFolder, f.toFile(), s_CommandLineValues.MutationLevel);
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

    private static Path generateUniqueName(Path outputFolder, String originalName){
        int i = 0;
        Path methodFolder = Paths.get(outputFolder.toAbsolutePath().toString(), originalName, "src");
        while (methodFolder.toFile().exists()) {
            i++;
            methodFolder = Paths.get(outputFolder.toAbsolutePath().toString(), originalName + "__" + i, "src");
        }
        return methodFolder;
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
