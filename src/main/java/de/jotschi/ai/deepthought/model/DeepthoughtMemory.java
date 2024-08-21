package de.jotschi.ai.deepthought.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.jotschi.ai.deepthought.Deepthought;

public class DeepthoughtMemory {

    private Deepthought dt;

    private List<DeepthoughtMemoryEntry> entries = new ArrayList<>();
    private String query;
    private boolean evaluated = false;

    public DeepthoughtMemory(Deepthought dt, String query) {
        this.dt = dt;
        this.query = query;
    }

    /**
     * Iterate over all entries and decompose them.
     * 
     * @throws IOException
     */
    public void decompose() throws IOException {
        if (evaluated) {
            throw new RuntimeException("The memory can only be evaluated one time");
        }
        int id = 1;
        for (DeepthoughtMemoryEntry entry : entries) {

            // Decompose with context
            if (entry.steps() == null) {
                
                // 1. Decompose the entry into steps
                DeepthoughtMemoryEntry processedEntry = dt.decompose(entry.query(), entry.context());
                entry.setSteps(processedEntry.steps());

                // 2. Evaluate each step
                for (DecompositionStep step : processedEntry.steps()) {
                    String result = dt.evalStep(id++, step, entry.context());
                    step.setResult(result);
                }
            }
//            } else {
//                // Now decompose the query with use of the initial context
//                for (DecompositionStep step : entry.steps()) {
//                    dt.decompose(step);
//                }
//            }

        }
        evaluated = true;
    }

    public List<DeepthoughtMemoryEntry> entries() {
        return entries;
    }

    public String query() {
        return query;
    }

}
