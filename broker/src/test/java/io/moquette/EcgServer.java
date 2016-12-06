package io.moquette;

import io.moquette.interception.AbstractInterceptHandler;
import io.moquette.interception.messages.InterceptPublishMessage;
import io.moquette.proto.messages.AbstractMessage;
import io.moquette.proto.messages.PublishMessage;
import io.moquette.server.Server;
import io.moquette.server.config.FileConfig;
import io.moquette.server.config.FilesystemConfig;
import io.moquette.server.config.MemoryConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Properties;

public class EcgServer extends Server {

    private static final Logger logger = LoggerFactory.getLogger(EcgServer.class);

    public static void main(String[] args) {
        try {
            new EcgServer().start("localhost", "8999", "8090");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init() throws Exception {
//        new EcgServer().start("localhost", "9988","8090");
        new EcgServer().start("rdp.dev.cim120.cn", "8999", "8090");
    }

    private EcgServer() {
    }

    private static EcgServer server;

    public void start(String host, String port, String wsport) throws Exception {
        Properties cfg = new Properties();
        cfg.put(BrokerConstants.HOST_PROPERTY_NAME, host);
        cfg.put(BrokerConstants.PORT_PROPERTY_NAME, port);
        cfg.put(BrokerConstants.WEB_SOCKET_PORT_PROPERTY_NAME, wsport);
//        MemoryConfig config = new MemoryConfig(cfg);
        String str = this.getClass().getResource("/").getFile();
        File file = new File(str, "/config/moquette_redis.properties");
        FileConfig config = new FileConfig(file);
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

    static private synchronized EcgServer instance() throws Exception {
        if (server != null) {
            throw new Exception("too many EcgServer");
        }
        return server = new EcgServer();
    }

}