package com.github.chord1645.msgque.demo.client.handle;

import com.github.chord1645.msgque.demo.model.PaintData;
import com.github.chord1645.msgque.demo.ui.PaintFrame;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaintClientHandler extends ChannelInboundHandlerAdapter {
    int counter;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    PaintFrame paintFrame;

    public PaintClientHandler(PaintFrame paintFrame) {
        this.paintFrame = paintFrame;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("channelActive:{}", ctx.channel());

//        for (int i = 0; i < 10; i++) {
//            PaintData data = new PaintData();
//            data.setCount(i);
//            ctx.write(data);
//        }
//        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        PaintData data = (PaintData) msg;
        logger.info("from server {} data:{}", ctx.channel(), data.getData());
        paintFrame.addData( data.getData());

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("ex", cause);
        ctx.close();
    }
}