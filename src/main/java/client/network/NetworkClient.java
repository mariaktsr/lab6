package client.network;

import common.request.Request;
import common.response.Response;
import common.util.SerializationHelper;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

//Клиентский модуль для сетевого взаимодействия с сервером
//(использует блокирующий режим)

public class NetworkClient {

    private SocketChannel channel;
    private final String host;
    private final int port;
    private boolean connected = false;

    private static final int BUFFER_SIZE = 8192;

    public NetworkClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    //Подключается к серверу
    public boolean connect() {
        try {
            channel = SocketChannel.open();
            channel.configureBlocking(true); //Блокирующий режим

            InetSocketAddress address = new InetSocketAddress(host, port);

            System.out.println("Подключение к серверу " + host + ":" + port + "...");

            //Блокирующее подключение
            channel.connect(address);

            connected = true;
            System.out.println("Подключено к серверу");
            return true;

        } catch (IOException e) {
            System.err.println("Ошибка подключения: " + e.getMessage());
            System.err.println("Убедитесь, что сервер запущен на порту " + port);
            connected = false;
            return false;
        }
    }

    //Отправляет запрос на сервер и получает ответ
    public Response sendRequest(Request request) throws IOException {
        if (!connected || channel == null || !channel.isConnected()) {
            throw new IOException("Нет подключения к серверу");
        }

        try {
            //Сериализация запроса
            byte[] requestData = SerializationHelper.toBytes(request);

            //Отправка запроса
            ByteBuffer requestBuffer = ByteBuffer.wrap(requestData);
            while (requestBuffer.hasRemaining()) {
                channel.write(requestBuffer);
            }
            System.out.println("Запрос отправлен (" + requestData.length + " байт)");

            //Чтение ответа
            ByteBuffer responseBuffer = ByteBuffer.allocate(BUFFER_SIZE);
            int bytesRead = channel.read(responseBuffer);

            System.out.println("Получено байт: " + bytesRead);

            if (bytesRead == -1) {
                throw new IOException("Сервер закрыл соединение");
            }

            if (bytesRead == 0) {
                System.out.println("Нет данных в ответе");
                return null;
            }

            responseBuffer.flip();
            byte[] responseData = new byte[responseBuffer.remaining()];
            responseBuffer.get(responseData);

            System.out.println("Размер ответа: " + responseData.length + " байт");

            //Десериализация ответа
            try {
                Response response = SerializationHelper.fromBytes(responseData, Response.class);
                System.out.println("Ответ получен: " + (response.isSuccess() ? "OK" : "ERROR"));
                return response;
            } catch (ClassNotFoundException e) {
                System.err.println("Ошибка десериализации: " + e.getMessage());
                e.printStackTrace();
                throw new IOException("Ошибка десериализации ответа", e);
            }

        } catch (IOException e) {
            connected = false;
            throw new IOException("Ошибка связи с сервером: " + e.getMessage(), e);
        }
    }

    //Проверяет, подключён ли клиент
    public boolean isConnected() {
        return connected && channel != null && channel.isConnected();
    }

    //Отключается от сервера
    public void disconnect() {
        if (channel != null && channel.isOpen()) {
            try {
                channel.close();
                System.out.println("Отключено от сервера");
            } catch (IOException e) {
                System.err.println("Ошибка при отключении: " + e.getMessage());
            }
        }
        connected = false;
    }
}