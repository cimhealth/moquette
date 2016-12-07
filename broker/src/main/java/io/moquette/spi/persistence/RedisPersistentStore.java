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

import io.moquette.proto.MQTTException;
import io.moquette.server.config.IConfig;
import io.moquette.spi.IMessagesStore;
import io.moquette.spi.ISessionsStore;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static io.moquette.BrokerConstants.AUTOSAVE_INTERVAL_PROPERTY_NAME;
import static io.moquette.BrokerConstants.PERSISTENT_STORE_PROPERTY_NAME;

/**
 * MapDB main persistence implementation
 */
public class RedisPersistentStore extends MapDBPersistentStore {
    IConfig props;
    JedisConnectionFactory connectionFactory;
    RedisTemplate redisTemplate;

    public RedisPersistentStore(IConfig props) {
        super(props);
        this.props = props;
        connectionFactory = new JedisConnectionFactory();
        connectionFactory.setHostName(props.getProperty("redis.host"));
        connectionFactory.setPort(Integer.valueOf(props.getProperty("redis.port")));
        connectionFactory.setDatabase(Integer.valueOf(props.getProperty("redis.database")));
        connectionFactory.setTimeout(Integer.valueOf(props.getProperty("redis.timeout")));
        connectionFactory.setUsePool(Boolean.valueOf(props.getProperty("redis.use.pool")));
        connectionFactory.setPoolConfig(new JedisPoolConfig());
        connectionFactory.afterPropertiesSet();
        redisTemplate = new RedisTemplate<String, IMessagesStore.StoredMessage>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer(IMessagesStore.StoredMessage.class));
//        redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
        redisTemplate.afterPropertiesSet();
    }

    @Override
    public IMessagesStore messagesStore() {
        RedisMessagesStore messagesStore = new RedisMessagesStore(props, m_db);
        messagesStore.setRedisTemplate(redisTemplate);
        return messagesStore;
    }
}
