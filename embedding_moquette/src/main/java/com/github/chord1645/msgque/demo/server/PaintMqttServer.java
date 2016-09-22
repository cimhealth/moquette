package com.github.chord1645.msgque.demo.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.chord1645.msgque.demo.Topics;
import com.github.chord1645.msgque.demo.model.BaseData;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import io.moquette.BrokerConstants;
import io.moquette.interception.AbstractInterceptHandler;
import io.moquette.interception.InterceptHandler;
import io.moquette.interception.messages.InterceptDisconnectMessage;
import io.moquette.interception.messages.InterceptLostMessage;
import io.moquette.interception.messages.InterceptPublishMessage;
import io.moquette.interception.messages.InterceptUnsubscribeMessage;
import io.moquette.proto.messages.AbstractMessage;
import io.moquette.proto.messages.PublishMessage;
import io.moquette.server.Server;
import io.moquette.server.config.FilesystemConfig;
import io.moquette.server.config.IConfig;
import io.moquette.server.config.MemoryConfig;
import org.msgpack.MessagePack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

public class PaintMqttServer extends Server {

    private static final Logger logger = LoggerFactory.getLogger(PaintMqttServer.class);
    //    Map<String, List<String>> room = new HashMap<>();
    static Multimap<String, String> room = ArrayListMultimap.create();

    public static void main(String[] args) throws IOException {
        String keystore="D:\\Work\\workspace\\moquette1645\\broker\\src\\test\\resources\\ows.jks";
        Properties cfg = new Properties();
        cfg.put(BrokerConstants.HOST_PROPERTY_NAME, "localhost");
        cfg.put(BrokerConstants.PORT_PROPERTY_NAME, "9999");

        cfg.put(BrokerConstants.SSL_PORT_PROPERTY_NAME, "8883");
        cfg.put(BrokerConstants.JKS_PATH_PROPERTY_NAME, keystore);
        cfg.put(BrokerConstants.KEY_STORE_PASSWORD_PROPERTY_NAME, "123123");
        cfg.put(BrokerConstants.KEY_MANAGER_PASSWORD_PROPERTY_NAME, "123123");
        final PaintMqttServer server = new PaintMqttServer();
//        final Properties configProps = IntegrationUtils.prepareTestPropeties();
        class JoinListener extends AbstractInterceptHandler {
            PaintMqttServer server;
            MessagePack messagePack = new MessagePack();

            public JoinListener(PaintMqttServer server) {
                this.server = server;
            }


            public void onPublish(InterceptPublishMessage msg) {
                logger.info("pubilsh=========================================================");
                if (Topics.S_JOIN.equalsIgnoreCase(msg.getTopicName())) {
                    try {
                        String json = new String(msg.getPayload().array(), "utf-8");
                        JSONObject obj = JSON.parseObject(json);
//                        room.put(obj.getString("room"), msg.getClientID());
                        String roomId = obj.getString("room");
//                        String player = obj.getString("player");
                        room.put(roomId, msg.getClientID());
                        logger.info("pubilsh [{}] user [{}] join room [{}] players:[{}]", msg.getQos(), msg.getClientID(), roomId, room);
                        PublishMessage message = new PublishMessage();
                        message.setTopicName(Topics.room(roomId));
                        message.setQos(AbstractMessage.QOSType.LEAST_ONE);
                        ArrayList<String> players = new ArrayList<>(room.get(roomId));
                        byte[] bt = messagePack.write(new BaseData(players));
                        message.setPayload(ByteBuffer.wrap(bt));
//                        message.setPayload(ByteBuffer.wrap(JSON.toJSONString(room.get(roomId)).getBytes("utf-8")));
                        server.internalPublish(message);
                        logger.info("internalPublish:" + Topics.room(roomId));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
        class QuitListener extends AbstractInterceptHandler {
            PaintMqttServer server;
            MessagePack messagePack = new MessagePack();

            public QuitListener(PaintMqttServer server) {
                this.server = server;
            }

            @Override
            public void onLost(InterceptLostMessage msg) {
                try {
                    logger.info("onLost:" + msg.getClientID());
                    super.onLost(msg);
                    Map<String, Collection<String>> map = room.asMap();
                    for(Map.Entry<String,Collection<String>> e:map.entrySet()){
                        if (e.getValue().contains(msg.getClientID())){
                            e.getValue().remove(msg.getClientID());
                            PublishMessage message = new PublishMessage();
                            message.setTopicName(Topics.room(e.getKey()));
                            message.setQos(AbstractMessage.QOSType.LEAST_ONE);
                            ArrayList<String> players = new ArrayList<>(room.get(e.getKey()));
                            byte[] bt = messagePack.write(new BaseData(players));
                            message.setPayload(ByteBuffer.wrap(bt));
                            server.internalPublish(message);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onUnsubscribe(InterceptUnsubscribeMessage msg) {
                logger.info("onUnsubscribe:" + msg.getTopicFilter());
                if (msg.getTopicFilter().startsWith(Topics.S_JOIN)) {
                    try {
                        String roomId = Topics.roomId(msg.getTopicFilter());
                        room.get(roomId).remove(msg.getClientID());
                        PublishMessage message = new PublishMessage();
                        message.setTopicName(Topics.room(roomId));
                        message.setQos(AbstractMessage.QOSType.LEAST_ONE);
                        ArrayList<String> players = new ArrayList<>(room.get(roomId));
                        byte[] bt = messagePack.write(new BaseData(players));
                        message.setPayload(ByteBuffer.wrap(bt));
//                        message.setPayload(ByteBuffer.wrap(JSON.toJSONString(room.get(roomId)).getBytes("utf-8")));
                        server.internalPublish(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        class AnswerListener extends AbstractInterceptHandler {
            @Override
            public void onPublish(InterceptPublishMessage msg) {
                if (Topics.S_ANSWER.equalsIgnoreCase(msg.getTopicName())) {
                }
            }
        }
        class ReadyListener extends AbstractInterceptHandler {
            @Override
            public void onPublish(InterceptPublishMessage msg) {
                if (Topics.S_READY.equalsIgnoreCase(msg.getTopicName())) {
                }
            }
        }
//        FilesystemConfig config = new FilesystemConfig(new File("D:\\Work\\workspace\\msgque-demo\\database\\db1.db"));
        MemoryConfig config = new MemoryConfig(cfg);
        server.startServer(config, Lists.newArrayList(new JoinListener(server), new QuitListener(server)));
//        server.startServer(new MemoryConfig(cfg));
        System.out.println("Server started, version 0.8");
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                server.stopServer();
            }
        });

    }


}