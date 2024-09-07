package de.jotschi.ai.deepthought.model;

import org.junit.jupiter.api.Test;

public class ThoughtTreeTest {

    @Test
    public void testTot() {
        Thought t = Thought.of("What is 1+1+1?");
        Thought t2 = Thought.of("Calculate 1+1.");
        Thought t3 = Thought.of("Add 1 to the result.");
        t2.add(t3);
        t.add(t2);

        System.out.println(t.toString());
    }
}
