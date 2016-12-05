package io.moquette;

import io.moquette.proto.messages.AbstractMessage;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

public class SubClient {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    IMqttClient iclient;
    MqttClientPersistence dataStore;
    String clientId;

    public SubClient(String clientId) {
        this.clientId = clientId;
    }

    public static void main(String[] args) throws Exception {
        String clientId = "2";
        SubClient client = new SubClient(clientId);
        client.connect("tcp://localhost:8999");
        client.sub();
    }

    private void sub() throws MqttException {
        iclient.subscribe("S_TEST_BYTE", AbstractMessage.QOSType.EXACTLY_ONCE.byteValue(), new IMqttMessageListener() {
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                logger.info("messageArrived:{}", topic);
                try {
                    logger.info("arrived topic={} msg={}", topic, new String(message.getPayload()));
                } catch (Throwable ex) {
                    logger.error("messageArrived ex", ex);
                }

            }
        });
    }

    private void detroy() throws MqttException {
        iclient.disconnect();
        iclient.close();
    }

    /**
     *
     * @param url
     * @throws MqttException
     */
    private void connect(String url) throws MqttException {
        String tmpDir = System.getProperty("java.io.tmpdir");
        dataStore = new MqttDefaultFilePersistence(tmpDir + File.separator + "send");
        iclient = new MqttClient(url, clientId, dataStore);
//        iclient.setCallback(new TestCallback());
        MqttConnectOptions options=new MqttConnectOptions();
        options.setCleanSession(true);//如果设置为false会接收错过的消息
//        options.setKeepAliveInterval(10);
//        options.setConnectionTimeout(3);
        iclient.connect(options);
    }

}