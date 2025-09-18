package de.jotschi.ai.deepthought.chat;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.jotschi.ai.deepthought.chat.impl.ChatMessageImpl;

public class ChatHistoryTest {

    @Test
    public void testHistory() throws IOException {
        ChatHistory history = new ChatHistory("test");
        history.clear();
        ChatMessage dummy = new ChatMessageImpl(ChatMessageType.USER, "Hello");
        history.add(dummy);
        List<ChatMessage> msgs= history.load();
        assertEquals(1, msgs.size());
    }
}
