package de.jotschi.ai.deepthought.chat;

public interface ChatMessage {

    ChatMessageType type();

    float temperature();

    String message();

}
