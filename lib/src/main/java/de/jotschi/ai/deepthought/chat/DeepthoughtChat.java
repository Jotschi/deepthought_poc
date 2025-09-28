package de.jotschi.ai.deepthought.chat;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.jotschi.ai.deepthought.chat.impl.ChatMessageImpl;
import de.jotschi.ai.deepthought.llm.LLM;
import de.jotschi.ai.deepthought.util.ChatUtils;
import io.metaloom.ai.genai.llm.LLMContext;
import io.metaloom.ai.genai.llm.ollama.OllamaLLMProvider;

@Singleton
public class DeepthoughtChat {

    private static final int TOKEN_LIMIT = 800;
    private OllamaLLMProvider provider;

    @Inject
    public DeepthoughtChat(OllamaLLMProvider provider) {
        this.provider = provider;
    }

    public void run() {

        // TODO configure prefix
        ChatHistory history = new ChatHistory("dummy");
        history.clear();
        while (true) {
            String input = ChatUtils.query();
            //System.out.println("Input: " + input);
            history.add(new ChatMessageImpl(ChatMessageType.USER, input));
            ChatMessage answer = answer(history);
            System.out.println("Assistant: " + answer.message());
            history.add(answer);
        }
    }

    private ChatMessage answer(ChatHistory history) {
        List<io.metaloom.ai.genai.llm.ChatMessage> msgHistory = ChatUtils.toLLMHistory(history.load(), TOKEN_LIMIT);
        List<io.metaloom.ai.genai.llm.ChatMessage> activeHistory = new ArrayList<>();
        // activeHistory.add(thinkControl);
        // activeHistory.addAll(prefixHistory(prompt));
        activeHistory.addAll(msgHistory);

        LLMContext ctx = LLMContext.ctx(activeHistory, LLM.OLLAMA_MISTRAL_SMALL_32_24B_Q8, null);

        String output = provider.generate(ctx);

        return new ChatMessageImpl(ChatMessageType.ASSISTANT, output);
    }

}
