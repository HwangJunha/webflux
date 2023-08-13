package com.around.webflux.echo.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class NettyHttpServer {
    @SneakyThrows
    public static void main(String[] args){
        EventLoopGroup parentGroup = new NioEventLoopGroup(); //accept 이벤트
        EventLoopGroup childGroup = new NioEventLoopGroup(4); // read 이벤트
        EventExecutorGroup eventExecutorGroup = new DefaultEventLoopGroup(4);

        try{
            ServerBootstrap serverBootstrap =  new ServerBootstrap();
            var server = serverBootstrap
                    .group(parentGroup, childGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline().addLast(
                                    eventExecutorGroup, new LoggingHandler(LogLevel.INFO) //INFO 이상의 로깅들이 추가
                            );
                            ch.pipeline().addLast(
                                    new HttpServerCodec(),
                                    new HttpObjectAggregator(1024*1024), // 1MB 설정
                                    new NettyHttpServerHandler()
                            );
                        }
                    });
            server.bind(8080)
                    .sync()
                    .addListener(new FutureListener<>(){
                        @Override
                        public void operationComplete(Future<Void> future) throws Exception {
                            if(future.isSuccess()){
                                log.info("Success to bind 8080");
                            }else{
                                log.info("Fail to bind 8080");
                            }
                        }
                    }).channel().closeFuture().sync();
        }finally {
            parentGroup.shutdownGracefully();
            childGroup.shutdownGracefully();
            eventExecutorGroup.shutdownGracefully();
        }
    }

}
