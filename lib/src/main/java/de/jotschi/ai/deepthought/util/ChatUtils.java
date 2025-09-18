package de.jotschi.ai.deepthought.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.model.openai.OpenAiTokenCountEstimator;
import io.metaloom.ai.genai.llm.ChatMessage;

public class ChatUtils {

    private static OpenAiTokenCountEstimator TOKENIZER = new OpenAiTokenCountEstimator(OpenAiChatModelName.GPT_4_O);

    public static String query() {
        String input = "";
        while (true) {
            input = readInput();
            if (!input.trim().isBlank()) {
                return input;
            }
        }
    }

    private static String readInput() {
        Scanner in = new Scanner(System.in);
        System.out.println("\nEnter: ");
        return in.nextLine();
    }

    public static List<ChatMessage> toLLMHistory(List<de.jotschi.ai.deepthought.chat.ChatMessage> storyHistory, int tokenLimit) {
        List<ChatMessage> msgs = new ArrayList<>();

        boolean isUser = true;
        for (var msg : storyHistory.reversed()) {
            if (isUser) {
                msgs.add(ChatMessage.user(msg.message().trim()));
            } else {
                msgs.add(ChatMessage.assistant(msg.message().trim()));
            }
            isUser = !isUser;
        }

        List<ChatMessage> reducedMessages = new ArrayList<>();
        List<ChatMessage> remainingMessages = new ArrayList<>();

        long totalTokens = 0;
        for (ChatMessage msg : msgs) {
            if (totalTokens < tokenLimit) {
                totalTokens += TOKENIZER.estimateTokenCountInText(msg.getText());
                reducedMessages.add(msg);
            } else {
                remainingMessages.add(msg);
            }
        }

        System.out.println(reducedMessages.size());
        // Cap to the limit
        // return msgs.stream().limit(limit).toList().reversed();
        return reducedMessages.reversed();
    }
}
