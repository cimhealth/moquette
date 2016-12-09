package io.moquette;

import io.moquette.interception.AbstractInterceptHandler;
import io.moquette.interception.messages.InterceptPublishMessage;
import io.moquette.proto.messages.AbstractMessage;
import io.moquette.server.RmsServer;
import io.moquette.server.Server;
import io.moquette.server.config.FileConfig;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Arrays;

public class RunServer extends Server {
    private static final Logger logger = LoggerFactory.getLogger(RmsServer.class);

    public static void main(String[] args) {
        try {
            new RunServer().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() throws Exception {
        URL str = new ClassPathResource("config/moquette_redis.properties").getURL();
        FileConfig config = new FileConfig(str);
        this.startServer(config, Arrays.asList(new AbstractInterceptHandler() {
            @Override
            public void onPublish(InterceptPublishMessage msg) {
//                System.out.println(msg.getTopicName());
                if ("S_TEST_BYTE".equalsIgnoreCase(msg.getTopicName())) {
                    try {
                        String str = new String(msg.getPayload().array(), "utf-8");
                        logger.info("Qos=[{}] client=[{}] msg=[{}]", msg.getQos(), msg.getClientID(), str);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        }));
//        server.startServer(config);
        System.out.println("Server started, version 0.8");
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                stopServer();
            }
        });
    }

}