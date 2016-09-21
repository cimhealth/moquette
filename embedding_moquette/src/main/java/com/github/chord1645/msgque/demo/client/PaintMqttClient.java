package com.github.chord1645.msgque.demo.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.chord1645.msgque.demo.Topics;
import com.github.chord1645.msgque.demo.model.BaseData;
import com.github.chord1645.msgque.demo.model.PaintData;
import com.github.chord1645.msgque.demo.ui.Apoint;
import com.github.chord1645.msgque.demo.ui.PaintFrame;
import io.moquette.proto.messages.AbstractMessage;
import io.moquette.proto.messages.PublishMessage;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.msgpack.MessagePack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PaintMqttClient implements IPaintClient {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    IMqttClient iclient;
    MqttClientPersistence dataStore;
    //    String topic = "/painter";
    String roomId;

    public PaintMqttClient() {
        paintFrame = new PaintFrame("画图程序", this);
    }

    public static void main(String[] args) throws Exception {
        new PaintMqttClient().connect("tcp://localhost:9999");
    }

    private void connect(String url) throws MqttException {
        String tmpDir = System.getProperty("java.io.tmpdir");
        dataStore = new MqttDefaultFilePersistence(tmpDir + File.separator + "publisher");
        iclient = new MqttClient(url, System.currentTimeMillis() + "", dataStore);
        iclient.setCallback(new TestCallback());
        iclient.connect();
//        iclient.disconnect();
//        iclient.subscribe(Topics.C_PAINT, qos);
//        iclient.subscribe(Topics.S_JOIN, qos);
//        iclient.subscribe(Topics.C_PING, qos);
//        MqttMessage mqttMessage = new MqttMessage("ping".getBytes());
//        mqttMessage.setQos(qos);
//        iclient.publish(Topics.C_PING, mqttMessage);
    }

    PaintFrame paintFrame;


    List<Apoint> list = new ArrayList<>();
    MessagePack messagePack = new MessagePack();
    int qos = 1;

    @Override
    public void clearCache() {
        list.clear();
    }


    @Override
    public void flushCache() {
        try {
            if (roomId == null)
                return;
            list.add(new Apoint(-1, -1, 6));
            PaintData paintData = new PaintData();
            paintData.setData(list);
            MqttMessage mqttMessage = new MqttMessage(messagePack.write(paintData));
            mqttMessage.setQos(qos);
            iclient.publish(Topics.paint(roomId), mqttMessage);
        } catch (Exception e) {
            logger.error("flushCache ex", e);
        }

    }

    @Override
    public void append(Apoint apoint) {
        list.add(apoint);
    }

    @Override
    public boolean join(String room, String player) {
        try {
            this.roomId =room;
            iclient.subscribe(Topics.room(room), qos);
            iclient.subscribe(Topics.paint(room), qos);
            JSONObject obj = JSON.parseObject("{}");
            obj.put("room", room);
            obj.put("player", player);
            MqttMessage mqttMessage = new MqttMessage(obj.toJSONString().getBytes("utf-8"));
            mqttMessage.setQos(qos);
            iclient.publish(Topics.S_JOIN, mqttMessage);
            return true;
        } catch (MqttException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean quit(String room, String player)   {
        try {
            iclient.unsubscribe(Topics.paint(room));
            iclient.unsubscribe(Topics.room(room));
            paintFrame.clearRoom();
            return true;
        } catch (MqttException e) {
            e.printStackTrace();
        }
        return false;
    }

    class TestCallback implements MqttCallback {

        private boolean m_connectionLost = false;

        @Override
        public void connectionLost(Throwable cause) {
            m_connectionLost = true;
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            logger.info("messageArrived:{}", topic);
            try {
                if (topic.startsWith(Topics.C_PAINT)) {
                    PaintData paintData = messagePack.read(message.getPayload(), PaintData.class);
                    logger.info("tranport topic={} len={} cost : {}ms", topic, paintData.getData().size(), System.currentTimeMillis() - paintData.getDate().getTime());
                    paintFrame.addData(paintData.getData());
                } else if (topic.startsWith(Topics.S_JOIN)) {
//                    System.out.println(new String(message.getPayload().clone()));
//                    JSONObject json = JSON.parseObject(new String(message.getPayload().clone()));
//                    System.out.println(json);
                    BaseData data = messagePack.read(message.getPayload(), BaseData.class);
                    paintFrame.refleshRoom(data.getData());
                }
            } catch (Throwable ex) {
                logger.error("messageArrived ex", ex);
            }


        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            logger.info("deliveryComplete:{}", token);
        }
    }

}