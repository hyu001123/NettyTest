package com.leoc.project.nettytest;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoop;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * Created by Administrator on 2018/8/27.
 */

public class ClientStringHandler extends ChannelInboundHandlerAdapter {
    private ImConnection imConnection=new ImConnection();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.println("client:" + msg.toString());
        Log.i("tag","clientMsg="+msg.toString());
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state().equals(IdleState.READER_IDLE)) {
                System.out.println("长期没收到服务器推送数据");
                Log.i("tag","长期没收到服务器推送数据");
                //可以选择重新连接
            } else if (event.state().equals(IdleState.WRITER_IDLE)) {
                System.out.println("长期未向服务器发送数据");
                Log.i("tag","长期未向服务器发送数据");
                //发送心跳包
                ctx.writeAndFlush("心跳包");
            } else if (event.state().equals(IdleState.ALL_IDLE)) {
                System.out.println("ALL");
                Log.i("tag","ALL");
            }
        }else{
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.err.println("掉线了...");
        //使用过程中断线重连
        final EventLoop eventLoop = ctx.channel().eventLoop();
        eventLoop.schedule(new Runnable() {
            @Override
            public void run() {
                new ImConnection().connect(new InetSocketAddress("127.0.0.1", 2222), new ImConnection.OnServerConnectListener() {
                    @Override
                    public void onConnectSuccess() {
                        Log.i("tag","reconncect_success3");
                    }

                    @Override
                    public void onConnectFailed() {
                        Log.i("tag","reconnect_fail3");
                    }
                });
            }
        }, 1L, TimeUnit.SECONDS);
        super.channelInactive(ctx);
    }

}
