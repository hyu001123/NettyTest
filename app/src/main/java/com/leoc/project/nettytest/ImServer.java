package com.leoc.project.nettytest;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * Created by Administrator on 2018/8/27.
 */

public class ImServer {

    private static ImServer INSTANCE;
    private boolean isInit;
    private ChannelFuture channelFuture;
    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workerGroup;

    public static ImServer getInstance() {
        if (INSTANCE == null) {
            synchronized (ImServer.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ImServer();
                }
            }
        }
        return INSTANCE;
    }

    public void init(int port) {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast("decoder", new StringDecoder());
                ch.pipeline().addLast("encoder", new StringEncoder());
                ch.pipeline().addLast(new ServerStringHandler());
            }
        });
        bootstrap.option(ChannelOption.SO_BACKLOG, 128);
        bootstrap.option(ChannelOption.SO_REUSEADDR, true);
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);

            channelFuture = bootstrap.bind(port);
            isInit=true;

    }

    public void shutDown(){
        if (channelFuture != null && channelFuture.isSuccess()) {
            isInit = false;
            channelFuture.channel().closeFuture();
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
