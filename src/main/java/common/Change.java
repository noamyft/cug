package common;

import com.github.javaparser.Range;
import mutators.AMutator;

public class Change {
    Range range;
//    AMutator mutator;
    String from;
    String to;

    public Change(Range range, String from, String to) {
        this.range = range;
//        this.mutator = mutator;
        this.from = from;
        this.to = to;
    }
}
