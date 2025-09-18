package de.jotschi.ai.deepthought.chat.impl;

import de.jotschi.ai.deepthought.chat.ChatMessage;
import de.jotschi.ai.deepthought.chat.ChatMessageType;

public class ChatMessageImpl implements ChatMessage {

    private String message;

    private float temperature;

    private ChatMessageType type;

    public ChatMessageImpl(ChatMessageType type, String message) {
        this.type = type;
        this.message = message;
    }

    @Override
    public ChatMessageType type() {
        return type;
    }

    @Override
    public float temperature() {
        return temperature;
    }

    @Override
    public String message() {
        return message;
    }

}
