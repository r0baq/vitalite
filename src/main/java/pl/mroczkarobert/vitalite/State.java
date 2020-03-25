package pl.mroczkarobert.vitalite;

import java.util.HashSet;
import java.util.Set;

public class State {
    public final Kind kind;
    public final Set<String> processed = new HashSet<>();
    public boolean anyChange = false;

    public State(Kind kind) {
        this.kind = kind;
    }
}
