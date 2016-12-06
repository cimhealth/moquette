package io.moquette;

import io.moquette.proto.messages.AbstractMessage;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;

public class MapdbTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    static File file = new File("D:\\Work\\tmp\\mapdb\\moquette_store.mapdb");
    static DB m_db = DBMaker.newFileDB(file).make();
    static Map<String, String> map = m_db.getHashMap("test");

    public static void main(String[] args) throws Exception {

        for (int i = 0; i < 1000; i++) {
            foo();
        }


    }

    private static void foo() {
        for (int i = 0; i < 10000; i++) {
            map.put(i + "", i + "");
        }
        m_db.commit();
        map.clear();
        m_db.commit();
    }
}