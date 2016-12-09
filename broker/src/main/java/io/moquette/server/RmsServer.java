package io.moquette.server;

import io.moquette.BrokerConstants;
import io.moquette.interception.AbstractInterceptHandler;
import io.moquette.interception.messages.InterceptPublishMessage;
import io.moquette.server.config.FileConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Arrays;
import java.util.Properties;

public class RmsServer extends Server {

    private static final Logger logger = LoggerFactory.getLogger(RmsServer.class);

    public static void main(String[] args) {
        try {
            new RmsServer().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private RmsServer() {
    }

    public void start() throws Exception {
        URL str = new ClassPathResource("config/moquette_redis.properties").getURL();
        System.out.println(str);
        logger.info("config file:{}", str);
        FileConfig config = new FileConfig(str);
        this.startServer(config);
        System.out.println("Server started, version 0.8");
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                stopServer();
            }
        });
    }

}