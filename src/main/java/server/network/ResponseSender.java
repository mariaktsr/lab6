package server.network;

import common.response.Response;
import common.util.SerializationHelper;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

//Модуль отправки ответов клиентам
//Сериализует объекты Response и отправляет в сетевой канал

public class ResponseSender {

    public void send(SocketChannel channel, Response response) throws IOException {
        byte[] data = SerializationHelper.toBytes(response);
        ByteBuffer buffer = ByteBuffer.wrap(data);

        //Отправляем данные
        while (buffer.hasRemaining()) {
            channel.write(buffer);
        }
    }
}