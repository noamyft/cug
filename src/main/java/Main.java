import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import common.CommandLineValues;
import common.MethodMutantData;
import common.MutantLog;
import org.kohsuke.args4j.CmdLineException;
import serialization.MutantFileIO;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedList;
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

        Instant start = Instant.now();

        Path outputFolder = Paths.get(s_CommandLineValues.Out);
        if (outputFolder != null){
            outputFolder.toFile().mkdirs();
        }

        if (s_CommandLineValues.ExtractMethods){
            if (s_CommandLineValues.File != null) {
                extractMethodsInFile(outputFolder, Paths.get(s_CommandLineValues.File));
            } else if (s_CommandLineValues.Dir != null) {
                extractMethodsInDir(s_CommandLineValues);
            }
        } else if (s_CommandLineValues.MutateMethods){
            if (s_CommandLineValues.File != null) {
                mutateMethodInFile(Paths.get(s_CommandLineValues.File), s_CommandLineValues);
            } else if (s_CommandLineValues.Dir != null) {
                mutateMethodInDir(s_CommandLineValues);
            }
        } else if (s_CommandLineValues.OutputMethods){
            if (s_CommandLineValues.File != null) {
                outputMutantsFromFile(outputFolder, Paths.get(s_CommandLineValues.File),
                        s_CommandLineValues.FromLevel, s_CommandLineValues.ToLevel);
            } else if (s_CommandLineValues.Dir != null) {
                outputMutantsFromDir(s_CommandLineValues);
            }
        }

        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        System.out.println("Time taken: "+ timeElapsed.toMillis() +" milliseconds");


//         TODO: FOR DEBUGING
        // parse it
//        FileInputStream in = new FileInputStream("Aug.java");
//        CompilationUnit cu = JavaParser.parse(in);
//
////             Go through all the types in the file
//            NodeList<TypeDeclaration<?>> types = cu.getTypes();
//            for (TypeDeclaration<?> type : types) {
//                // Go through all fields, methods, etc. in this type
//                NodeList<BodyDeclaration<?>> members = type.getMembers();
//                int ii = 0;
//                for (BodyDeclaration<?> member : members) {
//                    if (member instanceof MethodDeclaration) {
//                        MethodDeclaration method = (MethodDeclaration) member;
//
//                        MethodMutantData m = new MethodMutantData(method);
//                        HashSet<MethodDeclaration> j= new HashSet<>();
//                        j.add(method);
//                        m.addMutants(j);
//                        m.setClassName("mamama");
//
//                        System.out.println(method);
//                        new MutantFileIO().saveMutantData(m, Paths.get("blbl.json"));
//                        MethodMutantData ml = new MutantFileIO().loadMutantData(Paths.get("blbl.json"));
//                        ii++;
//                    }
//                }
//            }

            // visit and print the methods names
//            cu.accept(new TestVisitor(), null);

        //         prints the resulting compilation unit to default system output
