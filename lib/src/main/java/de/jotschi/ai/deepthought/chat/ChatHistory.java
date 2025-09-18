package de.jotschi.ai.deepthought.chat;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;

import de.jotschi.ai.deepthought.chat.impl.ChatMessageImpl;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ChatHistory {

    private String prefix;

    /**
     * Create a new history
     * 
     * @param prefix Prefix used to name the save file.
     */
    public ChatHistory(String prefix) {
        this.prefix = prefix;
    }

    public void add(ChatMessage msg) {
        List<ChatMessage> history = load();
        history.add(msg);
        save(history);
    }

    public void clear() {
        save(Collections.emptyList());
    }

    public void save(List<ChatMessage> history) {
        try {
            File file = getHistorySaveFile();
            JsonObject json = new JsonObject();
            JsonArray msgsData = new JsonArray();
            for (ChatMessage msg : history) {
                JsonObject data = new JsonObject()
                        .put("type", msg.type())
                        .put("temp", msg.temperature())
                        .put("text", msg.message());
                msgsData.add(data);
            }
            json.put("msgs", msgsData);
            FileUtils.writeStringToFile(file, json.encodePrettily(), Charset.defaultCharset());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<ChatMessage> load() {
        try {
            List<ChatMessage> history = new ArrayList<>();

            File file = getHistorySaveFile();
            if (!file.exists()) {
                System.err.println("Save " + file + " not found. Skipping..");
                return history;
            }
            String json = FileUtils.readFileToString(file, Charset.defaultCharset());
            JsonObject saveData = new JsonObject(json);
            JsonArray msgs = saveData.getJsonArray("msgs");
            if (msgs != null) {
                for (int i = 0; i < msgs.size(); i++) {
                    JsonObject msg = msgs.getJsonObject(i);
                    ChatMessageType msgType = ChatMessageType.valueOf(msg.getString("type"));
                    String msgText = msg.getString("text");
                    history.add(new ChatMessageImpl(msgType, msgText));
                }
            }
            return history;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private File getHistorySaveFile() throws IOException {
        return new File(getSaveFolder(), "history.json");
    }

    private File getSaveFolder() throws IOException {
        File saveFolder = new File("saves", prefix);
        if (!saveFolder.exists()) {
            FileUtils.forceMkdir(saveFolder);
        }
        return saveFolder;
    }
}
