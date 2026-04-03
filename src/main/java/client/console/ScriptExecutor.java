package client.console;

import client.network.NetworkClient;
import client.handler.ResponseHandler;
import common.commands.CommandType;
import common.model.HumanBeing;
import common.request.Request;
import common.response.Response;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

public class ScriptExecutor {

    private final ConsoleReader consoleReader;
    private final NetworkClient networkClient;
    private final ResponseHandler responseHandler;

    // Защита от рекурсивных вызовов
    private final Deque<String> scriptStack = new ArrayDeque<>();
    private static final int MAX_SCRIPT_DEPTH = 5;

    public ScriptExecutor(ConsoleReader consoleReader, NetworkClient networkClient, ResponseHandler responseHandler) {
        this.consoleReader = consoleReader;
        this.networkClient = networkClient;
        this.responseHandler = responseHandler;
    }

    //Выполняет скрипт из файла
    public boolean executeScript(String fileName) {

        if (scriptStack.contains(fileName)) {
            System.err.println("Обнаружена рекурсивная ссылка на скрипт: " + fileName);
            return false;
        }

        if (scriptStack.size() >= MAX_SCRIPT_DEPTH) {
            System.err.println("Превышена максимальная глубина вложенности скриптов (" + MAX_SCRIPT_DEPTH + ")");
            return false;
        }

        File file = new File(fileName);
        if (!file.exists()) {
            System.err.println("Файл не найден: " + fileName);
            return false;
        }

        if (!file.canRead()) {
            System.err.println("Нет прав на чтение файла: " + fileName);
            return false;
        }

        scriptStack.push(fileName);
        System.out.println("Начало выполнения скрипта: " + fileName);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();

                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                System.out.println("[" + lineNumber + "] " + line);

                executeCommand(line);
            }

            System.out.println("Завершение выполнения скрипта: " + fileName);
            return true;

        } catch (IOException e) {
            System.err.println("Ошибка чтения скрипта: " + e.getMessage());
            return false;
        } finally {
            scriptStack.pop();
        }
    }

    //Выполняет одну команду из скрипта
    private void executeCommand(String line) {
        String[] tokens = line.split("\\s+", 2);
        String cmdName = tokens[0].toLowerCase();

        try {
            CommandType type = CommandType.valueOf(cmdName.toUpperCase());

            if (type.isServerOnly()) {
                System.err.println("Команда '" + type + "' доступна только на сервере, пропускается");
                return;
            }

            if (type == CommandType.EXIT) {
                System.out.println("Команда 'exit' в скрипте игнорируется");
                return;
            }

            //Проверка на execute_script (рекурсивный вызов)
            if (type == CommandType.EXECUTE_SCRIPT) {
                if (tokens.length > 1) {
                    String scriptFile = tokens[1].trim();
                    executeScript(scriptFile);
                } else {
                    System.err.println("execute_script требует имя файла");
                }
                return;
            }

            if (type.requiresData()) {
                executeComplexCommand(type, tokens);
                return;
            }

            String[] args;
            if (tokens.length > 1) {
                args = new String[]{tokens[1]};
            } else {
                args = new String[0];
            }
            Request request = new Request(type, args);
            Response response = networkClient.sendRequest(request);
            responseHandler.handle(response);

        } catch (IllegalArgumentException e) {
            System.err.println("Неизвестная команда: " + cmdName);
        } catch (IOException e) {
            System.err.println("Ошибка связи с сервером: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Ошибка выполнения: " + e.getMessage());
        }
    }

    //Выполняет сложную команду с интерактивным вводом данных
    private void executeComplexCommand(CommandType type, String[] tokens) {
        try {
            String[] args;
            if (tokens.length > 1) {
                args = new String[]{tokens[1]};
            } else {
                args = new String[0];
            }

            Long existingId = null;
            if (type == CommandType.UPDATE && args.length > 0) {
                existingId = Long.parseLong(args[0].trim());
            }

            InputHelper inputHelper = consoleReader.getInputHelper();
            HumanBeing human = inputHelper.readHumanBeing(existingId);

            Request request = new Request(type, args, human);
            Response response = networkClient.sendRequest(request);
            responseHandler.handle(response);

        } catch (NumberFormatException e) {
            System.err.println("Ошибка парсинга аргумента: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Ошибка связи с сервером: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Ошибка выполнения: " + e.getMessage());
        }
    }
}