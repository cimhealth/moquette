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
package io.moquette.spi.persistence;

import io.moquette.server.config.IConfig;
import io.moquette.spi.IMatchingCondition;
import io.moquette.spi.IMessagesStore;
import org.mapdb.DB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.util.*;
import java.util.concurrent.ConcurrentMap;

/**
 * IMessagesStore implementation backed by Redis.
 * do not support retained now!
 *
 * @author Scott Lee
 */
public class RedisMessagesStore extends MapDBMessagesStore implements IMessagesStore {

    private static final Logger LOG = LoggerFactory.getLogger(RedisMessagesStore.class);
    private RedisTemplate<String, StoredMessage> redisTemplate;
    IConfig props;

    public RedisTemplate<String, StoredMessage> getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(RedisTemplate<String, StoredMessage> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    //    private StringRedisTemplate m_retainedStore;
//    private DB m_db;
    //maps clientID -> guid
//    private ConcurrentMap<String, String> m_retainedStore;//TODO 发布方设置retain时，订阅者初次订阅会得到最后一次消息
    //maps guid to message, it's message store
//    private ConcurrentMap<String, StoredMessage> m_persistentMessageStore;
    private RedisMessagesStore(DB db) {
        super(db);
    }

    public RedisMessagesStore(IConfig props, DB m_db) {
        this(m_db);
        this.props = props;
    }

    @Override
    public void initStore() {
    }

    @Override
    public void storeRetained(String topic, String guid) {
//        m_retainedStore.put(topic, guid);
//        m_retainedStore.opsForValue().set(topic, guid);
    }

    /**
     * 仅在retained时用
     *
     * @param condition
     */
    @Override
    public Collection<StoredMessage> searchMatching(IMatchingCondition condition) {
        LOG.debug("do not support retained now!");
        return new ArrayList<>();
    }

    @Override
    public String storePublishForFuture(StoredMessage evt) {
        LOG.debug("storePublishForFuture store evt {}", evt);
        if (evt.getClientID() == null) {
            LOG.error("persisting a message without a clientID, bad programming error msg: {}", evt);
            throw new IllegalArgumentException("\"persisting a message without a clientID, bad programming error");
        }
        String guid = UUID.randomUUID().toString();
        evt.setGuid(guid);
        redisTemplate.opsForValue().set(guid, evt);
//        redisTemplate.opsForHash().put(evt.getClientID(), evt.getMessageID(), guid);
        //这里的键值对是：发布者clientId__发布者唯一消息ID->事件唯一ID
        ConcurrentMap<Integer, String> messageIdToGuid = m_db.getHashMap(MapDBSessionsStore.messageId2GuidsMapName(evt.getClientID()));
        messageIdToGuid.put(evt.getMessageID(), guid);

        return guid;
    }

    @Override
    public List<StoredMessage> listMessagesInSession(Collection<String> guids) {
        List<StoredMessage> ret = new ArrayList<>();
        for (String guid : guids) {
            ret.add(redisTemplate.opsForValue().get(guid));
        }
        return ret;
    }

    @Override
    public void dropMessagesInSession(String clientID) {
        //删除的是作为发布者的历史事件
        m_db.delete(MapDBSessionsStore.messageId2GuidsMapName(clientID));
//        redisTemplate.opsForHash().delete(clientID);
//        redisTemplate.remove(clientID); //TODO？？
    }

    @Override
    public StoredMessage getMessageByGuid(String guid) {
        return redisTemplate.opsForValue().get(guid);
    }

    @Override
    public void cleanRetained(String topic) {
//        m_retainedStore.remove(topic);
    }
}
