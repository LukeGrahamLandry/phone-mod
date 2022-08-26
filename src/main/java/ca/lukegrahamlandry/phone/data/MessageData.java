package ca.lukegrahamlandry.phone.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageData {
    public final int phoneId;
    public final String message;

    public MessageData(int phoneId, String message) {
        this.phoneId = phoneId;
        this.message = message;
    }

    public static Map<String, List<MessageData>> clientMessages = new HashMap<>();
}
