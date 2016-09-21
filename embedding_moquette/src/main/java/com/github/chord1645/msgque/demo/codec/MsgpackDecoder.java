package com.github.chord1645.msgque.demo.codec;

import com.github.chord1645.msgque.demo.model.PaintData;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.msgpack.MessagePack;

import java.io.IOException;
import java.util.List;

public class MsgpackDecoder extends MessageToMessageDecoder<ByteBuf> { // (1)

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws IOException { // (2)
        int length = in.readableBytes();
        byte[] bytes = new byte[length];
        in.getBytes(in.readerIndex(), bytes, 0, length);
        MessagePack messagePack = new MessagePack();
        out.add(messagePack.read(bytes,PaintData.class));

    }
}