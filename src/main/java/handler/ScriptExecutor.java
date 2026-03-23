package handler;

import command.invoker.CommandInvoker;
import request.Request;
import request.Response;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ScriptExecutor {

    private final CollectionManager collectionManager;
    private final FileManager fileManager;
    private CommandInvoker invoker;
    private CliHandler cliHandler;  // Ссылка на CliHandler для интерактивного ввода
    private final Set<String> executingScripts = new HashSet<>();

    public ScriptExecutor(CollectionManager collectionManager, FileManager fileManager) {
        this.collectionManager = collectionManager;
        this.fileManager = fileManager;
    }

    public void setInvoker(CommandInvoker invoker) {
        this.invoker = invoker;
    }
    public void setCliHandler(CliHandler cliHandler) {
        this.cliHandler = cliHandler;
    }

    public void executeScript(String fileName) throws IOException {
        String absolutePath = new File(fileName).getAbsolutePath();

        if (executingScripts.contains(absolutePath)) {
            System.err.println("Ошибка: рекурсивный вызов скрипта: " + fileName);
            return;
        }

        executingScripts.add(absolutePath);
        System.out.println("Выполнение скрипта: " + fileName);

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String trimmed = line.trim();

                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }

                try {
                    String[] tokens = trimmed.split("\\s+", 2);
                    String commandName = tokens[0].toLowerCase();

                    String[] args;
                    if (tokens.length > 1) {
                        args = new String[]{tokens[1].trim()};
                    } else {
                        args = new String[0];
                    }

                    String message = "[" + lineNumber + "] " + commandName;
                    if (args.length > 0) {
                        message = message + " " + args[0];
                    }
                    System.out.println(message);

                    Request request;
                    if (cliHandler != null &&
                            (commandName.equals("add") ||
                                    commandName.equals("update") ||
                                    commandName.equals("insert_at"))) {

                        request = cliHandler.parseRequestForScript(commandName, args);
                    } else {
                        request = new Request(commandName, args);
                    }

                    executeRequest(request);

                } catch (Exception e) {
                    System.err.println("Ошибка на строке " + lineNumber + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } finally {
            executingScripts.remove(absolutePath);
        }

        System.out.println("Скрипт завершён");
    }

    private void executeRequest(Request request) {
        if (invoker == null) {
            System.err.println("Ошибка: invoker не установлен!");
            return;
        }

        Response response = invoker.execute(request);

        if (response != null && response.getMessage() != null && !response.getMessage().isEmpty()) {
            String prefix = response.isSuccess() ? "  Успех:" : "  Ошибка:";
            System.out.println(prefix + " " + response.getMessage());
        }
    }
}