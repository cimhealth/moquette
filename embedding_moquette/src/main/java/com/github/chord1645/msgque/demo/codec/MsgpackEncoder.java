package com.github.chord1645.msgque.demo.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.msgpack.MessagePack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MsgpackEncoder extends MessageToByteEncoder<Object> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) {
        try {
            MessagePack messagePack = new MessagePack();
            byte[] bytes = messagePack.write(msg);
            out.writeBytes(bytes);
        } catch (Throwable e) {
            logger.error("", e);
        }

    }
}