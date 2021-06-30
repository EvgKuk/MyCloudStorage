package com.kukushkin.cloudstorage.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

//Inbound - работа на вход/получение данных
public class CSHandler extends ChannelInboundHandlerAdapter {

    //channelActive - срабатывает, когда клиент подключается
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Клиент подключился");

    }

    //channelRead - канал получения сообщений от клиента
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // ! То, что приходит из сети => приходит в ByteBuffer
        // ! То, что отправляться будет в сеть, также должно быть обёрнуто в ByteBuffer

        ByteBuf buf = (ByteBuf) msg;

        //до тех пор, пока в канале есть непрочитанные байты, будем преобразовывать их в char
        while (buf.readableBytes() > 0){
            System.out.println((char) buf.readByte());
        }

        //после того, как завершили пользование буфером, его необходимо освободить
        buf.release();

    }

    //channelActive - перехват исключения, ошибки выполнения программы
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace(); //напечатать что произошло
        ctx.close();//закрыть соедниение с клиентом
    }
}
