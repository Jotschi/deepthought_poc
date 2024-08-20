package de.jotschi.ai.deepthought.llm;

public enum LLM {

    OLLAMA_MIXTRAL_8X22_Q8("mixtral:8x22b-instruct-v0.1-q8_0"),

    OLLAMA_LLAMA31_70B_INST_Q4_K_S("llama3.1:70b-instruct-q4_K_S"),

    OLLAMA_LLAMA31_8B_INST_Q8("llama3.1:8b-instruct-q8_0"),

    OLLAMA_MISTRAL_7B_INST_FP16("mistral:7b-instruct-fp16"),

    OLLAMA_MISTRAL_7B_INST_V03_FP16("mistral:7b-instruct-v0.3-fp16");

    /**
     * Model key that is being used when preparing the API request.
     */
    private String key;
    private String primaryHost;

    private LLM(String key, String primaryHost) {
        this.key = key;
        this.primaryHost = primaryHost;
    }

    private LLM(String key) {
        this.key = key;
    }

    public String key() {
        return key;
    }

    public String primaryHost() {
        return primaryHost;
    }

}
