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
            for (int i = 0; i <1; i++) {
                client.sendData();
//                try {
//                    client.sendData();
//                    TimeUnit.SECONDS.sleep(1);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }
            try {
                client.detroy();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }


    private void detroy() throws MqttException {
        iclient.disconnect();
        iclient.close();
    }

    private void connect(String url) throws MqttException {
        String tmpDir = System.getProperty("java.io.tmpdir");
        dataStore = new MqttDefaultFilePersistence(tmpDir + File.separator + "send");
//        iclient = new MqttClient(url,System.currentTimeMillis()+"", dataStore);
        iclient = new MqttClient(url,clientId, dataStore);
//        iclient.setCallback(new TestCallback());
        MqttConnectOptions options=new MqttConnectOptions();
        options.setCleanSession(true);//如果设置为false会接收错过的消息
        iclient.connect(options);
    }
   static int x = 10;
    public void sendData() {
        try {
            String s = (x++)+"__中午啊恶恶法俄文法文法文法文法文法额我发我中午啊恶恶法俄文法文法文法文法文法额我发我中午啊恶恶法俄文法文法文法文法文法额我发我中午啊恶恶法俄文法文法文法文法文法额我发我中午啊恶恶法俄文法文法文法文法文法额我发我中午啊恶恶法俄文法文法文法文法文法额我发我";
            MqttMessage mqttMessage = new MqttMessage(s.getBytes("utf-8"));
//            mqttMessage.setRetained(true);//如设置为true，新订阅者会受到最后一条
            mqttMessage.setId(x);
            mqttMessage.setQos(AbstractMessage.QOSType.EXACTLY_ONCE.byteValue());
            iclient.publish("S_TEST_BYTE", mqttMessage);
        } catch (MqttException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 1; i++) {
            new TestCase().start();
        }
    }
    static  String clientId="pub1";
}
