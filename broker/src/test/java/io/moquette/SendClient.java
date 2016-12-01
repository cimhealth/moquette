package io.moquette;

import io.moquette.proto.messages.AbstractMessage;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

public class SendClient {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    IMqttClient iclient;
    MqttClientPersistence dataStore;

    public SendClient() {

    }

    static class TestCase extends Thread {
        SendClient client = new SendClient();

        public TestCase() throws MqttException {
            client.connect("tcp://localhost:8999");
        }

        @Override
        public void run() {
            for (int i = 0; i < 20; i++) {
                try {
                    client.sendData();
                    TimeUnit.MILLISECONDS.sleep(500);
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
        for (int i = 0; i < 10; i++) {
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

    public void sendData() {
        try {
            String s = "fwaewfffawefawefawefawefawefawefawefawefawefawefawefaefawefawefawef";
            MqttMessage mqttMessage = new MqttMessage(s.getBytes("utf-8"));
            mqttMessage.setRetained(false);
            mqttMessage.setQos(AbstractMessage.QOSType.MOST_ONE.byteValue());
            iclient.publish("S_TEST_BYTE", mqttMessage);
        } catch (MqttException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}