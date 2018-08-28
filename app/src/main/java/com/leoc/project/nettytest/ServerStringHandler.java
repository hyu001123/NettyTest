package com.leoc.project.nettytest;

import android.util.Log;
import android.widget.Toast;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by Administrator on 2018/8/27.
 */

public class ServerStringHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Log.i("tag","channel=="+ctx.channel().toString());
        Log.i("tag","msg="+msg.toString());
        System.err.println("server:" + msg.toString());
        ctx.writeAndFlush(msg.toString() + "你好");
        if (msg.toString().equals("心跳包")) {
            Log.i("tag","心跳包工作");
            ctx.writeAndFlush(msg.toString());
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        Log.i("tag","重启服务器");
        new ImServer().init(2222);
    }
}
