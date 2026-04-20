package server.network;

import common.request.Request;
import common.response.Response;
import server.handler.CommandHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

//Модуль приёма подключений клиентов
//(использует неблокирующий режим через Selector и ServerSocketChannel)

public class  ConnectionAcceptor {

    private final ServerSocketChannel serverChannel;
    private final Selector selector;
    private final CommandHandler commandHandler;
    private final RequestReader requestReader;
    private final ResponseSender responseSender;

    private volatile boolean running = true;

    //Создаёт акцептор подключений на указанном порту

    public ConnectionAcceptor(int port, CommandHandler commandHandler) throws IOException {
        this.commandHandler = commandHandler;
        this.requestReader = new RequestReader();
        this.responseSender = new ResponseSender();

        //Создание и настройка серверного канала
        this.serverChannel = ServerSocketChannel.open();
        this.serverChannel.configureBlocking(false); // Неблокирующий режим
        this.serverChannel.bind(new InetSocketAddress(port));

        //Создание селектора и регистрация канала
        this.selector = Selector.open();
        this.serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("Сервер запущен на порту " + port);
    }

    //Запускает главный цикл обработки событий
    //Работает в однопоточном режиме
    public void run() {
        System.out.println("Ожидание подключений клиентов...");

        while (running) {
            try {
                // Блокируется, пока не появится событие
                int readyChannels = selector.select();

                if (readyChannels == 0) {
                    continue;
                }

                // Получаем набор готовых ключей
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    keyIterator.remove(); // Обязательно удаляем из набора

                    if (!key.isValid()) {
                        continue;
                    }

                    try {
                        if (key.isAcceptable()) {
                            handleAccept(key);
                        } else if (key.isReadable()) {
                            handleRead(key);
                        } else if (key.isWritable()) {
                            handleWrite(key);
                        }
                    } catch (CancelledKeyException e) {
                        // Клиент отключился, ключ отменён
                        cleanupKey(key);
                    } catch (IOException e) {
                        System.err.println("Ошибка обработки ключа: " + e.getMessage());
                        cleanupKey(key);
                    }
                }

            } catch (IOException e) {
                if (running) {
                    System.err.println("Ошибка селектора: " + e.getMessage());
                }
            }
        }

        shutdown();
    }

    //Обрабатывает новое подключение клиента
    private void handleAccept(SelectionKey key) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = serverChannel.accept();

        if (clientChannel != null) {
            clientChannel.configureBlocking(false); //  Неблокирующий режим
            clientChannel.register(selector, SelectionKey.OP_READ);

            System.out.println("Подключён клиент: " + clientChannel.getRemoteAddress());
        }
    }

    //Обрабатывает чтение запроса от клиента
    private void handleRead(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();

        // Чтение и десериализация запроса
        Request request = requestReader.read(clientChannel);

        if (request == null) {
            // Клиент закрыл соединение
            System.out.println("Клиент отключился: " + clientChannel.getRemoteAddress());
            cleanupKey(key);
            return;
        }

        System.out.println("Получен запрос: " + request.getCommandType());

        // Обработка команды (false = не с серверной консоли)
        Response response = commandHandler.handle(request, false);

        // Подготовка ответа к отправке
        key.attach(response);
        key.interestOps(SelectionKey.OP_WRITE);
    }

    //Обрабатывает отправку ответа клиенту
    private void handleWrite(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        Response response = (Response) key.attachment();

        if (response != null) {
            responseSender.send(clientChannel, response);
            System.out.println("Отправлен ответ: " + (response.isSuccess() ? "OK" : "ERROR"));
        }

        // Возвращаем интерес к чтению
        key.interestOps(SelectionKey.OP_READ);
        key.attach(null);
    }

    //Очищает ресурсы ключа селектора
    private void cleanupKey(SelectionKey key) {
        try {
            SocketChannel channel = (SocketChannel) key.channel();
            if (channel != null && channel.isOpen()) {
                System.out.println("Закрытие соединения: " + channel.getRemoteAddress());
                channel.close();
            }
        } catch (IOException e) {
            //Игнорируем ошибки при закрытии
        }
        key.cancel();
    }

    //Останавливает сервер
    public void stop() {
        running = false;
        selector.wakeup(); // Прерываем select()
    }

    //Завершает работу и освобождает ресурсы
    private void shutdown() {
        System.out.println("Завершение работы сервера...");

        try {
            if (selector != null && selector.isOpen()) {
                selector.close();
            }
            if (serverChannel != null && serverChannel.isOpen()) {
                serverChannel.close();
            }
        } catch (IOException e) {
            System.err.println("Ошибка при закрытии каналов: " + e.getMessage());
        }

        System.out.println("Сервер остановлен");
    }

    //Проверяет, работает ли сервер
    public boolean isRunning() {
        return running;
    }
}