/*
 * Copyright (c) 2012-2015 The original author or authors
 * ------------------------------------------------------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */
package io.moquette.spi;

import java.io.Serializable;
import java.nio.ByteBuffer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.moquette.proto.messages.AbstractMessage;

import java.util.Collection;
import java.util.List;

/**
 * Defines the SPI to be implemented by a StorageService that handle persistence of messages
 */
public interface IMessagesStore {

    @JsonIgnoreProperties(ignoreUnknown = true, value = {"message"})
    class StoredMessage implements Serializable {
        AbstractMessage.QOSType qos;
        byte[] payload;
        String topic;
        private boolean retained;
        private String clientID;
        //Optional attribute, available only fo QoS 1 and 2
        private Integer messageID;
        private String guid;

        public StoredMessage() {
        }

        public void setQos(AbstractMessage.QOSType qos) {
            this.qos = qos;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }

        public boolean isRetained() {
            return retained;
        }

        public void setRetained(boolean retained) {
            this.retained = retained;
        }

        public String getClientID() {
            return clientID;
        }

        public void setClientID(String clientID) {
            this.clientID = clientID;
        }

        public Integer getMessageID() {
            return messageID;
        }

        public void setMessageID(Integer messageID) {
            this.messageID = messageID;
        }

        public StoredMessage(byte[] message1, AbstractMessage.QOSType qos, String topic) {
            this.qos = qos;
            this.payload = message1;
            this.topic = topic;
        }

        public void setPayload(byte[] payload) {
            this.payload = payload;
        }

        public AbstractMessage.QOSType getQos() {
            return qos;
        }

        public ByteBuffer getPayload() {
            return (ByteBuffer) ByteBuffer.allocate(payload.length).put(payload).flip();
        }

        public String getTopic() {
            return topic;
        }

        public void setGuid(String guid) {
            this.guid = guid;
        }

        public String getGuid() {
            return guid;
        }


        public ByteBuffer getMessage() {
            return ByteBuffer.wrap(payload);
        }

        @Override
        public String toString() {
            return "PublishEvent{" +
                    "m_msgID=" + messageID +
                    ", clientID='" + clientID + '\'' +
                    ", m_retain=" + retained +
                    ", m_qos=" + qos +
                    ", m_topic='" + topic + '\'' +
                    '}';
        }
    }

    /**
     * Used to initialize all persistent store structures
     */
    void initStore();

    /**
     * Persist the message.
     * If the message is empty then the topic is cleaned, else it's stored.
     */
    void storeRetained(String topic, String guid);

    /**
     * Return a list of retained messages that satisfy the condition.
     */
    Collection<StoredMessage> searchMatching(IMatchingCondition condition);

    /**
     * Persist the message.
     *
     * @return the unique id in the storage (guid).
     */
    String storePublishForFuture(StoredMessage evt);

    /**
     * Return the list of persisted publishes for the given clientID.
     * For QoS1 and QoS2 with clean session flag, this method return the list of
     * missed publish events while the client was disconnected.
     */
    List<StoredMessage> listMessagesInSession(Collection<String> guids);

    void dropMessagesInSession(String clientID);

    StoredMessage getMessageByGuid(String guid);

    void cleanRetained(String topic);
}
