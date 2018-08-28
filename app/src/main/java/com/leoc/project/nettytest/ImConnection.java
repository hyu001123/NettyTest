package com.leoc.project.nettytest;

import android.util.Log;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import static android.content.ContentValues.TAG;

/**
 * Created by Administrator on 2018/8/27.
 */

public class ImConnection {
    private Channel channel;
    private Bootstrap b;
    private OnServerConnectListener onServerConnectListener;

    public Channel connect(InetSocketAddress address,OnServerConnectListener listener) {
        doConnect(address,listener);
        return this.channel;
    }
    private void doConnect(InetSocketAddress address,OnServerConnectListener onServerConnectListener) {
        this.onServerConnectListener=onServerConnectListener;
        if(b==null) {
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                b = new Bootstrap();
                b.group(workerGroup);
                b.channel(NioSocketChannel.class);
                b.option(ChannelOption.SO_KEEPALIVE, true);
                b.option(ChannelOption.SO_REUSEADDR, true);
                b.handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast("ping", new IdleStateHandler(60, 20, 60 * 10, TimeUnit.SECONDS));
                        ch.pipeline().addLast("decoder", new StringDecoder());
                        ch.pipeline().addLast("encoder", new StringEncoder());
                        ch.pipeline().addLast(new ClientStringHandler());
                    }
                });
                ChannelFuture f = b.connect(address);
                f.addListener(mConnectFutureListener);
                channel = f.channel();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Channel mChannel;
    private ChannelFutureListener mConnectFutureListener = new ChannelFutureListener() {
        @Override
        public void operationComplete(ChannelFuture pChannelFuture) throws Exception {
            if (pChannelFuture.isSuccess()) {
                mChannel = pChannelFuture.channel();
                if (onServerConnectListener != null) {
                    onServerConnectListener.onConnectSuccess();
                }
                Log.i(TAG, "operationComplete: connected!");
            } else {
                if (onServerConnectListener != null) {
                    onServerConnectListener.onConnectFailed();
                }
                Log.i(TAG, "operationComplete: connect failed!");
            }
        }
    };

    public interface OnServerConnectListener {
        void onConnectSuccess();
        void onConnectFailed();
    }
}
