package server;

import server.config.ServerConfig;
import server.console.ServerConsole;  // Добавьте этот импорт
import server.handler.CollectionManager;
import server.handler.CommandHandler;
import server.handler.FileManager;
import server.network.ConnectionAcceptor;
import server.util.ShutdownHook;

import java.io.IOException;
import java.util.List;

//Точка входа серверного приложения

public class ServerApp {

    private static CollectionManager collectionManager;
    private static FileManager fileManager;

    //Главная точка входа сервера
    public static void main(String[] args) {
        System.out.println("SERVER APPLICATION - LAB 6");

        try {
            // 1. Загрузка конфигурации
            ServerConfig config = new ServerConfig();
            System.out.println("Конфигурация загружена");
            System.out.println("Файл: " + config.getFileName());
            System.out.println("Порт: " + config.getPort());

            // 2. Инициализация менеджеров
            fileManager = new FileManager(config.getFileName());
            collectionManager = new CollectionManager();

            // 3. Загрузка коллекции из файла
            List<common.model.HumanBeing> loaded = fileManager.load();
            loaded.forEach(collectionManager::add);

            // 4. Инициализация генератора ID после загрузки
            collectionManager.initializeIdGenerator(loaded);
            System.out.println("Загружено элементов: " + loaded.size());
            System.out.println("Следующий ID: " + (loaded.size() > 0 ?
                    loaded.stream().mapToLong(common.model.HumanBeing::getId).max().orElse(0) + 1 : 1));

            // 5. Создание обработчика команд
            CommandHandler commandHandler = new CommandHandler(collectionManager, fileManager);

            // 6. Запуск серверной консоли в отдельном потоке
            Thread consoleThread = new Thread(new ServerConsole(commandHandler));
            consoleThread.setDaemon(true); // Завершится вместе с основным потоком
            consoleThread.start();

            // 7. Регистрация hook для автосохранения при завершении
            ShutdownHook.register(collectionManager, fileManager);

            System.out.println("\nЗапуск сетевого сервера...");
            ConnectionAcceptor acceptor = new ConnectionAcceptor(config.getPort(), commandHandler);
            acceptor.run();

        } catch (IllegalStateException e) {
            System.err.println("Ошибка конфигурации: " + e.getMessage());
            System.err.println("Убедитесь, что переменная окружения HUMAN_BEING_FILE задана");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Ошибка ввода-вывода: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Неожиданная ошибка: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
