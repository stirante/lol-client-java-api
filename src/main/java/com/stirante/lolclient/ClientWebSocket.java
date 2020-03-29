package com.stirante.lolclient;

import com.google.gson.*;
import com.stirante.lolclient.utils.SSLUtil;
import generated.UriMap;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import javax.net.ssl.SSLSocketFactory;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ClientWebSocket extends WebSocketClient {

    private SocketListener socketListener;
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Event.class, new EventDeserializer())
            .registerTypeAdapter(Message.class, new MessageDeserializer())
            .create();
    private static final HashMap<Pattern, Class> patterns = new HashMap<>();

    static {
        for (String s : UriMap.toClass.keySet()) {
            patterns.put(Pattern.compile(s), UriMap.toClass.get(s));
        }
    }

    ClientWebSocket(String token, int port) throws Exception {
        super(new URI("wss://localhost:" + port + "/"), createHeaders("Authorization", "Basic " + token));

        SSLSocketFactory factory = SSLUtil.getSocketFactory();
        setSocket(factory.createSocket());
        connectBlocking();
        subscribe("OnJsonApiEvent");
    }

    private static Map<String, String> createHeaders(String... headers) {
        if (headers.length % 2 != 0) {
            throw new IllegalArgumentException("Invalid amount of parameters!");
        }
        HashMap<String, String> map = new HashMap<>();
        for (int i = 0; i < headers.length; i += 2) {
            String key = headers[i];
            String value = headers[i + 1];
            map.put(key, value);
        }
        return map;
    }

    public void subscribe(String event) {
        sendMessage(new Message(MessageType.SUBSCRIBE, event, null));
    }

    public void unsubscribe(String event) {
        sendMessage(new Message(MessageType.UNSUBSCRIBE, event, null));
    }

    public void onOpen(ServerHandshake handshakedata) {
    }

    public void onClose(int code, String reason, boolean remote) {
        if (socketListener != null) {
            socketListener.onClose(code, reason);
        }
    }

    public void onMessage(String message) {
        if (message.isEmpty()) {
            return;
        }
        Message mess = GSON.fromJson(message, Message.class);
        if (mess == null) {
            return;
        }
        if (mess.type == MessageType.EVENT && socketListener != null) {
            socketListener.onEvent(mess.event);
        }
    }

    public void setSocketListener(SocketListener socketListener) {
        this.socketListener = socketListener;
    }

    public void sendMessage(Message message) {
        String text = GSON.toJson(message);
        send(text);
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    public interface SocketListener {

        void onEvent(Event event);

        void onClose(int code, String reason);
    }

    public enum MessageType {
        WELCOME(0),
        PREFIX(1),
        CALL(2),
        CALLRESULT(3),
        CALLERROR(4),
        SUBSCRIBE(5),
        UNSUBSCRIBE(6),
        PUBLISH(7),
        EVENT(8);

        private static final HashMap<Integer, MessageType> fromId = new HashMap<>();

        static {
            for (MessageType type : values()) {
                fromId.put(type.id, type);
            }
        }

        private final int id;

        MessageType(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static MessageType getById(int id) {
            return fromId.get(id);
        }

    }

    public static class Message {
        private final MessageType type;
        private final String source;
        private final Event event;

        public Message(MessageType type, String source, Event event) {
            this.type = type;
            this.source = source;
            this.event = event;
        }

        public MessageType getType() {
            return type;
        }

        public String getSource() {
            return source;
        }

        public Event getEvent() {
            return event;
        }

        @Override
        public String toString() {
            return "Message{" +
                    "type=" + type +
                    ", source='" + source + '\'' +
                    ", event=" + event +
                    '}';
        }
    }

    public static class Event {
        private final Object data;
        private final String eventType;
        private final String uri;

        private Event(Object data, String eventType, String uri) {
            this.data = data;
            this.eventType = eventType;
            this.uri = uri;
        }

        public Object getData() {
            return data;
        }

        public String getEventType() {
            return eventType;
        }

        public String getUri() {
            return uri;
        }

        @Override
        public String toString() {
            return "Event{" +
                    "eventType='" + eventType + '\'' +
                    ", uri='" + uri + '\'' +
                    ", data=" + data +
                    '}';
        }
    }

    public static class EventDeserializer implements JsonDeserializer<Event> {

        @Override
        public Event deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jObject = (JsonObject) json;
            String uri = jObject.get("uri").getAsString();
            String eventType = jObject.get("eventType").getAsString();
            Object data = jObject.get("data");
            Class c = UriMap.toClass.get(uri);
            if (c == null) {
                for (Pattern pattern : patterns.keySet()) {
                    if (pattern.matcher(uri).matches()) {
                        c = patterns.get(pattern);
                    }
                }
            }
            if (c == null) {
                data = context.deserialize((JsonElement) data, Object.class);
            }
            else {
                try {
                    data = context.deserialize((JsonElement) data, c);
                } catch (JsonSyntaxException e) {
                    //TODO: I think it should be reported a bit better, but don't really have an idea for that
                    System.err.println("Failed to deserialize from URI " + uri);
                    return new Event(data, eventType, uri);
                }
            }
            return new Event(data, eventType, uri);
        }
    }

    public static class MessageDeserializer implements JsonDeserializer<Message>, JsonSerializer<Message> {

        @Override
        public Message deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (!(json instanceof JsonArray)) {
                return null;
            }
            JsonArray jArr = (JsonArray) json;
            MessageType type = MessageType.getById(jArr.get(0).getAsInt());
            String source = jArr.get(1).getAsString();
            Event event = null;
            if (type == MessageType.EVENT) {
                event = context.deserialize(jArr.get(2), Event.class);
            }
            return new Message(type, source, event);
        }

        @Override
        public JsonElement serialize(Message message, Type type, JsonSerializationContext context) {
            JsonArray result = new JsonArray();
            result.add(message.type.id);
            result.add(message.source);
            if (message.event != null) {
                result.add(context.serialize(message.event));
            }
            return result;
        }
    }

}
