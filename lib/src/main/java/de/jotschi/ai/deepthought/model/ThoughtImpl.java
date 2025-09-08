package de.jotschi.ai.deepthought.model;

/**
 * The element of a ToT
 */
public class ThoughtImpl extends AbstractThought {

    public ThoughtImpl(String query) {
        super(query, null);
    }

    public ThoughtImpl(String query, String context) {
        super(query, context);
    }

}
