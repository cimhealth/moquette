package io.moquette;

import io.moquette.proto.messages.AbstractMessage;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

public class PubClient {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    IMqttClient iclient;
    MqttClientPersistence dataStore;

    public PubClient() {

    }

    static class TestCase extends Thread {
        PubClient client = new PubClient();

        public TestCase() throws MqttException {
            client.connect("tcp://localhost:8999");
        }

        @Override
        public void run() {
            for (int i = 0; i < 1; i++) {
                try {
                    client.sendData();
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                client.detroy();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 1; i++) {
            new TestCase().start();
        }
    }

    private void detroy() throws MqttException {
        iclient.disconnect();
        iclient.close();
    }

    private void connect(String url) throws MqttException {
        String tmpDir = System.getProperty("java.io.tmpdir");
        dataStore = new MqttDefaultFilePersistence(tmpDir + File.separator + "send");
        iclient = new MqttClient(url, System.currentTimeMillis() + "", dataStore);
//        iclient.setCallback(new TestCallback());
        iclient.connect();
    }
   static int x = 1;
    public void sendData() {
        try {
            String s = (x++)+"";
            MqttMessage mqttMessage = new MqttMessage(s.getBytes("utf-8"));
//            mqttMessage.setRetained(true);//如设置为true，新订阅者会受到最后一条
            mqttMessage.setQos(AbstractMessage.QOSType.EXACTLY_ONCE.byteValue());
            iclient.publish("S_TEST_BYTE", mqttMessage);
        } catch (MqttException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}