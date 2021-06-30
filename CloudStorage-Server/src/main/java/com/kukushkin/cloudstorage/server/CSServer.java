package com.kukushkin.cloudstorage.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class CSServer {

    public static void main(String[] args) {
        //создать пул потоков для обработки данных сервером

        //пул потоков, который отвечает за подключающихся клиентов
        EventLoopGroup bossGroup = new NioEventLoopGroup(4);

        //пул потоков для обработки данных, т.е. всего сетевого взаимодействия
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            //выполнить преднастройку сервера
            ServerBootstrap bst = new ServerBootstrap();
                //сообщить серверу, чтобы он использовал для подключающихся клиентов bossGroup,
                //а для обработки данных workerGroup
                bst.group(bossGroup, workerGroup)
                        //канал для подключения клиентов
                        .channel(NioServerSocketChannel.class)
                        //настроить процесс общения с клиентом при каждом его соединении/подключении
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                          @Override //инициализация клиента
                          protected void initChannel(SocketChannel socketChannel) throws Exception{
                            //добавить CSHandler в канал
                            //для каждого канала будет создан свой pipline конвейер
                              socketChannel.pipeline().addLast(new CSHandler());
                          }
                        });

                //Запустить сервер
            ChannelFuture future = bst.bind(8189).sync();
            //bst.bind указывает на то, что сервер должен запуститься на порту 8189, sync - это сам запуск задачи
            //ChannelFuture future - даёт понимание того, что происходит с сервером

            future.channel().closeFuture().sync();
            //closeFuture() - ожидание информации о том, когда остановят сервер - это полностью блокирующая операция
            //до тех пор пока не появится эта информация

        } catch (Exception exception) {
            exception.printStackTrace();

            //в блок finally попадаем, только после того, как кто-то остановил сервер
            //и закрываем ранее запущенные пулы потоков
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();

        }

    }
}
