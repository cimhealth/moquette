package io.moquette.interception.messages;

/**
 * 监听断开连接
 * @author Scott Lee
 */
public class InterceptLostMessage {
    private final String clientID;

    public InterceptLostMessage(String clientID) {
        this.clientID = clientID;
    }

    public String getClientID() {
        return clientID;
    }
}