//        System.out.println(cu.toString());
//         END: FOR DEBUGING


    }

    private static void outputMutantsFromFile(Path outputFolder, Path file, int from, int to) {

        MethodMutantData methodMutantData = null;
        try {
            methodMutantData = new MutantFileIO().loadMutantData(file);
        } catch (IOException e) {
            System.out.println("ERROR output: " + file.toString());
            e.printStackTrace();
            return;
        }
        //if there are no mutants in this level
        if (from > methodMutantData.getActualMutationLevel()){
            return;
        }

        String filename = file.getFileName().toString();
        String folderName = filename.substring(0, filename.length() - new String(".jsondat").length());


        //output original file
        if (from == 0) {
            Path methodFolder = Paths.get(outputFolder.toAbsolutePath().toString(), "original",
                    folderName, "src");
            methodFolder.toFile().mkdirs();
            File f = Paths.get(methodFolder.toAbsolutePath().toString(),
                    methodMutantData.getMethodName() + ".java").toFile();

            try (PrintWriter writer = new PrintWriter(f, "UTF-8")) {
                writer.println(methodMutantData.getOriginalMethod());
            } catch (FileNotFoundException e) {
                System.out.println("ERROR output: " + file.toString());
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                System.out.println("ERROR output: " + file.toString());
                e.printStackTrace();
            }

            from++;
        }

        //output mutants
        to = Math.min(methodMutantData.getActualMutationLevel(), to);
        for (Integer i = from; i <= to; i++){
            final int currentLevel = i;
            Path methodFolder = Paths.get(outputFolder.toAbsolutePath().toString(), "level" + i.toString(),
                    folderName, "src");
            methodFolder.toFile().mkdirs();
            File f = Paths.get(methodFolder.toAbsolutePath().toString(),
                    methodMutantData.getMethodName() + ".java").toFile();

            try (PrintWriter writer = new PrintWriter(f, "UTF-8")) {
                Set<MethodDeclaration> mutants = methodMutantData.getMutants().stream()
                        .filter(mutantLog -> mutantLog.getMutationLevel() == currentLevel)
                        .map(mutantLog -> mutantLog.getMutant())
                        .collect(Collectors.toSet());

                for (MethodDeclaration md : mutants) {
                    writer.println(md);
                }
            } catch (FileNotFoundException e) {
                System.out.println("ERROR output: " + file.toString());
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                System.out.println("ERROR output: " + file.toString());
                e.printStackTrace();
            }
        }
    }

    private static void outputMutantsFromDir(CommandLineValues s_CommandLineValues) {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(s_CommandLineValues.NumThreads);
        LinkedList<Callable<Void>> tasks = new LinkedList<>();
        Path outputFolder = Paths.get(s_CommandLineValues.Out);
        try {
            Files.walk(Paths.get(s_CommandLineValues.Dir)).filter(Files::isRegularFile)
                    .filter(p -> p.toString().toLowerCase().endsWith(".jsondat"))
                    .forEach(f -> {
                        tasks.add(() -> {
                            outputMutantsFromFile(outputFolder, f, s_CommandLineValues.FromLevel, s_CommandLineValues.ToLevel);
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

    private static void mutateMethodInFile(Path file, CommandLineValues config) {

        System.out.println("mutate: " + file.toString());
        // creates an input stream for the file to be parsed
        MethodMutantData methodMutantData = null;
        try {
            methodMutantData = new MutantFileIO().loadMutantData(file);
        } catch (IOException e) {
            System.out.println("ERROR mutate: " + file.toString());
            e.printStackTrace();
            return;
        }


//        //check if we cant mutate this file anymore
//        if (methodMutantData.getRequestedMutationLevel() > methodMutantData.getActualMutationLevel()){
//            return;
//        }

        MethodMutantData newMethodMutantData = generateMutationsToMethod(methodMutantData, config);

        if (newMethodMutantData == null){
            return;
        }

        //if there is no mutants - remove this file
        if (config.MutationLevel > 0 && methodMutantData.getMutants().isEmpty()){
            file.toFile().delete();
        } else {
            //write to file
            try {
                new MutantFileIO().saveMutantData(methodMutantData, file);
            } catch (IOException e) {
                System.out.println("ERROR mutate: " + file.toString());
                e.printStackTrace();
            }
        }

    }

    private static MethodMutantData generateMutationsToMethod(MethodMutantData methodMutantData,
                                                              CommandLineValues config) {

        int oldRequestedMutationLevel = methodMutantData.getRequestedMutationLevel();
        //check if we already developed to this level
        if (oldRequestedMutationLevel >= config.MutationLevel){
            return null;
        } else {
            methodMutantData.setRequestedMutationLevel(config.MutationLevel);
        }

        //check if we cant mutate this file anymore
        if (oldRequestedMutationLevel > methodMutantData.getActualMutationLevel()){
            return methodMutantData;
        }


        final int startFormLevel = methodMutantData.getActualMutationLevel();
        //create set of existing mutants
        Set<MethodDeclaration> existingMethods =
                methodMutantData.getMutants().stream().map(MutantLog::getMutant)
//                        .map(MethodDeclaration::toString)
                        .collect(Collectors.toSet());
        existingMethods.add(methodMutantData.getOriginalMethod());

        //get the last level
        Set<MutantLog> lastLevelMethods;
        if (startFormLevel == 0){
            lastLevelMethods = new HashSet<MutantLog>();
            lastLevelMethods.add(new MutantLog(methodMutantData.getOriginalMethod()));
        } else {
            lastLevelMethods = methodMutantData.getMutants().stream()
                    .filter(mutantLog -> mutantLog.getMutationLevel() == startFormLevel)
                    .collect(Collectors.toSet());
        }

        for (int i=startFormLevel + 1; i <= config.MutationLevel; i++) {
            Set<MutantLog> newMutants =
                    lastLevelMethods.stream()
                            .flatMap(mutantLog -> new MethodMutationsGenerator(config)
                                    .generateMutants(mutantLog, existingMethods).stream())
                            .collect(Collectors.toSet());

            if (newMutants.isEmpty()) {
                break;
            } else {
                methodMutantData.setActualMutationLevel(i);
            }

            methodMutantData.addMutantToLog(newMutants);
            lastLevelMethods = newMutants;
        }

        return methodMutantData;

    }

    private static void mutateMethodInDir(CommandLineValues s_CommandLineValues) {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(s_CommandLineValues.NumThreads);
        LinkedList<Callable<Void>> tasks = new LinkedList<>();
        Path outputFolder = Paths.get(s_CommandLineValues.Out);
        try {
            Files.walk(Paths.get(s_CommandLineValues.Dir)).filter(Files::isRegularFile)
                    .filter(p -> p.toString().toLowerCase().endsWith(".jsondat"))
                    .forEach(f -> {
                        tasks.add(() -> {
                            mutateMethodInFile(f, s_CommandLineValues);
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

    private static void extractMethodsInFile(Path outputFolder, Path file){
        // creates an input stream for the file to be parsed
        FileInputStream in = null;
        try {
            in = new FileInputStream(file.toFile());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        // parse the file
        CompilationUnit cu = JavaParser.parse(in);

        Set<MethodDeclaration> methods = new HashSet<>();
        // collect all methods
        cu.accept(new VoidVisitorAdapter<Set<MethodDeclaration>>() {
            @Override
            public void visit(MethodDeclaration n, Set<MethodDeclaration> arg) {
                arg.add(n);
                super.visit(n, arg);
            }
        }, methods);

        //output to files
        for (MethodDeclaration m : methods){
            MethodMutantData methodMutantData = new MethodMutantData(m);

            methodMutantData.setClassName(file.toFile().getName());
            System.out.println(methodMutantData.getMethodClassName());

            //write to file
            Path methodFile = generateUniqueName(outputFolder, methodMutantData.getMethodClassName());
//            methodFile.toFile().mkdirs();

            try {
                new MutantFileIO().saveMutantData(methodMutantData, methodFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private static void extractMethodsInDir(CommandLineValues s_CommandLineValues) {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(s_CommandLineValues.NumThreads);
        LinkedList<Callable<Void>> tasks = new LinkedList<>();
        Path outputFolder = Paths.get(s_CommandLineValues.Out);
        try {
            Files.walk(Paths.get(s_CommandLineValues.Dir)).filter(Files::isRegularFile)
                    .filter(p -> p.toString().toLowerCase().endsWith(".java"))
                    .forEach(f -> {
                        tasks.add(() -> {
                            extractMethodsInFile(outputFolder, f);
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
        Path methodFolder = Paths.get(outputFolder.toAbsolutePath().toString(), originalName.toLowerCase() + ".jsondat");
        while (methodFolder.toFile().exists()) {
            i++;
            methodFolder = Paths.get(outputFolder.toAbsolutePath().toString(), originalName.toLowerCase() + "__" + i + ".jsondat");
        }
        return methodFolder;
    }


//    //output to files
//        for (MethodMutantData m : methodMutants){
//        m.setClassName(file.getName());
//        System.out.println(m.getMethodClassName() + "\t" + m.getMutants().size());
//
//        if (!m.getMutants().isEmpty()) {
//            //create dir
//            Path methodFolder = generateUniqueFolderName(outputFolder, m.getMethodClassName());
//            methodFolder.toFile().mkdirs();
//
//            PrintWriter writer = null;
//            try {
//                //create original file
//                File originalMethodFile = Paths.get(methodFolder.toAbsolutePath().toString(), m.getMethodName() + ".java").toFile();
//                writer = new PrintWriter(originalMethodFile, "UTF-8");
//                writer.println(m.getOriginalMethod());
//                writer.close();
//
//                //create mutant file
//                File mutantMethodFile = Paths.get(methodFolder.toAbsolutePath().toString(), m.getMethodName() + "_mutants.java").toFile();
//                writer = new PrintWriter(mutantMethodFile, "UTF-8");
//                for (MethodDeclaration md : m.getMutants()) {
//                    writer.println(md);
//                }
//                writer.close();
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            } finally {
//                if (writer != null) writer.close();
//            }
//        }
    /**
     * Simple visitor implementation for visiting MethodDeclaration nodes.
     */
//    private static class TestVisitor extends VoidVisitorAdapter<Void> {
////        @Override
////        public void visit(NameExpr n, Void arg) {
////            /* here you can access the attributes of the method.
////             this method will be called for all methods in this
////             CompilationUnit, including inner class methods */
////            if (n.toString().equals("n")) {
////                System.out.println(n + "--" + n.getParentNode().get() + "--" + n.getParentNode().get().getParentNode().get());
////            }
////
////            super.visit(n, arg);
////        }
//        @Override
//        public void visit(VariableDeclarationExpr n, Void arg)
//        {
//            List <VariableDeclarator> myVars = n.getVariables();
//            for (VariableDeclarator vars: myVars){
//                System.out.println("Variable Name: "+vars.getName());
//            }
//        }
//
//        @Override
//        public void visit(Parameter n, Void arg)
//        {
////            List <VariableDeclarator> myVars = n.getVariables();
////            for (VariableDeclarator vars: myVars){
//                System.out.println("Param Name: "+n.getName());
////            }
//        }
//    }
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
