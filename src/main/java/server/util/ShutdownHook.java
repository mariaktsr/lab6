package server.util;

import server.handler.CollectionManager;
import server.handler.FileManager;

import java.io.IOException;

//Утилитный класс для регистрации hook завершения
//(используется для автосохранения коллекции)

public class ShutdownHook {

    private ShutdownHook() {
    }

    //Регистрирует hook для автосохранения
    public static void register(CollectionManager collectionManager, FileManager fileManager) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nПолучен сигнал завершения...");

            if (collectionManager != null && fileManager != null) {
                try {
                    System.out.println("Автосохранение коллекции...");
                    fileManager.save(collectionManager.getCollection());
                    System.out.println("Коллекция сохранена");
                } catch (IOException e) {
                    System.err.println("Ошибка при сохранении: " + e.getMessage());
                }
            }

            System.out.println("До свидания!");
        }));
    }
}