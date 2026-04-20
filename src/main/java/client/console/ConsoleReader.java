package client.console;

import client.network.NetworkClient;
import client.validation.DynamicValidationFactory;
import client.validation.Validation;
import common.commands.CommandType;
import common.model.CommandDescriptor;
import common.model.HumanBeing;
import common.request.Request;
import common.response.Response;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

//Чтения команд из консоли

public class ConsoleReader {

    private final Scanner scanner;
    private final InputHelper inputHelper;
    private final DynamicValidationFactory validationFactory;

    //Хранит метаданные команд, полученные от сервера
    private Map<String, CommandDescriptor> commandDescriptors = new HashMap<>();

    public ConsoleReader(Scanner scanner) {
        this.scanner = scanner;
        this.inputHelper = new InputHelper(scanner);
        this.validationFactory = new DynamicValidationFactory();
    }

    //Запрашивает у сервера метаданные команд (рукопожатие)
    //Вызывается один раз сразу после подключения
    public boolean fetchCommandMetadata(NetworkClient networkClient) {
        try {
            // Используем специальную команду для запроса метаданных
            Request bootstrap = new Request(CommandType.GET_COMMANDS_METADATA, new String[0]);
            Response response = networkClient.sendRequest(bootstrap);

            if (response.isSuccess() && response.getData(Map.class) != null) {
                @SuppressWarnings("unchecked")
                Map<String, CommandDescriptor> received = (Map<String, CommandDescriptor>) response.getData(Map.class);
                this.commandDescriptors = received;
                System.out.println("Синхронизация команд завершена. Доступно: " + commandDescriptors.size());
                return true;
            } else {
                System.err.println("Не удалось получить метаданные команд: " + response.getMessage());
                return false;
            }
        } catch (Exception e) {
            System.err.println("Ошибка рукопожатия: " + e.getMessage());
            return false;
        }
    }

    public Request readCommand() {
        System.out.print("\n> ");

        if (!scanner.hasNextLine()) {
            System.out.println("\nВвод завершён. Завершение работы...");
            return new Request(CommandType.EXIT, new String[0]);
        }

        String line = scanner.nextLine().trim();

        if (line.isEmpty()) {
            return null;
        }

        String[] tokens = line.split("\\s+", 2);
        String cmdName = tokens[0].toLowerCase();

        //проверяем дескриптор от сервера
        CommandDescriptor descriptor = commandDescriptors.get(cmdName);

        if (descriptor == null) {
            // Fallback на локальный enum для обратной совместимости
            try {
                CommandType type = CommandType.valueOf(cmdName.toUpperCase());
                if (type.isServerOnly()) {
                    System.err.println("Команда '" + cmdName + "' доступна только на сервере");
                    return null;
                }
                return processFallback(type, tokens);
            } catch (IllegalArgumentException e) {
                System.err.println("Неизвестная команда: " + cmdName);
                return null;
            }
        }

        // 2. Работаем по новой архитектуре
        if (descriptor.isServerOnly()) {
            System.err.println("Команда '" + cmdName + "' доступна только на сервере");
            return null;
        }

        String[] args = tokens.length > 1 ? new String[]{tokens[1]} : new String[0];

        // Валидация через динамическую фабрику
        Validation<String[]> validator = validationFactory.createValidator(descriptor);
        Validation.ValidationError error = validator.validate(args);
        if (error != null) {
            System.err.println(error.getMessage());
            return null;
        }

        // Формируем Request
        CommandType type;
        try {
            type = CommandType.valueOf(cmdName.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.err.println("Команда '" + cmdName + "' отсутствует в локальном клиенте. " +
                    "Для полной поддержки обновите клиент или добавьте команду в CommandType.");
            return null;
        }

        if (descriptor.isRequiresData()) {
            return readComplexCommand(type, args);
        }

        return new Request(type, args);
    }

    private Request processFallback(CommandType type, String[] tokens) {
        String[] args = tokens.length > 1 ? new String[]{tokens[1]} : new String[0];
        if (type.requiresData()) {
            return readComplexCommand(type, args);
        }
        return new Request(type, args);
    }

    //Читает сложные команды с вводом данных HumanBeing
    private Request readComplexCommand(CommandType type, String[] args) {
        try {
            Long existingId = null;
            if (type == CommandType.UPDATE && args.length > 0) {
                existingId = Long.parseLong(args[0].trim());
            } else if (type == CommandType.INSERT_AT && args.length > 0) {
                int index = Integer.parseInt(args[0].trim());
                if (index < 0) {
                    System.err.println("Индекс не может быть отрицательным");
                    return null;
                }
                args = new String[]{String.valueOf(index)};
            }

            System.out.println("\nВвод данных элемента:");
            HumanBeing human = inputHelper.readHumanBeing(existingId);

            return new Request(type, args, human);

        } catch (NumberFormatException e) {
            System.err.println("Ошибка парсинга аргумента: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("Ошибка ввода: " + e.getMessage());
            return null;
        }
    }

    public InputHelper getInputHelper() {
        return inputHelper;
    }

    // Геттеры для внешних модулей (например, ScriptExecutor)
    public Map<String, CommandDescriptor> getCommandDescriptors() {
        return commandDescriptors;
    }
}