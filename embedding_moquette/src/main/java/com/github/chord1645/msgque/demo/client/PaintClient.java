package com.github.chord1645.msgque.demo.client;

import com.github.chord1645.msgque.demo.model.PaintData;
import com.github.chord1645.msgque.demo.client.handle.PaintClientHandler;
import com.github.chord1645.msgque.demo.codec.MsgpackDecoder;
import com.github.chord1645.msgque.demo.codec.MsgpackEncoder;
import com.github.chord1645.msgque.demo.ui.Apoint;
import com.github.chord1645.msgque.demo.ui.PaintFrame;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class PaintClient implements IPaintClient {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static void main(String[] args) throws Exception {
        new PaintClient().connect(9999, "localhost");
    }

    PaintFrame paintFrame;

    public PaintClient() {
        paintFrame = new PaintFrame("画图程序", this);
    }

    public ChannelFuture serverFuture;

    public void connect(int port, String host) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap(); // (1)
            b.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2));
                            ch.pipeline().addLast(new MsgpackDecoder());
                            ch.pipeline().addLast(new LengthFieldPrepender(2));
                            ch.pipeline().addLast(new MsgpackEncoder());
                            ch.pipeline().addLast(new PaintClientHandler(paintFrame));
                        }
                    });

            // Start the client.
            serverFuture = b.connect(host, port).sync(); // (5)
            serverFuture.channel().closeFuture().sync();
            System.currentTimeMillis();
        }catch(Throwable e){
            logger.error("start ex",e);
        } finally {
            group.shutdownGracefully();
        }
    }
    List<Apoint> list = new ArrayList<>();
    public void write(PaintData paintData) {
        serverFuture.channel().writeAndFlush(paintData);
    }

    @Override
    public void clearCache() {
        list.clear();
    }

    @Override
    public void flushCache() {
        list.add(new Apoint(-1, -1, 6));
        PaintData paintData = new PaintData();
        paintData.setData(list);
        this.write(paintData);
    }

    @Override
    public void append(Apoint apoint) {

        list.add(apoint);
    }

    @Override
    public boolean join(String room, String player) {
        return false;
    }

    @Override
    public boolean quit(String room, String player) {
        return false;
    }

}