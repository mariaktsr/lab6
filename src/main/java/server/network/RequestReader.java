package server.network;

import common.request.Request;
import common.util.SerializationHelper;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

//Модуль чтения запросов от клиентов
//(десериализует объекты Request из сетевого канала)

public class RequestReader {

    private static final int BUFFER_SIZE = 8192;

    public Request read(SocketChannel channel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

        //Читаем данные из канала
        int bytesRead = channel.read(buffer);

        if (bytesRead == -1) {
            //Клиент закрыл соединение
            return null;
        }

        if (bytesRead == 0) {
            //Нет данных для чтения
            return null;
        }

        buffer.flip();
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);

        try {
            return SerializationHelper.fromBytes(data, Request.class);
        } catch (ClassNotFoundException e) {
            System.err.println("Неизвестный класс при десериализации: " + e.getMessage());
            return null;
        }
    }
}