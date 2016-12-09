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
            new RmsServer().start("localhost", "8999", "8090");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RmsServer() {
    }

    private static RmsServer server;

    public void start(String host, String port, String wsport) throws Exception {
        Properties cfg = new Properties();
        cfg.put(BrokerConstants.HOST_PROPERTY_NAME, host);
        cfg.put(BrokerConstants.PORT_PROPERTY_NAME, port);
        cfg.put(BrokerConstants.WEB_SOCKET_PORT_PROPERTY_NAME, wsport);
//        MemoryConfig config = new MemoryConfig(cfg);
        URL str = new ClassPathResource("config/moquette_redis.properties").getURL();
        System.out.println(str);
        logger.info("config root:{}", str);
        FileConfig config = new FileConfig(str);
        server = instance();
        server.startServer(config, Arrays.asList(new AbstractInterceptHandler() {
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
                server.stopServer();
            }
        });
    }

    static private synchronized RmsServer instance() throws Exception {
        if (server != null) {
            throw new Exception("too many RmsServer");
        }
        return server = new RmsServer();
    }

}