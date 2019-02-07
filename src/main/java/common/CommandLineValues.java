package common;

import java.io.File;
import java.nio.file.Path;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

/**
 * This class handles the programs arguments.
 */
public class CommandLineValues {
    @Option(name = "--extract_methods", required = false, forbids={"--output_mutants", "--mutate_methods","--mutation_level", "--from", "--to"})
    public boolean ExtractMethods = false;

    @Option(name = "--mutate_methods", required = false, forbids={"--output_mutants", "--extract_methods", "--from", "--to", "--output"})
    public boolean MutateMethods = false;

    @Option(name = "--output_mutants", required = false, forbids={"--mutate_methods", "--extract_methods", "--mutation_level"})
    public boolean OutputMethods = false;

    @Option(name = "--file", required = false)
    public String File = null;

    @Option(name = "--dir", required = false, forbids = "--file")
    public String Dir = null;

    @Option(name = "--output", required = false)
    public String Out = "";

    @Option(name = "--mutation_level", required = false)
    public Integer MutationLevel = 1;

    @Option(name = "--mutation_count", required = false)
    public Integer MutationCount = 1;

//    @Option(name = "--no_hash", required = false)
//    public boolean NoHash = false;
//
    @Option(name = "--num_threads", required = false)
    public int NumThreads = 32;

    @Option(name = "--from", required = false)
    public int FromLevel = 0;

    @Option(name = "--to", required = false)
    public int ToLevel = Integer.MAX_VALUE;

//    @Option(name = "--max_mutants_per_mutator", required = false)
//    public int MaxMutantsPerMutator = Integer.MAX_VALUE;

//    @Option(name = "--max_dev_mut_per_iteration", required = false)
//    public int MaxDevelopedMutantsPerIteration = Integer.MAX_VALUE;

//    @Option(name = "--mutation_rate", required = false)
//    public double MutationRate = 1;

//
//    @Option(name = "--min_code_len", required = false)
//    public int MinCodeLength = 1;
//
//    @Option(name = "--max_code_len", required = false)
//    public int MaxCodeLength = 10000;
//
//    @Option(name = "--pretty_print", required = false)
//    public boolean PrettyPrint = false;
//


    public CommandLineValues(String... args) throws CmdLineException {
        CmdLineParser parser = new CmdLineParser(this);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
            throw e;
        }
    }

    public CommandLineValues() {

    }
}