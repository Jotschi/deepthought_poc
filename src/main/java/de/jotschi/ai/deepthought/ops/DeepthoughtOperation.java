package de.jotschi.ai.deepthought.ops;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import de.jotschi.ai.deepthought.model.Thought;

public interface DeepthoughtOperation {

    void process(Thought thought) throws JsonMappingException, JsonProcessingException;

}
