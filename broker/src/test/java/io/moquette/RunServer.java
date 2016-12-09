package io.moquette;

import io.moquette.proto.messages.AbstractMessage;
import io.moquette.server.RmsServer;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class RunServer {
    public static void main(String[] args) {
        try {
            new RmsServer().start("localhost", "8999", "8090");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}