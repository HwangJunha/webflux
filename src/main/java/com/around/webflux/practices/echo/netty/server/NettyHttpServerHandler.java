package com.around.webflux.practices.echo.netty.server;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.nio.charset.StandardCharsets;

public class NettyHttpServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof FullHttpRequest){
            var request = (FullHttpRequest)msg;
            var response = new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.OK);

            response.headers().set("Content-Type", "text/plain");
            response.content().writeCharSequence("Hello, world!", StandardCharsets.UTF_8);
            ctx.writeAndFlush(response)
                    .addListener(ChannelFutureListener.CLOSE);
        }
    }
}