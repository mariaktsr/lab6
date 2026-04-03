package client;

import client.console.ConsoleReader;
import client.console.ScriptExecutor;
import client.handler.ResponseHandler;
import client.network.NetworkClient;
import common.commands.CommandType;
import common.request.Request;
import common.response.Response;

import java.io.IOException;
import java.util.Scanner;

//Точка входа клиентского приложения

public class ClientApp {

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 12345;

    public static void main(String[] args) {
        System.out.println("CLIENT APPLICATION");

        //Чтение конфигурации из переменных окружения
        String host = System.getenv("SERVER_HOST");
        String portStr = System.getenv("SERVER_PORT");

        if (host == null || host.trim().isEmpty()) {
            host = DEFAULT_HOST;
        }

        int port = DEFAULT_PORT;
        if (portStr != null && !portStr.trim().isEmpty()) {
            try {
                port = Integer.parseInt(portStr.trim());
            } catch (NumberFormatException e) {
                System.err.println("Неверный порт: " + portStr + ", используется порт по умолчанию: " + DEFAULT_PORT);
            }
        }

        System.out.println("Сервер: " + host + ":" + port);

        NetworkClient networkClient = new NetworkClient(host, port);
        ConsoleReader consoleReader = new ConsoleReader(new Scanner(System.in));
        ResponseHandler responseHandler = new ResponseHandler();
        ScriptExecutor scriptExecutor = new ScriptExecutor(consoleReader, networkClient, responseHandler);

        if (!networkClient.connect()) {
            System.err.println("Не удалось подключиться к серверу");
            System.err.println("Убедитесь, что сервер запущен и доступен");
            System.exit(1);
        }

        System.out.println("\nПодключено к серверу");
        System.out.println("Введите 'help' для списка команд");
        System.out.println("Введите 'exit' для завершения\n");

        while (true) {
            try {
                Request request = consoleReader.readCommand();

                if (request == null) {
                    continue;
                }

                if (request.getCommandType() == CommandType.EXIT) {
                    System.out.println("Завершение работы клиента...");
                    networkClient.disconnect();
                    break;
                }

                if (request.getCommandType() == CommandType.EXECUTE_SCRIPT) {
                    String[] commandArgs = request.getArguments();
                    if (commandArgs == null || commandArgs.length == 0) {
                        System.err.println("Команда 'execute_script' требует имя файла");
                        continue;
                    }
                    String scriptFile = commandArgs[0].trim();
                    scriptExecutor.executeScript(scriptFile);
                    continue;
                }

                Response response = networkClient.sendRequest(request);

                responseHandler.handle(response);

            } catch (IOException e) {
                System.err.println("Ошибка связи с сервером: " + e.getMessage());
                System.err.println("Попробуйте повторить команду позже");

                System.out.print("Попробовать снова? (y/n): ");
                String choice = new Scanner(System.in).nextLine().trim().toLowerCase();

                if (!choice.equals("y") && !choice.equals("yes")) {
                    networkClient.disconnect();
                    break;
                }
            } catch (Exception e) {
                System.err.println("Неожиданная ошибка: " + e.getMessage());
                e.printStackTrace();
            }
        }

        System.out.println("Клиент завершил работу");
    }
}